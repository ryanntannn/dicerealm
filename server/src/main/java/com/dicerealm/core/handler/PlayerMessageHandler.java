package com.dicerealm.core.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import com.dicerealm.core.command.ChangeLocationCommand;
import com.dicerealm.core.command.MessageCommand;
import com.dicerealm.core.command.OutgoingMessageCommand;
import com.dicerealm.core.command.ShowPlayerActionsCommand;
import com.dicerealm.core.dm.DungeonMasterResponse;
import com.dicerealm.core.locations.Location;
import com.dicerealm.core.message.Message;
import com.dicerealm.core.player.Player;
import com.dicerealm.core.room.RoomContext;

public class PlayerMessageHandler extends CommandHandler<MessageCommand> {
	public PlayerMessageHandler() {
		super("MESSAGE");
	}

	@Override
	public void handle(UUID playerId, MessageCommand command, RoomContext context) {
		handleNormalMessage(playerId, command.message, context);
	}

	public void handleNormalMessage(UUID playerId, String message, RoomContext context) {
		Player thisPlayer = context.getRoomState().getPlayerMap().get(playerId);
			Message playerMessage = new Message(message, thisPlayer.getDisplayName());
			context.getRoomState().getMessages().add(playerMessage);
			context.getBroadcastStrategy().sendToAllPlayers(new OutgoingMessageCommand(playerMessage));

			context.getBroadcastStrategy().sendToAllPlayers(new OutgoingMessageCommand(new Message("Dungeon Master is thinking...", "Dungeon Master")));
			DungeonMasterResponse response = context.getDungeonMaster().handlePlayerMessage(message, thisPlayer);

			Message dmResponseMessage = new Message(response.displayText, "Dungeon Master");
			context.getBroadcastStrategy().sendToAllPlayers(new OutgoingMessageCommand(dmResponseMessage));
			context.getRoomState().getMessages().add(dmResponseMessage);
			handlePlayerActions(response.actionChoices, context);
			handleLocationChange(response, context);
	}

	/**
	 * Handle a location change from a DungeonMasterResponse
	 * @param response
	 */
	public void handleLocationChange(DungeonMasterResponse response, RoomContext context) {
		if (response.locationId == null) {
			return;
		}
		UUID newLocationUuid = UUID.fromString(response.locationId);
		// check if location change is needed
		if (newLocationUuid.equals(context.getRoomState().getLocationGraph().getCurrentLocation().getId())) {
			return;
		}
		Location newLocation = context.getRoomState().getLocationGraph().getN(newLocationUuid);
		if (newLocation == null) {
			throw new IllegalArgumentException("Invalid location id");
		}
		context.getRoomState().getLocationGraph().setCurrentLocation(newLocation);
		context.getBroadcastStrategy().sendToAllPlayers(new ChangeLocationCommand(newLocation));
	}

	/**
	 * Handle the actionChoices from a DungeonMasterResponse, sending them to the appropriate players
	 * @param actionChoices
	 */
	public void handlePlayerActions(DungeonMasterResponse.PlayerAction[] actionChoices, RoomContext context) {
		HashMap<UUID, ArrayList<DungeonMasterResponse.PlayerAction>> playerActions = new HashMap<>();
		for (DungeonMasterResponse.PlayerAction action : actionChoices) {
			UUID id = UUID.fromString(action.playerId);
			if (!playerActions.containsKey(id)) {
				playerActions.put(id, new ArrayList<>());
			}
			playerActions.get(id).add(action);
		}
		for (UUID id : playerActions.keySet()) {
			Player player = context.getRoomState().getPlayerMap().get(id);
			ArrayList<DungeonMasterResponse.PlayerAction> actions = playerActions.getOrDefault(id, new ArrayList<>());
			DungeonMasterResponse.PlayerAction[] playerActionsArray = actions.toArray(new DungeonMasterResponse.PlayerAction[actions.size()]);
			context.getBroadcastStrategy().sendToPlayer(new ShowPlayerActionsCommand(playerActionsArray), player);
		}
	}
}
