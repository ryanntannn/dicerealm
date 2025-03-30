package com.dicerealm.core.command.combat;

import com.dicerealm.core.combat.managers.CombatManager;
import com.dicerealm.core.command.Command;

public class ComandEndTurnCommand extends Command {
    private CombatManager combatManager;
    private int combatTurnNumber;

    public ComandEndTurnCommand(CombatManager combatManager, int CombatTurnNumber) {
        this.combatManager = combatManager;
        this.combatTurnNumber = CombatTurnNumber;
        super.type = "COMBAT_END_TURN";
    }

    public void execute() {
        combatManager.endTurn();
    }
}