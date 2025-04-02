package com.dicerealm.core.command.combat;

import com.dicerealm.core.command.Command;

/**
 * Sent by the server to notify players that the current turn has ended.
 */
public class ComandEndTurnCommand extends Command {
    private final int turnNumber;

    public ComandEndTurnCommand(int turnNumber) {
        super.type = "COMBAT_END_TURN";
        this.turnNumber = turnNumber;
    }

    public int getTurnNumber() {
        return turnNumber;
    }
}