package com.dicerealm.core.command.dialogue;

import com.dicerealm.core.command.Command;
import com.dicerealm.core.dialogue.SkillCheck;

/**
 * Sent by the server to the client to indicate the end of a turn
 */
public class EndTurnCommand extends Command {
	private int turnNumber;
	private SkillCheck.ActionResultDetail actionResultDetail;
	public EndTurnCommand(int turnNumber, SkillCheck.ActionResultDetail actionResultDetail) {
		this.turnNumber = turnNumber;
		this.actionResultDetail = actionResultDetail;
		super.type = "DIALOGUE_END_TURN";
	}
	public int getTurnNumber() {
		return turnNumber;
	}
	public SkillCheck.ActionResultDetail getActionResultDetail() {
		return actionResultDetail;
	}
}
