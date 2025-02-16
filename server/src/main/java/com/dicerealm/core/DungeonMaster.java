package com.dicerealm.core;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

record DungeonMasterResponse(
    @JsonProperty(required = true, value = "displayText") String displayText,
		@JsonProperty(required = true, value = "actionChoices") PlayerAction[] actionChoices
){
		record PlayerAction(
			@JsonProperty(required = true, value = "displayText") String action,
			@JsonProperty(required = true, value = "actionId") String actionId
		) {
		}
}

public class DungeonMaster {

	private String makeSystemPrompt() { 
		return """
			Act as a Dungeon Master for a game of DND. You will be provided with a JSON object containing the state of the players, a summary of the progress so far, and 10 of the latest messages so far. Using this information, you will be asked to provide a response to the player, and a list of possible actions they might be able to take.
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

	public String handlePlayerMessage(String message, Player player) {
		String prompt = makeSystemPrompt() + "\nPlayers\n" + jsonSerializationStrategy.serialize(roomState.getPlayers()) + "\nSummary\n" + summary + "\nPrevious Messages:\n" + latestTenMessages() + "\n New Message from " + player.getDisplayName() + " says: " + message;
		updateSummary();
		DungeonMasterResponse response = llmStrategy.promptSchema(prompt, DungeonMasterResponse.class);
		response.actionChoices();
		return response.displayText();
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
