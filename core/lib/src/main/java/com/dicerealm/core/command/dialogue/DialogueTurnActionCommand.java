package com.dicerealm.core.command.dialogue;

import java.util.UUID;

import com.dicerealm.core.command.Command;
import com.dicerealm.core.entity.StatsMap;

/**
 * Sent by the client to the server to indicate the player's action during a dialogue turn
 */
public class DialogueTurnActionCommand extends Command {
	private int turnNumber;
	private UUID playerId;
	private String action;
	private StatsMap skillCheck;

	public DialogueTurnActionCommand(int turnNumber, UUID playerId, String action, StatsMap skillCheck) {
		super.type = "DIALOGUE_TURN_ACTION";
		this.turnNumber = turnNumber;
		this.playerId = playerId;
		this.action = action;
		this.skillCheck = skillCheck;
	}

	public UUID getPlayerId() {
		return playerId;
	}
	public String getAction() {
		return action;
	}
	public StatsMap getSkillCheck() {
		return skillCheck;
	}
	public int getTurnNumber() {
		return turnNumber;
	}
}
