package com.dicerealm.core.command.combat;

import java.util.UUID;

import com.dicerealm.core.command.Command;

/**
 * Sent by the server to notify players that a new turn has started.
 */
public class CombatStartTurnCommand extends Command {
		// index of the current turn
		private final int turnNumber;
		private final int roundNumber;
		private final UUID currentTurnEntityId;


    public CombatStartTurnCommand(int turnNumber, int roundNumber, UUID currentTurnEntityId) {
        super.type = "COMBAT_START_TURN";
				this.turnNumber = turnNumber;
				this.roundNumber = roundNumber;
				this.currentTurnEntityId = currentTurnEntityId;
    }

		public int getTurnNumber() {
			return turnNumber;
		}

		public int getRoundNumber() {
			return roundNumber;
		}

		public UUID getCurrentTurnEntityId() {
			return currentTurnEntityId;
		}

}