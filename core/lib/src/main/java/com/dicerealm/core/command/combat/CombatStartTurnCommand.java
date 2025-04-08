package com.dicerealm.core.command.combat;

import java.util.UUID;

import com.dicerealm.core.command.Command;

/**
 * Sent by the server to notify players that a new turn has started.
 */
public class CombatStartTurnCommand extends Command {
		// index of the current turn
		private final int turnNumber;
		private final UUID currentTurnEntityId;

    public CombatStartTurnCommand(int turnNumber, UUID currentTurnEntityId) {
        super.type = "COMBAT_START_TURN";
				this.turnNumber = turnNumber;
				this.currentTurnEntityId = currentTurnEntityId;
    }

		public int getTurnNumber() {
			return turnNumber;
		}

		public UUID getCurrentTurnEntityId() {
			return currentTurnEntityId;
		}
}