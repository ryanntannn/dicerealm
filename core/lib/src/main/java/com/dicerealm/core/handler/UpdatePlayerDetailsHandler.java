package com.dicerealm.core.handler;

import java.util.UUID;

import com.dicerealm.core.command.UpdatePlayerDetailsCommand;
import com.dicerealm.core.command.UpdatePlayerDetailsRequestCommand;
import com.dicerealm.core.player.Player;
import com.dicerealm.core.player.PresetPlayerFactory;
import com.dicerealm.core.room.RoomContext;
import com.dicerealm.core.room.RoomState;

public class UpdatePlayerDetailsHandler extends CommandHandler<UpdatePlayerDetailsRequestCommand> {

	public UpdatePlayerDetailsHandler() {
		super("UPDATE_PLAYER_DETAILS_REQUEST");
	}

	@Override
	public void handle(UUID playerId, UpdatePlayerDetailsRequestCommand command, RoomContext context) {

		if (!context.getRoomState().getState().equals(RoomState.State.LOBBY)) {
			throw new RuntimeException("Cannot update player details outside of lobby");
		}

		if (!context.getRoomState().getPlayerMap().containsKey(playerId)) {
			throw new RuntimeException("Player not found in room");
		}

		Player newPlayer = new Player(command.displayName, command.race, command.entityClass, command.baseStats);

		PresetPlayerFactory.addDefaultItems(newPlayer);
		PresetPlayerFactory.addDefaultSkills(newPlayer);

		newPlayer.setId(playerId);

		System.out.println("Player ID: " + playerId);

		context.getRoomState().getPlayerMap().put(playerId, newPlayer);
		
		context.getBroadcastStrategy().sendToAllPlayers(new UpdatePlayerDetailsCommand(newPlayer));
	}
	
}
