package com.dicerealm.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import com.dicerealm.core.command.Command;
import com.dicerealm.core.command.CommandDeserializer;
import com.dicerealm.core.command.FullRoomStateCommand;
import com.dicerealm.core.command.MessageCommand;
import com.dicerealm.core.command.OutgoingMessageCommand;
import com.dicerealm.core.command.PlayerEquipItemRequest;
import com.dicerealm.core.command.PlayerEquipItemResponse;
import com.dicerealm.core.command.PlayerJoinCommand;
import com.dicerealm.core.command.PlayerLeaveCommand;
import com.dicerealm.core.command.ShowPlayerActionsCommand;
import com.dicerealm.core.dm.DungeonMaster;
import com.dicerealm.core.dm.DungeonMasterResponse;
import com.dicerealm.core.item.Item;
import com.dicerealm.core.item.EquippableItem;
import com.dicerealm.core.strategy.BroadcastStrategy;
import com.dicerealm.core.strategy.JsonSerializationStrategy;
import com.dicerealm.core.strategy.LLMStrategy;

/**
 * Represents a room in the game, containing the state of the room and the logic for handling player actions and messages
 * 
 * @see Command - base class for sending/receiving information between players and the room
 * @see Player - represents a player in the room
 * @see RoomBuilder - for creating a Room
 * @see RoomState - contains the entire state of the room
 * @see BroadcastStrategy - broadcasts Commands to players
 * @see LLMStrategy - for generating prompt responses using an LLM
 * @see DungeonMaster - uses an LLMStrategy to handle player messages
 * @see CommandDeserializer - for deserializing incoming player commands
 */
public class Room {
	private RoomState roomState = new RoomState();

	private BroadcastStrategy broadcastStrategy;
	private DungeonMaster dungeonMaster;
	private CommandDeserializer commandDeserializer;

	/**
	 * Create a new RoomBuilder for creating a Room
	 * @return RoomBuilder
	 */
	public static RoomBuilder builder() {
		return new RoomBuilder();
	}
	
	/**
	 * Create a new Room, you may want to use the RoomBuilder instead
	 * @param broadcastStrategy
	 * @param llmStrategy
	 * @param jsonSerializationStrategy
	 * 
	 * @see RoomBuilder
	 */
	public Room(BroadcastStrategy broadcastStrategy, LLMStrategy llmStrategy, JsonSerializationStrategy jsonSerializationStrategy) {
		this.broadcastStrategy = broadcastStrategy;
		this.dungeonMaster = new DungeonMaster(llmStrategy, jsonSerializationStrategy, roomState);
		this.commandDeserializer = new CommandDeserializer(jsonSerializationStrategy);
	}

	/**
	 * Add a player to the room
	 * @param player
	 */
	public void addPlayer(Player player) {
		roomState.getPlayerMap().put(player.getId(), player);
		broadcastStrategy.sendToAllPlayers(new PlayerJoinCommand(player));
		broadcastStrategy.sendToPlayer(new FullRoomStateCommand(roomState, player.getId().toString()), player);
	}

	/**
	 * Get a player by their ID
	 * @param id
	 * @return
	 */
	public Player getPlayerById(UUID id) {
		return roomState.getPlayerMap().get(id);
	}

	/**
	 * Remove a player from the room by their ID
	 * @param id
	 * @throws NullPointerException
	 */
	public void removePlayerById(UUID id) throws NullPointerException {
		Player player = roomState.getPlayerMap().get(id);
		roomState.getPlayerMap().remove(id);
		broadcastStrategy.sendToAllPlayers(new PlayerLeaveCommand(player));
	}

	/**
	 * Check if the room has no players
	 * @return
	 */
	public boolean isEmpty() {
		return roomState.getPlayers().length == 0;
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
		if (command instanceof PlayerEquipItemRequest) {
			handlePlayerEquipItemRequest(playerId, (PlayerEquipItemRequest) command);
		}
	}

	/**
	 * Handle the actionChoices from a DungeonMasterResponse, sending them to the appropriate players
	 * @param actionChoices
	 */
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

	/**
	 * Handle a player request to equip an item, and broadcast the event if successful
	 * @param playerId
	 * @param command
	 */
	public void handlePlayerEquipItemRequest(UUID playerId, PlayerEquipItemRequest command) {
		Player player = roomState.getPlayerMap().get(playerId);

		String itemIdStr = command.getItemId();

		UUID itemId = UUID.fromString(itemIdStr);

		Item item = player.getInventory().getItem(itemId);
		
		if (item == null) {
			throw new IllegalArgumentException("Item not found in inventory");
		}
		if (!(item instanceof EquippableItem)) {
			throw new IllegalArgumentException("Item is not wearable");
		}
		boolean equipSuccessful = player.equipItem(command.getBodyPart(), (EquippableItem) item);
		if (!equipSuccessful) {
			throw new IllegalArgumentException("Item could not be equipped");
		}
		broadcastStrategy.sendToAllPlayers(new PlayerEquipItemResponse(playerId.toString(), item, command.getBodyPart(), player.getStats()));
	}
}
