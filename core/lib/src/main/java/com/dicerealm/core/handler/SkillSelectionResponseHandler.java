package com.dicerealm.core.handler;

import java.util.UUID;

import com.dicerealm.core.command.levelling.SkillSelectionCommand;
import com.dicerealm.core.command.levelling.SkillSelectionCompleteCommand;
import com.dicerealm.core.command.levelling.SkillSelectionResponseCommand;
import com.dicerealm.core.player.Player;
import com.dicerealm.core.room.RoomContext;
import com.dicerealm.core.skills.SkillSelectionProcessor;
import com.dicerealm.core.skills.SkillSelectionResult;

/**
 * Handles skill selection responses from players.
 * Processes the selection and broadcasts the result to all players.
 */
public class SkillSelectionResponseHandler extends CommandHandler<SkillSelectionResponseCommand> {
    
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

        // Process the skill selection using SkillSelectionProcessor
        SkillSelectionResult result = SkillSelectionProcessor.processSkillSelection(
            playerId,
            command.getSelectedSkillId(),
            command.getReplacedSkillId(),
            context.getLevelManager(),
            player
        );

        context.getBroadcastStrategy().sendToPlayer(new SkillSelectionCompleteCommand(playerId, result), player);

        if (!result.isSuccess() && result.getAvailableSkills() != null && !result.getAvailableSkills().isEmpty()) {
            // Send a new SkillSelectionCommand to prompt the player
            int roomLevel = context.getRoomState().getRoomLevel();
            context.getBroadcastStrategy().sendToPlayer(
                new SkillSelectionCommand(
                    player.getId(),
                    result.getAvailableSkills(),
                    player.getSkillsInventory().getItems(),
                    roomLevel
                )
                ,player
            );
        }
    }

    
    
    /**
     * Checks if any players still have pending skill selections.
     */
    private boolean anyPendingSkillSelections(RoomContext context) {
        for (Player player : context.getRoomState().getPlayerMap().values()) {
            if (context.getLevelManager().hasPendingSkillSelection(player.getId())) {
                return true;
            }
        }
        return false;
    }
}