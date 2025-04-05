package com.dicerealm.core.combat.managers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.dicerealm.core.entity.EntityClass;
import com.dicerealm.core.player.Player;
import com.dicerealm.core.room.RoomState;
import com.dicerealm.core.skills.Skill;
import com.dicerealm.core.skills.SkillsRepository;

/**
 * Manages room XP, leveling, and player skill selection.
 */
public class LevelManager {
    private static final int XP_PER_LEVEL = 100;
    private int roomExperience = 0;
    private Map<UUID, List<Skill>> pendingSkillSelections = new HashMap<>();
    private int MAX_SKILLS = 4;


    public int getRoomExperience() {
        return roomExperience;
    }
    
    public void addExperience(int xp, RoomState roomState) {
        roomExperience += xp;
    }
    
    public boolean checkLevelUp(RoomState roomState) {
        int currentLevel = roomState.getRoomLevel();
        int requiredXP = XP_PER_LEVEL * currentLevel;
        
        if (roomExperience >= requiredXP) {
            // Level up the room
            roomExperience -= requiredXP;
            roomState.setRoomLevel(currentLevel + 1);
            return true;
        }
        return false;
    }
    
    /**
     * Get XP needed for next level.
     */
    public int getXpForNextLevel(RoomState roomState) {
        return XP_PER_LEVEL * roomState.getRoomLevel();
    }
    
    /**
     * Prepare skill selection options for a player based on room level.
     */
    public List<Skill> preparePlayerSkillSelection(Player player, int roomLevel) {
        EntityClass playerClass = player.getEntityClass();
        
        // Get available skills for player's class and room level
        List<Skill> availableSkills = SkillsRepository.getAvailableSkills(playerClass, roomLevel);
        
        // Filter out skills the player already has
        List<Skill> playerSkills = player.getSkillsInventory().getItems();
        List<Skill> newSkills = SkillsRepository.filterNewSkills(availableSkills, playerSkills);
        
        // Store pending skill selections
        if (!newSkills.isEmpty()) {
            pendingSkillSelections.put(player.getId(), newSkills);
        }
        
        return newSkills;
    }
    
    /**
     * Process a player's skill selection.
     */
    public boolean processSkillSelection(UUID playerId, UUID selectedSkillId, UUID replacedSkillId, Player player) {
        // Check if this player has pending skill selections
        if (!pendingSkillSelections.containsKey(playerId)) {
            return false;
        }
        
        // Find the selected skill
        List<Skill> availableSkills = pendingSkillSelections.get(playerId);
        Skill selectedSkill = availableSkills.stream()
            .filter(skill -> skill.getId().equals(selectedSkillId))
            .findFirst()
            .orElse(null);
            
        if (selectedSkill == null) {
            return false;
        }
        
        // Handle skill addition/replacement
        var skillInventory = player.getSkillsInventory();
        
        if (skillInventory.getItems().size() >= MAX_SKILLS) {
            // Need to replace an existing skill
            if (replacedSkillId == null) {
                return false;
            }
            
            Skill replacedSkill = skillInventory.getItem(replacedSkillId);
            if (replacedSkill == null) {
                return false;
            }
            
            // Remove the old skill and add the new one
            skillInventory.removeItem(replacedSkill);
            skillInventory.addItem(selectedSkill);
        } else {
            // Just add the new skill
            skillInventory.addItem(selectedSkill);
        }
        
        // Remove pending selections for this player
        pendingSkillSelections.remove(playerId);
        return true;
    }
    
    /**
     * Check if a player has pending skill selections.
     */
    public boolean hasPendingSkillSelection(UUID playerId) {
        return pendingSkillSelections.containsKey(playerId);
    }
    
    /**
     * Get the pending skill selections for a player.
     */
    public List<Skill> getPendingSkillSelections(UUID playerId) {
        return pendingSkillSelections.get(playerId);
    }
}