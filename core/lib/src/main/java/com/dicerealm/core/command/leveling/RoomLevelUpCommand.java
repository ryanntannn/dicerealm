package com.dicerealm.core.command.leveling;

import com.dicerealm.core.command.Command;

/**
 * Command sent to notify clients when the room levels up.
 */
public class RoomLevelUpCommand extends Command {
    private int newLevel;
    private int currentXp;
    private int requiredXpForNextLevel;

    public RoomLevelUpCommand(int newLevel, int currentXp, int requiredXpForNextLevel) {
        super.type = "ROOM_LEVEL_UP";
        this.newLevel = newLevel;
        this.currentXp = currentXp;
        this.requiredXpForNextLevel = requiredXpForNextLevel;
    }

    public int getNewLevel() {
        return newLevel;
    }
    
    public int getCurrentXp() {
        return currentXp;
    }
    
    public int getRequiredXpForNextLevel() {
        return requiredXpForNextLevel;
    }
}