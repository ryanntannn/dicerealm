package com.dicerealm.core.combat.systems;

import java.util.List;
import java.util.Random;

import com.dicerealm.core.entity.Entity;
import com.dicerealm.core.monster.Monster;
import com.dicerealm.core.room.RoomState;


/**
 * Utility class for calculating the total strength of monsters in a room.
 * Used for balancing encounters and determining difficulty.
 */
public class RoomStrengthCalculator {

    /**
     * Calculates the total monster strength in a room by summing all monster levels.
     * 
     * @param roomState The current state of the room
     * @return The total monster strength as an integer
     */
    public static int calculateMonsterRoomStrength(RoomState roomState) {
        if (roomState == null) {
            return 0;
        }
        
        // Get all entities at the current location
        List<Entity> entities = roomState.getLocationGraph().getCurrentLocation().getEntities();
        
        if (entities == null || entities.isEmpty()) {
            return 0;
        }
        
        // Filter for monsters and sum their levels
        return entities.stream()
            .filter(entity -> entity instanceof Monster)
            .mapToInt(entity -> ((Monster) entity).getMonsterLevel())
            .sum();
    }

    public static int calculatePlayerPartyStrength(RoomState roomState) {
        if (roomState == null) {
            return 0;
        }
        
        return roomState.getRoomLevel() * roomState.getPlayerMap().size();
    }
    
    /**
     * Determines if the room is balanced for the current player party.
     * This can be used to decide if more monsters should be added or removed.
     * 
     * @param roomState The current state of the room
     * @param targetDifficulty Target difficulty multiplier (1.0 = balanced)
     * @return true if the room is appropriately balanced, false otherwise
     */
    public static boolean isRoomBalanced(RoomState roomState) {
        double targetDifficulty = getTargetDifficultyForRoom(roomState);
        int monsterStrength = calculateMonsterRoomStrength(roomState); 
        int playerStrength = calculatePlayerPartyStrength(roomState);
        int roomDifficulty = (int) Math.floor(playerStrength*targetDifficulty);
        
        if (playerStrength == 0) {
            return monsterStrength == 0; // No monsters for no players
        }
        
        return roomDifficulty >= playerStrength;
    }
    
    /**
     * Gets an appropriate difficulty target based on the room's level and type
     * 
     * @param roomState The current room state
     * @return A difficulty multiplier (monsters strength / player strength)
     */
    public static double getTargetDifficultyForRoom(RoomState roomState) {
        int roomLevel = roomState.getRoomLevel();
        Random random = new Random();
        // Base difficulty starts at 0.8 and increases with room level
        // This means higher level rooms have more challenging encounters
        double baseDifficulty = 0.8 + (roomLevel * 0.1);
        double randomFactor = 1.0 + (random.nextDouble() * 0.2); // Between 1.0 and 1.2
        baseDifficulty *= randomFactor;
        
        return baseDifficulty;
    }
}