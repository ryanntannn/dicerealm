package com.dicerealm.core.handler;

import java.util.UUID;

import com.dicerealm.core.combat.managers.CombatManager;
import com.dicerealm.core.command.combat.ComandEndTurnCommand;
import com.dicerealm.core.command.combat.CombatStartTurnCommand;
import com.dicerealm.core.command.combat.CombatTurnActionCommand;
import com.dicerealm.core.room.RoomContext;
import com.dicerealm.core.room.RoomState;

public class CombatTurnActionHandler extends CommandHandler<CombatTurnActionCommand> {

    public CombatTurnActionHandler() {
        super("COMBAT_TURN_ACTION");
    }

    @Override
    public void handle(UUID playerId, CombatTurnActionCommand command, RoomContext context) {
        // Validate the command
        if (context.getRoomState().getState() != RoomState.State.BATTLE) {
            throw new IllegalArgumentException("Cannot process combat turn action when the room is not in battle state.");
        }

        CombatManager combatManager = context.getCombatManager();

        // Ensure the action is valid for the current turn
        if (!combatManager.isValidAction(command.getAttacker())) {
            throw new IllegalArgumentException("Invalid action: It is not the attacker's turn.");
        }

        // Execute the combat action
        combatManager.executeCombatTurn(command.getAttacker(), command.getTarget(), command.getActionType());

        // Broadcast the command to all players
        context.getBroadcastStrategy().sendToAllPlayers(command);

        // End the turn
        combatManager.endTurn();
        context.getBroadcastStrategy().sendToAllPlayers(new ComandEndTurnCommand(combatManager.getCurrentTurnIndex()));

        // Check if the combat is over
        if (combatManager.isCombatOver()) {
            context.getRoomState().setState(RoomState.State.DIALOGUE_TURN);
        } else {
            // Start the next turn
            combatManager.startTurn();
            context.getBroadcastStrategy().sendToAllPlayers(new CombatStartTurnCommand(combatManager.getCurrentTurnEntity()));
        }
    }
}