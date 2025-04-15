package com.dicerealm.core.handler;

import java.util.UUID;
import com.dicerealm.core.command.UpdateThemeCommand;
import com.dicerealm.core.room.RoomContext;
import com.dicerealm.core.room.RoomState;

public class UpdateThemeHandler extends CommandHandler<UpdateThemeCommand> {

	public UpdateThemeHandler() {
		super("UPDATE_THEME");
	}

	@Override
	public void handle(UUID playerId, UpdateThemeCommand command, RoomContext context) {
		if (context.getRoomState().getState() != RoomState.State.LOBBY) {
			throw new IllegalStateException("Game has already started");
		}
		context.getRoomState().setTheme(command.getTheme());
		context.getBroadcastStrategy().sendToAllPlayers(command);
	}
}
