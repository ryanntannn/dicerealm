package com.dicerealm.core.dm;

import java.lang.reflect.AccessFlag.Location;
import java.util.ArrayList;

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

	public DungeonMasterResponse handleDialogueTurn(String dialogueTurnSummary) {
		String systemPrompt = systemPrompt() + "\nCurrent Location\n" + jsonSerializationStrategy.serialize(roomState.getLocationGraph().getCurrentLocation()) + "\nAdjacent Locations\n" + jsonSerializationStrategy.serialize(roomState.getLocationGraph().getAdjacentLocations()) + "\nPlayers\n" + jsonSerializationStrategy.serialize(roomState.getPlayers());
		String userPrompt = "This is what has happened so far:\n" + contextSummary  + "\nThese are players' chosen actions:\n" + dialogueTurnSummary + "\n Continue the story accordingly.";
		DungeonMasterResponse response = llmStrategy.promptSchema(systemPrompt, userPrompt, DungeonMasterResponse.class); 
		contextSummary = response.contextSummary;
		return response;
	}
}
