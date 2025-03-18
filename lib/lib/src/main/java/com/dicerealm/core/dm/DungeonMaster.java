package com.dicerealm.core.dm;

import com.dicerealm.core.room.RoomState;
import com.dicerealm.core.strategy.JsonSerializationStrategy;
import com.dicerealm.core.strategy.LLMStrategy;

public class DungeonMaster {

	private String makeSystemPrompt() { 
		return """
			Act as a Dungeon Master for a multiplayer game of DND. You will be provided with a JSON object containing the state of the players, a summary of the progress so far, and 10 of the latest messages so far. Using this information, you will be asked to provide a response based on the latest message, to the entire room in a json format:
			
			displayText - the text to display to the players. Briefly describe the actions taken by the player, the new state of the room as a result of the actions, and any other information you want to convey to the players. This should be a string.
			actionChoices - a list of actions that the players can take
			locationId - the id of the location of the party. If you want to stay in the current location, set this to the current location id. This should be a UUID.
			contextSummary - a summary of the current context of the room. This will be fed back to you in the next turn, and should contain any information you want to remember for the next turn. This should be a string.

			`actionChoices` should be a list of objects with the following fields:
			action - a string representing the action the player can take
			playerId - the id of the player that can take this action
			skillCheck - a JSON object representing the skill check required to take this action. This should be a JSON object that could have the following keys:

			`STRENGTH`, `DEXTERITY`, `CONSTITUTION`, `INTELLIGENCE`, `WISDOM`, `CHARISMA` Each of these keys should have an integer value representing the skill check required to take this action. If the player will roll a d20, and add their modifier to the roll. If the roll is greater than or equal to the value in the JSON object, the player can take the action. If a stat is not required, it should be set to 0. Do not unnecessarily require skill checks.

			You may only move the party to an adjacent location if they unanimously decide on the action. If the party decides to move, set the locationId to the id of the location you want to move to. If the party decides to stay, set the locationId to the current location id. Do not allow the part to split up. If the party decides to move, all players should move to the new location.
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
		String prompt = makeSystemPrompt() + "\nPlayers\n" + jsonSerializationStrategy.serialize(roomState.getPlayers()) + "\nCurrent Location\n" + jsonSerializationStrategy.serialize(roomState.getLocationGraph().getCurrentLocation()) + "\nAdjacent Locations\n" + jsonSerializationStrategy.serialize(roomState.getLocationGraph().getAdjacentLocations()) + "\nContext Summary\n" + contextSummary + "\nThe Player's Chosen Actions:\n" + dialogueTurnSummary;
		DungeonMasterResponse response = llmStrategy.promptSchema(prompt, DungeonMasterResponse.class);
		contextSummary = response.contextSummary;
		return response;
	}

}
