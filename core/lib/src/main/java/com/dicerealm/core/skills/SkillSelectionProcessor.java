package com.dicerealm.core.skills;

import java.util.List;
import java.util.UUID;

import com.dicerealm.core.combat.managers.LevelManager;
import com.dicerealm.core.player.Player;

public class SkillSelectionProcessor {

    public SkillSelectionResult processSkillSelection(
        UUID playerId,
        UUID selectedSkillId,
        UUID replacedSkillId,
        Player player,
        LevelManager levelManager
    ) {
        // Check if player has pending skill selection
        if (!levelManager.hasPendingSkillSelection(playerId)) {
            return SkillSelectionResult.failure(
                player,
                SkillSelectionResult.ResultType.FAILED_NO_SELECTION,
                null
            );
        }

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
            return SkillSelectionResult.failure(
                player,
                SkillSelectionResult.ResultType.FAILED_INVALID_SKILL,
                availableSkills
            );
        }

        // Check replacement skill if needed
        if (player.getSkillsInventory().getItems().size() >= 4) {
            if (replacedSkillId == null) {
                return SkillSelectionResult.failure(
                    player,
                    SkillSelectionResult.ResultType.FAILED_INVENTORY_FULL,
                    availableSkills
                );
            }

            Skill replacedSkill = player.getSkillsInventory().getItem(replacedSkillId);
            if (replacedSkill == null) {
                return SkillSelectionResult.failure(
                    player,
                    SkillSelectionResult.ResultType.FAILED_INVALID_REPLACE,
                    availableSkills
                );
            }
        }

        // Process the skill selection
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
                return SkillSelectionResult.successReplaced(
                    player,
                    selectedSkill,
                    replacedSkill,
                    availableSkills
                );
            } else {
                return SkillSelectionResult.success(
                    player,
                    selectedSkill,
                    availableSkills
                );
            }
        } else {
            // Generic failure
            return SkillSelectionResult.failure(
                player,
                SkillSelectionResult.ResultType.FAILED_INVALID_SKILL,
                availableSkills
            );
        }
    }
}