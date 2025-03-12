package com.dicerealm.core.handler;

import java.util.UUID;

import com.dicerealm.core.command.dialogue.DialogueTurnActionCommand;
import com.dicerealm.core.dialogue.DialogueTurnAction;
import com.dicerealm.core.dialogue.DialogueTurnHandler;
import com.dicerealm.core.room.RoomContext;
import com.dicerealm.core.room.RoomState;

public class DialogueTurnActionHandler extends CommandHandler<DialogueTurnActionCommand> {

	public DialogueTurnActionHandler() {
		super("DIALOGUE_TURN_ACTION");
	}

	@Override
	public void handle(UUID playerId, DialogueTurnActionCommand command, RoomContext context) {

		// Validate the command
		if(context.getRoomState().getState() != RoomState.State.DIALOGUE_TURN) {
			throw new IllegalArgumentException("Cannot process dialogue turn action when the room is not in dialogue turn state");
		}

		if (command.getTurnNumber() != context.getRoomState().getCurrentDialogueTurnNumber()) {
			throw new IllegalArgumentException("Invalid turn number, the current turn number is " + context.getRoomState().getCurrentDialogueTurnNumber());
		}

		if(!command.getPlayerId().equals(playerId)) {
			throw new IllegalArgumentException("Cannot perform dialogue turn action for another player");
		}

		// Update the Game State
		context.getRoomState().getCurrentDialogueTurn().setDialogueTurnAction(playerId, new DialogueTurnAction(command.getAction(), command.getSkillCheck()));

		// Broadcast the command to all players
		context.getBroadcastStrategy().sendToAllPlayers(command);

		// Check if all players have submitted their actions

		if(context.getRoomState().getCurrentDialogueTurn().getNumberOfActions() == context.getRoomState().getPlayerMap().size()) {
			DialogueTurnHandler.endDialogueTurn(context);
		}
	}
}