package com.dicerealm.core.handler;

import java.util.UUID;

import com.dicerealm.core.command.StartGameCommand;
import com.dicerealm.core.command.UpdateLocationGraphCommand;
import com.dicerealm.core.dialogue.DialogueManager;
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
		// Comment out this line to use mockLocationGraph.
		context.getDungeonMaster().handleLocationGeneration(context.getRoomState().getTheme());
		context.getBroadcastStrategy().sendToAllPlayers(
				new UpdateLocationGraphCommand(context.getRoomState().getLocationGraph()));
		DungeonMasterResponse response =
				context.getDungeonMaster().handleDialogueTurn("Start the adventure.");
		DialogueManager.broadcastPlayerActions(response.actionChoices, context);
		DialogueManager.broadcastLocationChange(response, context);
		DialogueManager.startNewDialogueTurn(response.displayText, context);
	}
}
