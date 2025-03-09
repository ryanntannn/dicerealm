package com.dicerealm.core.handler;

import java.util.UUID;

import com.dicerealm.core.command.StartGameCommand;
import com.dicerealm.core.dialogue.DialogueTurnHandler;
import com.dicerealm.core.dm.DungeonMasterResponse;
import com.dicerealm.core.room.RoomContext;
import com.dicerealm.core.room.RoomState;

public class StartGameHandler extends CommandHandler<StartGameCommand> {
	public StartGameHandler() {
		super("START_GAME");
	}

	@Override
	public void handle(UUID playerId, StartGameCommand command, RoomContext context) {
		if (context.getRoomState().getState() != RoomState.State.LOBBY) {
			throw new IllegalStateException("Game has already started");
		}
		context.getBroadcastStrategy().sendToAllPlayers(command);
		context.getRoomState().setState(RoomState.State.DIALOGUE_TURN);
		DungeonMasterResponse response = context.getDungeonMaster().handleDialogueTurn("Start the adventure.");
		DialogueTurnHandler.handlePlayerActions(response.actionChoices, context);
		DialogueTurnHandler.handleLocationChange(response, context);
		DialogueTurnHandler.startNewDialogueTurn(response.displayText, context);
	}
}
