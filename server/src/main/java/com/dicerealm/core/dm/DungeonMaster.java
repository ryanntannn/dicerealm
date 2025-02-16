package com.dicerealm.core.dm;

import java.util.List;
import java.util.stream.Collectors;

import com.dicerealm.core.JsonSerializationStrategy;
import com.dicerealm.core.LLMStrategy;
import com.dicerealm.core.Message;
import com.dicerealm.core.Player;
import com.dicerealm.core.RoomState;

public class DungeonMaster {

	private String makeSystemPrompt() { 
		return """
			Act as a Dungeon Master for a game of DND. You will be provided with a JSON object containing the state of the players, a summary of the progress so far, and 10 of the latest messages so far. Using this information, you will be asked to provide a response to the entire room in a json format:
			
			displayText - the text to display to the players
			actionChoices - a list of actions that the players can take
			removedItems - a list of items that have been removed from players inventory. This should be empty if the last action did not involve consuming items
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
		String prompt = makeSystemPrompt() + "\nPlayers\n" + jsonSerializationStrategy.serialize(roomState.getPlayers()) + "\nSummary\n" + summary + "\nPrevious Messages:\n" + latestTenMessages() + "\n New Message from " + player.getDisplayName() + " says: " + message;
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
