package com.dicerealm.core.command.combat;

import com.dicerealm.core.combat.managers.CombatManager;
import com.dicerealm.core.command.Command;
import com.dicerealm.core.entity.Entity;

public class CombatTurnActionCommand extends Command {
    private final CombatManager combatManager;
    private final Entity attacker;
    private final Entity target;
    private final Object actionType;

    public CombatTurnActionCommand(CombatManager combatManager, Entity attacker, Entity target, Object actionType) {
        this.combatManager = combatManager;
        this.attacker = attacker;
        this.target = target;
        this.actionType = actionType;
    }

    public void execute() {
        combatManager.executeCombatTurn(attacker, target, actionType);
    }
}