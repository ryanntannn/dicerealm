package com.dicerealm.core.dm;

import java.util.ArrayList;

import com.dicerealm.core.combat.managers.MonsterGenerator;
import com.dicerealm.core.locations.Location;
import com.dicerealm.core.locations.LocationGraph;
import com.dicerealm.core.locations.Path;
import com.dicerealm.core.room.RoomState;
import com.dicerealm.core.strategy.JsonSerializationStrategy;
import com.dicerealm.core.strategy.LLMStrategy;

public class DungeonMaster {

	private String systemPrompt(){
		return """
		Act as a Dungeon Master for a multiplayer game of DND. 
		You will be provided with a JSON object containing the state of the players, a summary of the progress so far, and the 10 latest messages. 
		Based on this information, provide a JSON-formatted response that is addressed to the entire room. 

		Your JSON output must include the following keys:
		
			1. displayText
			- A string describing the actions taken by the players, the new state of the room as a result of these actions, and any additional context you wish to convey. 
			- Do not provide any action choices in the displayText.
			- Engage with the party as a whole and not just one player.
			- Guide the players to combat as much as possible within the context of the story.
		
			2. actionChoices
			- A list of objects, each representing an action available to the players. Each object must contain:
				- action: A string describing the action.
					- Provide action for the player to do in Current location.
					- Always include an option for players to engage in combat with the monster.
				- playerId: The ID of the player who can take this action.
				- skillCheck: A JSON object specifying any required skill checks. This object can include keys such as STRENGTH, DEXTERITY, CONSTITUTION, INTELLIGENCE, WISDOM, and CHARISMA with integer values representing the threshold. For skills not needed, set their value to 0.
					- Ensure that any skill checks required are logically tied to the action. For example, use DEXTERITY for lockpicking, CHARISMA for persuasion, etc. If no skill check is needed, set the skill check values to 0.
				- Each player should have at least 3 action choices available.
				- Different players can have the same action choices, but the playerId must be unique for each action choice.
				- Always provide an action choice to allow players to engage in combat with an enemy within context of the story.
				- If there are adjacent locations, include an action choice for the player to move to all adjacent locations.
		
			3. location
			- A UUID representing the current location of the party.
			- When at least half of the party wants to move to a new location, you must set this to the new location's id.
		
			4. contextSummary
			- A string summarizing the current context of the room and adding it to the previous context summary 
			- Include as much infomation as possbile.
			- This summary will be provided back to you in the next turn.

			5. switchToCombatThisTurn
			- If the player's chosen actions is "I want to fight someone", always set this to true.
			- A boolean indicating whether the room should switch to combat mode this turn. 
			- Ensure that the monster created is logically tied to the current location and the context of the story.
			- If true, the room will switch to combat mode and a different system will handle combat
			- If false, the room will remain in dialogue mode.
			- Set this to true if any player chooses to fight an enemy.
		""";
	}

	private String locationPrompt(){
		return """
				Act as a Dungeon Master for a multiplayer game of DND. Come up with a list of locations based on the setting and paths between them.
				Provide a JSON-formatted response that includes the following keys:
				
					1. locations
					- A list of objects, each representing a location. Each object must contain:
						- displayName: A string representing the name of the location.
						- description: A string describing the location in detail.
					- The first location will be the starting point of the adventure.
					- Each location should be unique and have a distinct name and description.
					- The locations should be interconnected in a way that makes sense for the game world.
					- Provide at exactly 6 locations.

					2. Paths
					- A list of objects, each representing a path between two locations. Each object must contain:
						- from: The displayName of the starting location based on the those generated in locations.
						- to: The displayName of the ending location based on the those generated in locations..
						- distance: An integer representing the distance between the two locations.
					- The paths should connect the locations in a logical manner.
					- Ensure al the locations are connected by at least one path.
					- One location can have multiple paths leading to different locations.
				""";
	}

	private String monsterPrompt(){
		return """
				Act as a Dungeon Master for a multiplayer game of DND. Come up with a list of monsters based on the current location.
				Provide a JSON-formatted response that includes the following keys:
				
					1. enemies: A list of objects, each representing an enemy.
					- Each object must contain:
						- name: A string representing the name of the enemy
						- race: A string representing the race of the enemy out of these options: HUMAN, ELF, DEMON, DWARF, TIEFLING
						- entityClass: A string representing the class of the enemy out of these options: WARRIOR, WIZARD, CLERIC, ROGUE, RANGER
					- Each monster should be unique and have a distinct name.
					- The monsters should be interconnected in a way that makes sense for the game world.
				""";
	}

	private LLMStrategy llmStrategy;
	private JsonSerializationStrategy jsonSerializationStrategy;
	private RoomState roomState;
	private String contextSummary;

	public DungeonMaster(LLMStrategy llmStrategy, JsonSerializationStrategy jsonSerializationStrategy, RoomState roomState) {
		this.llmStrategy = llmStrategy;
		this.roomState = roomState;
		this.jsonSerializationStrategy = jsonSerializationStrategy;
		this.contextSummary = "No context summary yet, this is the first turn.";
	}

	public void handleLocationGeneration(String theme) {
		String systemPrompt = locationPrompt();
		String userPrompt = "Generate a list of creative locations for a story set in a " + theme;
		DungeonMasterLocationResponse response = llmStrategy.promptSchema(systemPrompt, userPrompt, DungeonMasterLocationResponse.class); 
		roomState.setLocationGraph(generateLocations(response));
	}

	public void handleMonsterGeneration() {
		String systemPrompt = monsterPrompt();
		String userPrompt = "This is what has happened so far:\n" + contextSummary + "\nGenerate a list of 1 enemies for the player's to fight";;
		DungeonMasterMonsterResponse response = llmStrategy.promptSchema(systemPrompt, userPrompt, DungeonMasterMonsterResponse.class); 
		addMonsterToLocation(roomState.getLocationGraph().getCurrentLocation(), response);
	}

	public DungeonMasterResponse handleDialogueTurn(String dialogueTurnSummary) {

		String systemPrompt = systemPrompt() + "\nCurrent Location\n" + roomState.getLocationGraph().getCurrentLocation().getSummary() + "\nAdjacent Locations\n" + roomState.getLocationGraph().getAdjacentLocationSummaries() + "\nPlayers\n" + roomState.getPlayerSummaries();
		String userPrompt = "This is what has happened so far:\n" + contextSummary  + "\nThese are players' chosen actions:\n" + dialogueTurnSummary + "\n Continue the story accordingly.";
		DungeonMasterResponse response = llmStrategy.promptSchema(systemPrompt, userPrompt, DungeonMasterResponse.class);
		contextSummary = response.contextSummary;
		return response;
	}

	public LocationGraph generateLocations(DungeonMasterLocationResponse response){
		ArrayList<Location> locations = new ArrayList<>();
		ArrayList<Path> paths = new ArrayList<>();
		for (DungeonMasterLocationResponse.Location location : response.locations) {
			Location loc = new Location(location.displayName, location.description);
			locations.add(loc);
		}
		for (DungeonMasterLocationResponse.PathList path : response.paths) {
			Location from = locations.stream()
					.filter(Location -> path.from.equals(Location.getDisplayName()))
					.findFirst()
					.orElseThrow(() -> new IllegalArgumentException("Location not found: " + path.from));
			Location to = locations.stream()
					.filter(Location -> path.to.equals(Location.getDisplayName()))
					.findFirst()
					.orElseThrow(() -> new IllegalArgumentException("Location not found: " + path.to));
			Path p = new Path(from, to, path.distance);
			paths.add(p);
		}
		LocationGraph graph = new LocationGraph(locations.get(0));
		for (Location location : locations) {
			graph.addN(location);
		}
		for (Path path : paths) {
			graph.addE(path);
		}

		return graph;
	}

	public void addMonsterToLocation(Location currentLocation, DungeonMasterMonsterResponse response) {
		roomState.getLocationGraph().getCurrentLocation().getEntities().clear();
		for (DungeonMasterMonsterResponse.Enemy enemy : response.enemies) {
			currentLocation.getEntities().add(MonsterGenerator.generateMonster(enemy.name, enemy.entityClass, enemy.race, 1));
		}
	}
}
