package com.dicerealm.core.dm;

import java.util.List;
import java.util.stream.Collectors;

import com.dicerealm.core.Message;
import com.dicerealm.core.Player;
import com.dicerealm.core.RoomState;
import com.dicerealm.core.strategy.JsonSerializationStrategy;
import com.dicerealm.core.strategy.LLMStrategy;

public class DungeonMaster {

	private String makeSystemPrompt() { 
		return """
			Act as a Dungeon Master for a multiplayer game of DND. You will be provided with a JSON object containing the state of the players, a summary of the progress so far, and 10 of the latest messages so far. Using this information, you will be asked to provide a response based on the latest message, to the entire room in a json format:
			
			displayText - the text to display to the players
			actionChoices - a list of actions that the players can take
			locationId - the id of the location of the party. If you want to stay in the current location, set this to the current location id. This should be a UUID.

			`actionChoices` should be a list of objects with the following fields:
			action - a string representing the action the player can take
			playerId - the id of the player that can take this action
			skillCheck - a JSON object representing the skill check required to take this action. This should be a JSON object that could have the following keys:
			`STRENGTH`, `DEXTERITY`, `CONSTITUTION`, `INTELLIGENCE`, `WISDOM`, `CHARISMA` Each of these keys should have an integer value representing the skill check required to take this action. If the player will roll a d20, and add their modifier to the roll. If the roll is greater than or equal to the value in the JSON object, the player can take the action. If a stat is not required, it should be set to 0. Do not unnecessarily require skill checks.

			You may only move the party to an adjacent location, which will be provided in the JSON object.
			""";
	}

	private LLMStrategy llmStrategy;
	private JsonSerializationStrategy jsonSerializationStrategy;
	private List<Message> messageHistory;
	private String summary;
	private RoomState roomState;

	public DungeonMaster(LLMStrategy llmStrategy, JsonSerializationStrategy jsonSerializationStrategy, RoomState roomState) {
		this.llmStrategy = llmStrategy;
		this.messageHistory = roomState.getMessages();
		this.roomState = roomState;
		this.jsonSerializationStrategy = jsonSerializationStrategy;
	}

	public DungeonMasterResponse handlePlayerMessage(String message, Player player) {
		String prompt = makeSystemPrompt() + "\nPlayers\n" + jsonSerializationStrategy.serialize(roomState.getPlayers()) + "\nCurrent Location\n" + jsonSerializationStrategy.serialize(roomState.getLocationGraph().getCurrentLocation()) + "\nAdjacent Locations\n" + jsonSerializationStrategy.serialize(roomState.getLocationGraph().getAdjacentLocations()) + "\nSummary\n" + summary + "\nPrevious Messages:\n" + latestTenMessages() + "\n New Message from " + player.getDisplayName() + " says: " + message;
		updateSummary();
		return llmStrategy.promptSchema(prompt, DungeonMasterResponse.class);
	}

	private String latestTenMessages() {
		return messageHistory.subList(Math.max(messageHistory.size() - 10, 0), Math.max(messageHistory.size() - 1, 0)).stream().map(Message::toString).collect(Collectors.joining("\n"));
	}

	public void updateSummary() {
		// If there are less than 10 messages, don't update the summary
		if (messageHistory.size() < 10) {
			return;
		}

		String prompt = "Provide an updated summary of not more than 1000 characters, given the previous summary and the latest ten messages:\nSummary:\n" + summary + "\nLatest 10 messages\n" + latestTenMessages();

		summary = llmStrategy.promptStr(prompt);
	}
}
