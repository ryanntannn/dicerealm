package com.dicerealm.core.handler;

import java.util.ArrayList;
import java.util.UUID;

import com.dicerealm.core.command.StartGameCommand;
import com.dicerealm.core.dialogue.DialogueManager;
import com.dicerealm.core.dm.DungeonMasterLocationResponse;
import com.dicerealm.core.dm.DungeonMasterResponse;
import com.dicerealm.core.locations.Location;
import com.dicerealm.core.locations.LocationGraph;
import com.dicerealm.core.locations.Path;
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
		context.getDungeonMaster().handleLocationGeneration("Forgotten Kingdom of Shadows");
		context.getBroadcastStrategy().sendToAllPlayers(command);
		context.getRoomState().setState(RoomState.State.DIALOGUE_TURN);
		DungeonMasterResponse response = context.getDungeonMaster().handleDialogueTurn("Start the adventure.");
		DialogueManager.broadcastPlayerActions(response.actionChoices, context);
		DialogueManager.broadcastLocationChange(response, context);
		DialogueManager.startNewDialogueTurn(response.displayText, context);
	}
}
