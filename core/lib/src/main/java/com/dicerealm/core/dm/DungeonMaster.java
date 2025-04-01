package com.dicerealm.core.dm;

import java.util.ArrayList;

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
		
			2. actionChoices
			- A list of objects, each representing an action available to the players. Each object must contain:
				- action: A string describing the action.
					- Provide action for the player to do in Current location.
				- playerId: The ID of the player who can take this action.
				- skillCheck: A JSON object specifying any required skill checks. This object can include keys such as STRENGTH, DEXTERITY, CONSTITUTION, INTELLIGENCE, WISDOM, and CHARISMA with integer values representing the threshold. For skills not needed, set their value to 0.
					- Ensure that any skill checks required are logically tied to the action. For example, use DEXTERITY for lockpicking, CHARISMA for persuasion, etc. If no skill check is needed, set the skill check values to 0.
		
			3. location
			- A UUID representing the current location of the party.
			- When at least half of the party wants to move to a new location, update the locationId
		
			4. contextSummary
			- A string summarizing the current context of the room and adding it to the previous context summary 
			- Include as much infomation as possbile.
			- This summary will be provided back to you in the next turn.

			5. switchToCombatThisTurn
			- A boolean indicating whether the room should switch to combat mode this turn. 
			- If there are enemies present in the current location, you can set this to true to initiate combat this turn.
			- If there are no enemies present, this must be set to false.
			- If true, the room will switch to combat mode and a different system will handle combat
			- If false, the room will remain in dialogue mode. 
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
					- Provide at least 6 locations.

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

	public DungeonMasterResponse handleDialogueTurn(String dialogueTurnSummary) {
		String systemPrompt = systemPrompt() + "\nCurrent Location\n" + jsonSerializationStrategy.serialize(roomState.getLocationGraph().getCurrentLocation()) + "\nAdjacent Locations\n" + jsonSerializationStrategy.serialize(roomState.getLocationGraph().getAdjacentLocations()) + "\nPlayers\n" + jsonSerializationStrategy.serialize(roomState.getPlayers());
		String userPrompt = "This is what has happened so far:\n" + contextSummary  + "\nThese are players' chosen actions:\n" + dialogueTurnSummary + "\n Continue the story accordingly.";
		DungeonMasterResponse response = llmStrategy.promptSchema(systemPrompt, userPrompt, DungeonMasterResponse.class); 
		contextSummary = response.contextSummary;
		return response;
	}

	public LocationGraph generateLocations(DungeonMasterLocationResponse response){
		ArrayList<Location> locations = new ArrayList<>();
		ArrayList<Path> paths = new ArrayList<>();
		for (DungeonMasterLocationResponse.LocationList location : response.locations) {
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
}
