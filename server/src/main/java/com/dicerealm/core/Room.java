package com.dicerealm.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.dicerealm.core.command.Command;
import com.dicerealm.core.command.CommandDeserializerStrategy;
import com.dicerealm.core.command.MessageCommand;
import com.dicerealm.core.command.MessageHistoryCommand;
import com.dicerealm.core.command.OutgoingMessageCommand;
import com.dicerealm.core.command.PlayerJoinCommand;
import com.dicerealm.core.command.PlayerLeaveCommand;

public class Room {
	// create a dictionary of players
	// key: player id
	// value: player object
	private Map<UUID, Player> players = new HashMap<UUID, Player>();
	private List<Message> messages = Collections.synchronizedList(new ArrayList<Message>());
	private BroadcastStrategy broadcastStrategy;
	private DungeonMaster dungeonMaster;
	private CommandDeserializerStrategy commandDeserializerStrategy;
	public static RoomBuilder builder() {
		return new RoomBuilder();
	}
	
	public Room(BroadcastStrategy broadcastStrategy, LLMStrategy llmStrategy, CommandDeserializerStrategy commandDeserializerStrategy) {
		this.broadcastStrategy = broadcastStrategy;
		this.dungeonMaster = new DungeonMaster(llmStrategy, messages);
		this.commandDeserializerStrategy = commandDeserializerStrategy;
	}

	public void addPlayer(Player player) {
		players.put(player.getId(), player);
		broadcastStrategy.sendToAllPlayers(new PlayerJoinCommand(player));
		broadcastStrategy.sendToPlayer(
			new MessageHistoryCommand(
				messages.toArray(new Message[messages.size()])), 
				player
			);
	}

	public Player getPlayerById(UUID id) {
		return players.get(id);
	}

	public void removePlayerById(UUID id) throws NullPointerException {
		Player player = players.get(id);
		players.remove(id);
		broadcastStrategy.sendToAllPlayers(new PlayerLeaveCommand(player));
	}

	public void handlePlayerCommand(UUID playerId, String json) {
		Command command = commandDeserializerStrategy.deserialize(json);
		if (command instanceof MessageCommand) {
			handleNormalMessage(playerId, ((MessageCommand) command).message);
		}
	}

	public void handleNormalMessage(UUID playerId, String message) {
		Player thisPlayer = players.get(playerId);
		Message playerMessage = new Message(message, thisPlayer.getDisplayName());
		messages.add(playerMessage);
		broadcastStrategy.sendToAllPlayers(new OutgoingMessageCommand(playerMessage));
		String response = dungeonMaster.handlePlayerMessage(message, thisPlayer);
		Message dmResponseMessage = new Message(response, "Dungeon Master");
		broadcastStrategy.sendToAllPlayers(new OutgoingMessageCommand(dmResponseMessage));
		messages.add(dmResponseMessage);
	}
}
