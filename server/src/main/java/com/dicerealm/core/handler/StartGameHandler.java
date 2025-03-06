package com.dicerealm.core.handler;

import java.util.UUID;

import com.dicerealm.core.command.StartGameCommand;
import com.dicerealm.core.room.RoomContext;
import com.dicerealm.core.room.RoomState;

public class StartGameHandler extends CommandHandler<StartGameCommand> {
	PlayerMessageHandler playerMessageHandler = new PlayerMessageHandler();
	public StartGameHandler() {
		super("START_GAME");
	}

	@Override
	public void handle(UUID playerId, StartGameCommand command, RoomContext context) {
		if (context.getRoomState().getState() != RoomState.State.LOBBY) {
			throw new IllegalStateException("Game has already started");
		}
		context.getRoomState().setState(RoomState.State.DIALOGUE);
		context.getBroadcastStrategy().sendToAllPlayers(command);
		playerMessageHandler.handleNormalMessage(playerId, "Let's start the adventure!", context);
	}
}
