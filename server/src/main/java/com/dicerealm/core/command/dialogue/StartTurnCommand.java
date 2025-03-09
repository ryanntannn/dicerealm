package com.dicerealm.core.command.dialogue;

import com.dicerealm.core.command.Command;
import com.dicerealm.core.dialogue.DialogueTurn;

/**
 * Sent by the server to the client to indicate the start of a new turn
 */
public class StartTurnCommand extends Command {
	private DialogueTurn dialogueTurn;
	public StartTurnCommand(DialogueTurn dialogueTurn) {
		this.dialogueTurn = dialogueTurn;
		super.type = "DIALOGUE_START_TURN";
	}
	public DialogueTurn getDialogueTurn() {
		return dialogueTurn;
	}
}
