package com.dicerealm.core.command.combat;

import com.dicerealm.core.command.Command;

/**
 * Sent by the server to notify players that a new turn has started.
 */
public class CombatStartTurnCommand extends Command {
		// index of the current turn
		private final int turnNumber;

    public CombatStartTurnCommand(int turnNumber) {
        super.type = "COMBAT_START_TURN";
				this.turnNumber = turnNumber;
    }

		public int getTurnNumber() {
			return turnNumber;
		}
}