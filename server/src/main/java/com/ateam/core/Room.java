package com.ateam.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import com.ateam.core.command.MessageCommand;
import com.ateam.core.command.OutgoingMessageCommand;
import com.ateam.core.command.PlayerJoinCommand;
import com.ateam.core.command.PlayerLeaveCommand;

public class Room {
	// create a dictionary of players
	// key: player id
	// value: player object
	private Map<UUID, Player> players = new HashMap<UUID, Player>();
	private BroadcastStrategy broadcastStrategy;
	private LLMStrategy llmStrategy;
	private List<Message> messages = Collections.synchronizedList(new ArrayList<Message>());
	
	public Room(BroadcastStrategy broadcastStrategy, LLMStrategy llmStrategy) {
		this.broadcastStrategy = broadcastStrategy;
		this.llmStrategy = llmStrategy;
	}

	public void addPlayer(Player player) {
		players.put(player.getId(), player);
		broadcastStrategy.sendToAllPlayers(new PlayerJoinCommand(player));
	}

	public Player getPlayerById(UUID id) {
		return players.get(id);
	}

	public void removePlayerById(UUID id) {
		Player player = players.get(id);
		players.remove(id);
		broadcastStrategy.sendToAllPlayers(new PlayerLeaveCommand(player));
	}

	public void handleNormalMessage(UUID playerId, String message) {
		broadcastStrategy.sendToAllPlayers(new MessageCommand(message));
		Stream<String> responseSteam = llmStrategy.prompt(message);
		Message dmResponseMessage = new Message("", "Dungeon Master");
		responseSteam.forEach(response -> {
			dmResponseMessage.appendMessage(response);
			broadcastStrategy.sendToAllPlayers(new OutgoingMessageCommand(dmResponseMessage));
		});
		messages.add(dmResponseMessage);
	}
}
