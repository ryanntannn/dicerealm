package com.dicerealm.core.command.combat;

import com.dicerealm.core.command.Command;
import com.dicerealm.core.entity.Entity;

/**
 * Sent by the server to notify players that a new turn has started.
 */
public class CombatStartTurnCommand extends Command {
    private final Entity currentTurnEntity;

    public CombatStartTurnCommand(Entity currentTurnEntity) {
        super.type = "COMBAT_START_TURN";
        this.currentTurnEntity = currentTurnEntity;
    }

    public Entity getCurrentTurnEntity() {
        return currentTurnEntity;
    }
}