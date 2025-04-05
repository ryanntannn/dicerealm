package com.dicerealm.core.command.levelling;

import java.util.UUID;

import com.dicerealm.core.command.Command;

/**
 * Command sent from client to server with the player's skill selection.
 */
public class SkillSelectionResponseCommand extends Command {
    private UUID playerId;
    private UUID selectedSkillId;
    private UUID replacedSkillId; // Can be null if not replacing any skill

    public SkillSelectionResponseCommand(UUID playerId, UUID selectedSkillId, UUID replacedSkillId) {
        super.type = "SKILL_SELECTION_RESPONSE";
        this.playerId = playerId;
        this.selectedSkillId = selectedSkillId;
        this.replacedSkillId = replacedSkillId;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public UUID getSelectedSkillId() {
        return selectedSkillId;
    }

    public UUID getReplacedSkillId() {
        return replacedSkillId;
    }
}