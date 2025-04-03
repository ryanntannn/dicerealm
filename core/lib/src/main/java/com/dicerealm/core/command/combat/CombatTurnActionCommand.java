package com.dicerealm.core.command.combat;

import com.dicerealm.core.command.Command;
import com.dicerealm.core.entity.Entity;

public class CombatTurnActionCommand extends Command {
    private final Entity attacker;
    private final Entity target;
    private final Object actionType;

    public CombatTurnActionCommand(Entity attacker, Entity target, Object actionType) {
        super.type = "COMBAT_TURN_ACTION";
        this.attacker = attacker;
        this.target = target;
        this.actionType = actionType;
    }

    public Entity getAttacker() {
        return attacker;
    }  
    public Entity getTarget() {
        return target;
    }
    public Object getActionType() {
        return actionType;
    }
}