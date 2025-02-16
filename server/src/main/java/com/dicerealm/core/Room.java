package com.dicerealm.core;

import java.util.UUID;

import com.dicerealm.core.command.Command;
import com.dicerealm.core.command.CommandDeserializerStrategy;
import com.dicerealm.core.command.FullRoomStateCommand;
import com.dicerealm.core.command.MessageCommand;
import com.dicerealm.core.command.OutgoingMessageCommand;
import com.dicerealm.core.command.PlayerJoinCommand;
import com.dicerealm.core.command.PlayerLeaveCommand;

public class Room {
	// create a dictionary of players
	// key: player id
	// value: player object
	private RoomState roomState = new RoomState();
	private BroadcastStrategy broadcastStrategy;
	private DungeonMaster dungeonMaster;
	private CommandDeserializerStrategy commandDeserializerStrategy;
	public static RoomBuilder builder() {
		return new RoomBuilder();
	}
	
	public Room(BroadcastStrategy broadcastStrategy, LLMStrategy llmStrategy, CommandDeserializerStrategy commandDeserializerStrategy) {
		this.broadcastStrategy = broadcastStrategy;
		this.dungeonMaster = new DungeonMaster(llmStrategy, roomState.getMessages());
		this.commandDeserializerStrategy = commandDeserializerStrategy;
	}

	public void addPlayer(Player player) {
		roomState.getPlayerMap().put(player.getId(), player);
		broadcastStrategy.sendToAllPlayers(new PlayerJoinCommand(player));
		broadcastStrategy.sendToPlayer(new FullRoomStateCommand(roomState, player.getId().toString()), player);
	}

	public Player getPlayerById(UUID id) {
		return roomState.getPlayerMap().get(id);
	}

	public void removePlayerById(UUID id) throws NullPointerException {
		Player player = roomState.getPlayerMap().get(id);
		roomState.getPlayerMap().remove(id);
		broadcastStrategy.sendToAllPlayers(new PlayerLeaveCommand(player));
	}

	public void handlePlayerCommand(UUID playerId, String json) {
		Command command = commandDeserializerStrategy.deserialize(json);
		if (command instanceof MessageCommand) {
			handleNormalMessage(playerId, ((MessageCommand) command).message);
		}
	}

	public void handleNormalMessage(UUID playerId, String message) {
		Player thisPlayer = roomState.getPlayerMap().get(playerId);
		Message playerMessage = new Message(message, thisPlayer.getDisplayName());
		roomState.getMessages().add(playerMessage);
		broadcastStrategy.sendToAllPlayers(new OutgoingMessageCommand(playerMessage));
		String response = dungeonMaster.handlePlayerMessage(message, thisPlayer);
		Message dmResponseMessage = new Message(response, "Dungeon Master");
		broadcastStrategy.sendToAllPlayers(new OutgoingMessageCommand(dmResponseMessage));
		roomState.getMessages().add(dmResponseMessage);
	}

	public boolean isEmpty() {
		return roomState.getPlayers().length == 0;
	}
}
