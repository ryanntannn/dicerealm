package com.dicerealm.core.command.combat;

import com.dicerealm.core.combat.CombatResult;
import com.dicerealm.core.command.Command;

/**
 * Sent by the server to notify players that the current turn has ended.
 */
public class CommandEndTurnCommand extends Command {
		// The turn number that has ended
    private final int turnNumber;
		// The result of the combat for this turn
		private CombatResult combatResult;

    public CommandEndTurnCommand(int turnNumber, CombatResult combatResult) {
        super.type = "COMBAT_END_TURN";
        this.turnNumber = turnNumber;
				this.combatResult = combatResult;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

		public CombatResult getCombatResult() {
			return combatResult;
		}
}