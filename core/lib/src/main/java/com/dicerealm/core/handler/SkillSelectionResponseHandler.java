package com.dicerealm.core.handler;

import com.dicerealm.core.command.leveling.SkillSelectionResponseCommand;
import com.dicerealm.core.command.leveling.SkillSelectionCommand;
import com.dicerealm.core.combat.managers.LevelManager;
import com.dicerealm.core.player.Player;
import com.dicerealm.core.room.RoomContext;
import com.dicerealm.core.skills.Skill;
import com.dicerealm.core.skills.SkillSelectionResult;
import com.dicerealm.core.skills.SkillSelectionResult.ResultType;

import java.util.List;
import java.util.UUID;

public class SkillSelectionResponseHandler extends CommandHandler<SkillSelectionResponseCommand> {

    private SkillSelectionResult skillSelectionResult;

    public SkillSelectionResponseHandler() {
        super("SKILL_SELECTION_RESPONSE");
    }

    @Override
    public void handle(UUID playerId, SkillSelectionResponseCommand command, RoomContext context) {
        if (!playerId.equals(command.getPlayerId())) {
            throw new IllegalArgumentException("Player ID mismatch: cannot select skills for another player.");
        }

        Player player = context.getRoomState().getPlayerMap().get(playerId);
        if (player == null) {
            throw new IllegalArgumentException("Player not found in room.");
        }

        LevelManager levelManager = context.getLevelManager();
        
        // Check if player has pending skill selection
        if (!levelManager.hasPendingSkillSelection(playerId)) {
            // Create failure result directly
            skillSelectionResult = SkillSelectionResult.failure(
                player, 
                ResultType.FAILED_NO_SELECTION, 
                null
            );
            
        }

        UUID selectedSkillId = command.getSelectedSkillId();
        UUID replacedSkillId = command.getReplacedSkillId();
        
        // Find the selected skill
        List<Skill> availableSkills = levelManager.getPendingSkillSelections(playerId);
        Skill selectedSkill = null;
        if (availableSkills != null) {
            selectedSkill = availableSkills.stream()
                .filter(skill -> skill.getId().equals(selectedSkillId))
                .findFirst()
                .orElse(null);
        }
        
        // If selected skill not found, return failure
        if (selectedSkill == null) {
            skillSelectionResult = SkillSelectionResult.failure(
                player, 
                ResultType.FAILED_INVALID_SKILL, 
                availableSkills
            );
        }
        
        // Check replacement skill if needed
        if (player.getSkillsInventory().getItems().size() >= 4) {
            if (replacedSkillId == null) {
                skillSelectionResult = SkillSelectionResult.failure(
                    player, 
                    ResultType.FAILED_INVENTORY_FULL, 
                    availableSkills
                );
            }
            
            Skill replacedSkill = player.getSkillsInventory().getItem(replacedSkillId);
            if (replacedSkill == null) {
                skillSelectionResult = SkillSelectionResult.failure(
                    player, 
                    ResultType.FAILED_INVALID_REPLACE, 
                    availableSkills
                );
                return;
            }
        }
        
        // Process the skill selection - this now returns a boolean, but we'll modify LevelManager
        // in a future update to return SkillSelectionResult directly
        boolean success = levelManager.processSkillSelection(
            playerId,
            selectedSkillId,
            replacedSkillId,
            player
        );
        
        if (success) {
            // Create a success result based on whether a skill was replaced

            if (replacedSkillId != null) {
                Skill replacedSkill = player.getSkillsInventory().getItem(replacedSkillId);
                skillSelectionResult = SkillSelectionResult.successReplaced(
                    player, 
                    selectedSkill, 
                    replacedSkill, 
                    availableSkills
                );
            } else {
                skillSelectionResult = SkillSelectionResult.success(
                    player, 
                    selectedSkill, 
                    availableSkills
                );
            }
        } else {
            // Generic failure
            skillSelectionResult = SkillSelectionResult.failure(
                player, 
                ResultType.FAILED_INVALID_SKILL, 
                availableSkills
            );
        }

        if (!skillSelectionResult.isSuccess() && skillSelectionResult.getAvailableSkills() != null) {
            int roomLevel = context.getRoomState().getRoomLevel();
            context.getBroadcastStrategy().sendToPlayer(
                new SkillSelectionCommand(
                    player.getId(),
                    skillSelectionResult.getAvailableSkills(),
                    player.getSkillsInventory().getItems(),
                    roomLevel
                )
                ,player
            );
        }
    }

    public SkillSelectionResult getSkillSelectionResult() {
        return skillSelectionResult;
    }
    
}