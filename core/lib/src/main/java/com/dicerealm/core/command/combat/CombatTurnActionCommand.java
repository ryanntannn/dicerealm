package com.dicerealm.core.command.combat;

import com.dicerealm.core.command.Command;
import com.dicerealm.core.entity.Entity;

public class CombatTurnActionCommand extends Command {

		public enum ActionType {
				SKILL,
				WEAPON,
				SCROLL,
				POTION,
		}

    private final Entity attacker;
    private final Entity target;
    private final Object action;
		private final ActionType actionType;

    public CombatTurnActionCommand(Entity attacker, Entity target, Object action, ActionType actionType) {
        super.type = "COMBAT_TURN_ACTION";
        this.attacker = attacker;
        this.target = target;
        this.action = action;
				this.actionType = actionType;
    }

    public Entity getAttacker() {
        return attacker;
    }  
    public Entity getTarget() {
        return target;
    }
    public Object getAction() {
        return action;
    }
		public ActionType getActionType() {
			return actionType;
		}
}