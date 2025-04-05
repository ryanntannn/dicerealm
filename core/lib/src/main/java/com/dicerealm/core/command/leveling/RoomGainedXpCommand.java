package com.dicerealm.core.command.leveling;

import com.dicerealm.core.command.Command;

/**
 * Command sent to notify clients when the room gains XP.
 */
public class RoomGainedXpCommand extends Command {
    private int xpGained;
    private int totalXp;
    private int requiredXpForNextLevel;

    public RoomGainedXpCommand(int xpGained, int totalXp, int requiredXpForNextLevel) {
        super.type = "ROOM_GAINED_XP";
        this.xpGained = xpGained;
        this.totalXp = totalXp;
        this.requiredXpForNextLevel = requiredXpForNextLevel;
    }

    public int getXpGained() {
        return xpGained;
    }

    public int getTotalXp() {
        return totalXp;
    }
    
    public int getRequiredXpForNextLevel() {
        return requiredXpForNextLevel;
    }
}