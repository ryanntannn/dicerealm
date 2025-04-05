package com.dicerealm.core.command.levelling;

import com.dicerealm.core.command.Command;

/**
 * Command sent to notify clients when a level-up occurs.
 */
public class LevelUpCommand extends Command {
    private final int newLevel;

    public LevelUpCommand(int newLevel) {
        super.type = "LEVEL_UP";
        this.newLevel = newLevel;
    }

    public int getNewLevel() {
        return newLevel;
    }
}