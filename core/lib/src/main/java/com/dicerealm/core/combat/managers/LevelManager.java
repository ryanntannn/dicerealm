package com.dicerealm.core.combat.managers;

import com.dicerealm.core.room.RoomState;

/**
 * Manages leveling and experience for the room.
 */
public class LevelManager {
    private int XP_PER_LEVEL = 100;
    private int experience = 0;

    public LevelManager() {
    }

    public int getExperience() {
        return experience;
    }

    public void addExperience(int xp, RoomState roomState) {
        experience += xp;
        checkLevelUp(roomState);
    }

    private void checkLevelUp(RoomState roomState) {
        while (experience >= XP_PER_LEVEL) {
            experience -= XP_PER_LEVEL;
            roomState.setRoomLevel(roomState.getRoomLevel() + 1);
        }
    }
}
