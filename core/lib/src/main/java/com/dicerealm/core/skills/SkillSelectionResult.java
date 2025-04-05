package com.dicerealm.core.skills;

import java.util.List;

import com.dicerealm.core.player.Player;

public class SkillSelectionResult {

    public enum ResultType {
        SUCCESS_ADDED,
        SUCCESS_REPLACED,
        FAILED_NO_SELECTION,
        FAILED_INVALID_SKILL,
        FAILED_INVALID_REPLACE,
        FAILED_INVENTORY_FULL
    }

    private final Player player;
    private final ResultType resultType;
    private final Skill selectedSkill;
    private final Skill replacedSkill;
    private final List<Skill> availableSkills;
    private final String resultMessage;

    private SkillSelectionResult(Player player, ResultType resultType, Skill selectedSkill, Skill replacedSkill, List<Skill> availableSkills, String resultMessage) {
        this.player = player;
        this.resultType = resultType;
        this.selectedSkill = selectedSkill;
        this.replacedSkill = replacedSkill;
        this.availableSkills = availableSkills;
        this.resultMessage = resultMessage;
    }

    public Player getPlayer() {
        return player;
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

    public static SkillSelectionResult success(Player player, Skill selectedSkill, List<Skill> availableSkills) {
        return new SkillSelectionResult(
                player,
                ResultType.SUCCESS_ADDED,
                selectedSkill,
                null,
                availableSkills,
                "You learned " + selectedSkill.getDisplayName() + "!"
        );
    }

    public static SkillSelectionResult successReplaced(Player player, Skill selectedSkill,
                                                       Skill replacedSkill, List<Skill> availableSkills) {
        return new SkillSelectionResult(
                player,
                ResultType.SUCCESS_REPLACED,
                selectedSkill,
                replacedSkill,
                availableSkills,
                "You replaced " + replacedSkill.getDisplayName() +
                        " with " + selectedSkill.getDisplayName() + "!"
        );
    }

    public static SkillSelectionResult failure(Player player, ResultType failureType, List<Skill> availableSkills) {
        String message = switch (failureType) {
            case FAILED_NO_SELECTION -> "No pending skill selection found.";
            case FAILED_INVALID_SKILL -> "Invalid skill selected.";
            case FAILED_INVALID_REPLACE -> "Invalid skill to replace.";
            case FAILED_INVENTORY_FULL -> "Your skill inventory is full. You must replace an existing skill.";
            default -> "Skill selection failed.";
        };

        return new SkillSelectionResult(
                player,
                failureType,
                null,
                null,
                availableSkills,
                message
        );
    }
}
