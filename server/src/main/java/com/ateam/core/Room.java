package com.ateam.core;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.ateam.core.command.MessageCommand;
import com.ateam.core.command.PlayerJoinCommand;
import com.ateam.core.command.PlayerLeaveCommand;

public class Room {
	// create a dictionary of players
	// key: player id
	// value: player object
	private Map<UUID, Player> players = new HashMap<UUID, Player>();
	private BroadcastStrategy broadcastStrategy;
	
	public Room(BroadcastStrategy broadcastStrategy) {
		this.broadcastStrategy = broadcastStrategy;
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
	}
}
