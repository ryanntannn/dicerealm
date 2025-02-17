package com.dicerealm.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import com.dicerealm.core.command.Command;
import com.dicerealm.core.command.CommandDeserializer;
import com.dicerealm.core.command.FullRoomStateCommand;
import com.dicerealm.core.command.MessageCommand;
import com.dicerealm.core.command.OutgoingMessageCommand;
import com.dicerealm.core.command.PlayerJoinCommand;
import com.dicerealm.core.command.PlayerLeaveCommand;
import com.dicerealm.core.command.ShowPlayerActionsCommand;
import com.dicerealm.core.dm.DungeonMaster;
import com.dicerealm.core.dm.DungeonMasterResponse;

public class Room {
	// create a dictionary of players
	// key: player id
	// value: player object
	private RoomState roomState = new RoomState();
	private BroadcastStrategy broadcastStrategy;
	private DungeonMaster dungeonMaster;
	private CommandDeserializer commandDeserializer;

	public static RoomBuilder builder() {
		return new RoomBuilder();
	}
	
	public Room(BroadcastStrategy broadcastStrategy, LLMStrategy llmStrategy, JsonSerializationStrategy jsonSerializationStrategy) {
		this.broadcastStrategy = broadcastStrategy;
		this.dungeonMaster = new DungeonMaster(llmStrategy, jsonSerializationStrategy, roomState);
		this.commandDeserializer = new CommandDeserializer(jsonSerializationStrategy);
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

	/**
	 * Handle an incoming command from a player
	 * @param playerId
	 * @param json
	 */
	public void handlePlayerCommand(UUID playerId, String json) {
		Command command = commandDeserializer.deserialize(json);
		if (command instanceof MessageCommand) {
			handleNormalMessage(playerId, ((MessageCommand) command).message);
		}
	}

	public void handlePlayerActions(DungeonMasterResponse.PlayerAction[] actionChoices) {
		HashMap<UUID, ArrayList<String>> playerActions = new HashMap<>();
		for (DungeonMasterResponse.PlayerAction action : actionChoices) {
			UUID id = UUID.fromString(action.playerId);
			if (!playerActions.containsKey(id)) {
				playerActions.put(id, new ArrayList<>());
			}
			playerActions.get(id).add(action.action);
		}
		for (UUID id : playerActions.keySet()) {
			Player player = roomState.getPlayerMap().get(id);
			String[] actions = playerActions.get(id).toArray(new String[0]);
			broadcastStrategy.sendToPlayer(new ShowPlayerActionsCommand(actions), player);
		}
	}

	/**
	 * Handle a normal text message from a player
	 * @param playerId
	 * @param message
	 */
	public void handleNormalMessage(UUID playerId, String message) {
		Player thisPlayer = roomState.getPlayerMap().get(playerId);
		Message playerMessage = new Message(message, thisPlayer.getDisplayName());
		roomState.getMessages().add(playerMessage);
		broadcastStrategy.sendToAllPlayers(new OutgoingMessageCommand(playerMessage));
		DungeonMasterResponse response = dungeonMaster.handlePlayerMessage(message, thisPlayer);

		Message dmResponseMessage = new Message(response.displayText, "Dungeon Master");
		broadcastStrategy.sendToAllPlayers(new OutgoingMessageCommand(dmResponseMessage));
		roomState.getMessages().add(dmResponseMessage);
		handlePlayerActions(response.actionChoices);
	}

	public boolean isEmpty() {
		return roomState.getPlayers().length == 0;
	}
}
