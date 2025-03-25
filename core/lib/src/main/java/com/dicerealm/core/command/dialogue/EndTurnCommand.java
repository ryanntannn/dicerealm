package com.dicerealm.core.command.dialogue;

import com.dicerealm.core.command.Command;

/**
 * Sent by the server to the client to indicate the end of a turn
 */
public class EndTurnCommand extends Command {
	private int turnNumber;
	public EndTurnCommand(int turnNumber) {
		this.turnNumber = turnNumber;
		super.type = "DIALOGUE_END_TURN";
	}
	public int getTurnNumber() {
		return turnNumber;
	}
}
