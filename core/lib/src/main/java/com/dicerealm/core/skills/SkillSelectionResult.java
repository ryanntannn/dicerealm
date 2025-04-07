package com.dicerealm.core.skills;

import java.util.List;
import java.util.UUID;

public class SkillSelectionResult {

    public enum ResultType {
        SUCCESS_ADDED,
        SUCCESS_REPLACED,
        FAILED_NO_SELECTION,
        FAILED_INVALID_SKILL,
        FAILED_INVALID_REPLACE,
        FAILED_INVENTORY_FULL
    }

    private final UUID playerID;
    private final ResultType resultType;
    private final Skill selectedSkill;
    private final Skill replacedSkill;
    private final List<Skill> availableSkills;
    private final String resultMessage;

    private SkillSelectionResult(UUID playerID, ResultType resultType, Skill selectedSkill, Skill replacedSkill, List<Skill> availableSkills, String resultMessage) {
        this.playerID = playerID;
        this.resultType = resultType;
        this.selectedSkill = selectedSkill;
        this.replacedSkill = replacedSkill;
        this.availableSkills = availableSkills;
        this.resultMessage = resultMessage;
    }

    public UUID getPlayerID() {
        return playerID;
    }

    public ResultType getResultType() {
        return resultType;
    }

    public Skill getSelectedSkill() {
        return selectedSkill;
    }

    public Skill getReplacedSkill() {
        return replacedSkill;
    }

    public List<Skill> getAvailableSkills() {
        return availableSkills;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public boolean isSuccess() {
        return resultType == ResultType.SUCCESS_ADDED || resultType == ResultType.SUCCESS_REPLACED;
    }

    public static SkillSelectionResult success(UUID playerID, Skill selectedSkill, List<Skill> availableSkills) {
        return new SkillSelectionResult(
                playerID,
                ResultType.SUCCESS_ADDED,
                selectedSkill,
                null,
                availableSkills,
                "You learned " + selectedSkill.getDisplayName() + "!"
        );
    }

    public static SkillSelectionResult successReplaced(UUID playerID, Skill selectedSkill,
                                                       Skill replacedSkill, List<Skill> availableSkills) {
        return new SkillSelectionResult(
                playerID,
                ResultType.SUCCESS_REPLACED,
                selectedSkill,
                replacedSkill,
                availableSkills,
                "You replaced " + replacedSkill.getDisplayName() +
                        " with " + selectedSkill.getDisplayName() + "!"
        );
    }

    public static SkillSelectionResult failure(UUID playerID, ResultType failureType, List<Skill> availableSkills) {
        String message = switch (failureType) {
            case FAILED_NO_SELECTION -> "No pending skill selection found.";
            case FAILED_INVALID_SKILL -> "Invalid skill selected.";
            case FAILED_INVALID_REPLACE -> "Invalid skill to replace.";
            case FAILED_INVENTORY_FULL -> "Your skill inventory is full. You must replace an existing skill.";
            default -> "Skill selection failed.";
        };

        return new SkillSelectionResult(
                playerID,
                failureType,
                null,
                null,
                availableSkills,
                message
        );
    }
}
