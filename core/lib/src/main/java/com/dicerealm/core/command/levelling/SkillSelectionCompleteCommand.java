package com.dicerealm.core.command.levelling;

import java.util.UUID;

import com.dicerealm.core.command.Command;
import com.dicerealm.core.skills.SkillSelectionResult;

public class SkillSelectionCompleteCommand extends Command {
    private UUID playerId;
    private SkillSelectionResult result;

    public SkillSelectionCompleteCommand(UUID playerId, SkillSelectionResult result) {
        super.type = "SKILL_SELECTION_COMPLETE";
        this.playerId = playerId;
        this.result = result;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public SkillSelectionResult getResult() {
        return result;
    }
    
}
