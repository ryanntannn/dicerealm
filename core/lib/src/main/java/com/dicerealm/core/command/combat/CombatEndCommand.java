package com.dicerealm.core.command.combat;

import com.dicerealm.core.command.Command;

public class CombatEndCommand extends Command {

	public static enum CombatEndStatus {
		WIN,
		LOSE,
	}

	private final CombatEndStatus status;

	public CombatEndCommand(CombatEndStatus status) {
		this.status = status;
		super.type = "COMBAT_END";
	}

	public CombatEndStatus getStatus() {
		return status;
	}
}
