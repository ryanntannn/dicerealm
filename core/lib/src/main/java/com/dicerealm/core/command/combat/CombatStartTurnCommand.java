package com.dicerealm.core.command.combat;

import com.dicerealm.core.combat.managers.CombatManager;
import com.dicerealm.core.command.Command;

public class CombatStartTurnCommand extends Command {
    private CombatManager combatManager;

    public CombatStartTurnCommand(CombatManager combatManager) {
        this.combatManager = combatManager;
        super.type = "COMBAT_START_TURN";
    }

    public void execute() {
        combatManager.startTurn();
    }
}