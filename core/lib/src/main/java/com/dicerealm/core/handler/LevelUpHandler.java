package com.dicerealm.core.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.dicerealm.core.combat.managers.LevelManager;
import com.dicerealm.core.command.levelling.LevelUpCommand;
import com.dicerealm.core.command.levelling.SkillSelectionCommand;
import com.dicerealm.core.command.levelling.SkillSelectionResponseCommand;
import com.dicerealm.core.player.Player;
import com.dicerealm.core.room.RoomContext;
import com.dicerealm.core.skills.Skill;
import com.dicerealm.core.skills.SkillSelectionProcessor;
import com.dicerealm.core.skills.SkillSelectionResult;

public class LevelUpHandler extends CommandHandler<LevelUpCommand> {
    
    private final SkillSelectionProcessor skillSelectionProcessor;
    
    // Track which players have pending skill selections
    private final Map<UUID, Boolean> pendingPlayerSkillSelections = new HashMap<>();
    
    public LevelUpHandler() {
        super("LEVEL_UP");
        this.skillSelectionProcessor = new SkillSelectionProcessor();
    }
    
    @Override
    public void handle(UUID playerId, LevelUpCommand command, RoomContext context) {
        int newLevel = command.getNewLevel();
        LevelManager levelManager = context.getLevelManager();
        Player player = context.getRoomState().getPlayerMap().get(playerId);
        
        if (player == null) {
            throw new IllegalArgumentException("Player with ID " + playerId + " not found.");
        }
        
        // Notify the player about the level-up
        context.getBroadcastStrategy().sendToPlayer(command, player);
        
        // Prepare skill selection for the player
        List<Skill> newSkills = levelManager.preparePlayerSkillSelection(player, newLevel);
        
        // Mark this player as having a pending skill selection
        pendingPlayerSkillSelections.put(playerId, true);
        
        // Send command to the player for skill selection
        sendSkillSelectionCommand(player, newSkills, newLevel, context);
    }
    
    /**
     * Handles the skill selection response from a player.
     * This replaces the functionality previously in SkillSelectionResponseHandler.
     */
    public void handleSkillSelectionResponse(UUID playerId, SkillSelectionResponseCommand command, RoomContext context) {
        
        Player player = context.getRoomState().getPlayerMap().get(playerId);
        if (player == null) {
            throw new IllegalArgumentException("Player with ID " + playerId + " not found.");
        }
        
        LevelManager levelManager = context.getLevelManager();
        
        // Process the skill selection using the SkillSelectionProcessor
        SkillSelectionResult result = skillSelectionProcessor.processSkillSelection(
            playerId,
            command.getSelectedSkillId(),
            command.getReplacedSkillId(),
            player,
            levelManager
        );
        
        if (result.isSuccess()) {
            // Remove pending selection status for this player
            pendingPlayerSkillSelections.remove(playerId);
        } else {
            // Skill selection failed, re-prompt with available skills
            int roomLevel = context.getRoomState().getRoomLevel();
            
            // Re-prompt the player with available skills
            sendSkillSelectionCommand(
                player,
                result.getAvailableSkills(),
                roomLevel,
                context
            );
        }
    }
    
    /**
     * Helper method to send a skill selection command to a player
     */
    private void sendSkillSelectionCommand(Player player, List<Skill> availableSkills, int level, RoomContext context) {
        context.getBroadcastStrategy().sendToPlayer(
            new SkillSelectionCommand(
                player.getId(),
                availableSkills,
                player.getSkillsInventory().getItems(),
                level
            )
            ,player
        );
    }
    
    /**
     * Clear all pending skill selections (e.g., when leaving a room)
     */
    public void clearPendingSkillSelections() {
        pendingPlayerSkillSelections.clear();
    }
}