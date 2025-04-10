package com.dicerealm.core.skills;

import java.util.List;
import java.util.UUID;

import com.dicerealm.core.combat.managers.LevelManager;
import com.dicerealm.core.player.Player;

public class SkillSelectionProcessor {

    public static SkillSelectionResult processSkillSelection(
        UUID playerID,
        UUID selectedSkillId,
        UUID replacedSkillId,
        LevelManager levelManager,
        Player player
    ) {

        // Check if playerID has pending skill selection
        if (!levelManager.hasPendingSkillSelection(playerID)) {
            return SkillSelectionResult.failure(
                playerID,
                SkillSelectionResult.ResultType.FAILED_NO_SELECTION,
                null
            );
        }

        // Find the selected skill
        List<Skill> availableSkills = levelManager.getPendingSkillSelections(playerID);
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
                playerID,
                SkillSelectionResult.ResultType.FAILED_INVALID_SKILL,
                availableSkills
            );
        }

        // Check replacement skill if needed
        if (player.getSkillsInventory().getItems().size() >= 4) {
            if (replacedSkillId == null) {
                return SkillSelectionResult.failure(
                    playerID,
                    SkillSelectionResult.ResultType.FAILED_INVENTORY_FULL,
                    availableSkills
                );
            }

            Skill replacedSkill = player.getSkillsInventory().getItem(replacedSkillId);
            if (replacedSkill == null) {
                return SkillSelectionResult.failure(
                    playerID,
                    SkillSelectionResult.ResultType.FAILED_INVALID_REPLACE,
                    availableSkills
                );
            }
        }

        // Process the skill selection
        boolean success = levelManager.processSkillSelection(
            playerID,
            selectedSkillId,
            replacedSkillId,
            player
        );

        if (success) {
            // Create a success result based on whether a skill was replaced
            if (replacedSkillId != null) {
                Skill replacedSkill = player.getSkillsInventory().getItem(replacedSkillId);
                return SkillSelectionResult.successReplaced(
                    playerID,
                    selectedSkill,
                    replacedSkill,
                    availableSkills
                );
            } else {
                return SkillSelectionResult.success(
                    playerID,
                    selectedSkill,
                    availableSkills
                );
            }
        } else {
            // Generic failure
            return SkillSelectionResult.failure(
                playerID,
                SkillSelectionResult.ResultType.FAILED_INVALID_SKILL,
                availableSkills
            );
        }
    }
}