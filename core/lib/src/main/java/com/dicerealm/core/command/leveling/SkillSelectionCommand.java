package com.dicerealm.core.command.leveling;

import com.dicerealm.core.command.Command;
import com.dicerealm.core.skills.Skill;

import java.util.List;
import java.util.UUID;

/**
 * Command sent to clients when a player needs to select a skill.
 */
public class SkillSelectionCommand extends Command {
    private UUID playerId;
    private List<Skill> availableSkills;
    private List<Skill> currentSkills;
    private int roomLevel;

    public SkillSelectionCommand(UUID playerId, List<Skill> availableSkills, 
                                List<Skill> currentSkills, int roomLevel) {
        super.type = "SKILL_SELECTION";
        this.playerId = playerId;
        this.availableSkills = availableSkills;
        this.currentSkills = currentSkills;
        this.roomLevel = roomLevel;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public List<Skill> getAvailableSkills() {
        return availableSkills;
    }
    
    public List<Skill> getCurrentSkills() {
        return currentSkills;
    }
    
    public int getRoomLevel() {
        return roomLevel;
    }
}