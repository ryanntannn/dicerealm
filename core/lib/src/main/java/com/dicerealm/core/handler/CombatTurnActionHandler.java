package com.dicerealm.core.handler;

import java.util.UUID;

import com.dicerealm.core.combat.CombatResult;
import com.dicerealm.core.combat.managers.CombatManager;
import com.dicerealm.core.combat.managers.LevelManager;
import com.dicerealm.core.combat.managers.MonsterAI;
import com.dicerealm.core.command.combat.CombatStartTurnCommand;
import com.dicerealm.core.command.combat.CombatTurnActionCommand;
import com.dicerealm.core.command.combat.CombatEndCommand.CombatEndStatus;
import com.dicerealm.core.command.combat.CombatEndCommand;
import com.dicerealm.core.command.combat.CombatEndTurnCommand;
import com.dicerealm.core.dialogue.DialogueManager;
import com.dicerealm.core.dm.DungeonMasterResponse;
import com.dicerealm.core.entity.Entity;
import com.dicerealm.core.entity.Entity.Allegiance;
import com.dicerealm.core.monster.Monster;
import com.dicerealm.core.player.Player;
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

				Entity attacker = combatManager.getEntityById(command.getAttacker().getId());

				if (attacker == null) {
					throw new IllegalArgumentException("Invalid attacker: Entity not found.");
				}
        // Ensure the action is valid for the current turn
        if (!combatManager.isValidAction(attacker)) {
					throw new IllegalArgumentException("Invalid action: It is not the attacker's turn.");
				}

				Entity target = combatManager.getEntityById(command.getTarget().getId());
				if (target == null) {
					throw new IllegalArgumentException("Invalid target: Entity not found.");
				}

        // Execute the combat action
        CombatResult result = combatManager.executeCombatTurn((Player)attacker, (Monster)target, command.getAction());

				int currentTurnIndex = combatManager.getCurrentTurnIndex();

        // End the turn
        combatManager.endTurn();

				// Broadcast the end of turn command containing the result
        context.getBroadcastStrategy().sendToAllPlayers(new CombatEndTurnCommand(currentTurnIndex, result));

				if (combatManager.isCombatOver()) {
					handleCombatOver(context);
				} else {
					handleNextTurn(context);
				}
    }

		public static void handleNextTurn(RoomContext context) {
			CombatManager combatManager = context.getCombatManager();
			MonsterAI monsterAI = context.getMonsterAI();

			System.err.println("CombatTurnActionHandler.handleNextTurn: " + combatManager.getCurrentTurnEntity().getDisplayName() + " is taking their turn.");
			
			while (combatManager.getCurrentTurnEntity().getAllegiance() == Allegiance.ENEMY) {
				combatManager.startTurn();
				context.getBroadcastStrategy().sendToAllPlayers(new CombatStartTurnCommand(combatManager.getCurrentTurnIndex(),  combatManager.getCurrentTurnEntity().getId()));
				CombatResult monsterResult = monsterAI.handleMonsterTurn(combatManager.getParticipants(), combatManager.getCurrentTurnEntity());
        int currentTurn = combatManager.getCurrentTurnIndex();
				combatManager.endTurn();
				context.getBroadcastStrategy().sendToAllPlayers(new CombatEndTurnCommand(currentTurn, monsterResult));
				if (combatManager.isCombatOver()) {
					handleCombatOver(context);
					return;
				}
			}

			// Check if the combat is over
			if (combatManager.isCombatOver()) {
					handleCombatOver(context);
			} else {
					// Start the next turn
					combatManager.startTurn();
					context.getBroadcastStrategy().sendToAllPlayers(new CombatStartTurnCommand(combatManager.getCurrentTurnIndex(),  combatManager.getCurrentTurnEntity().getId()));
			}
		}

		public static void handleCombatOver(RoomContext context) {
			if (context.getCombatManager().isPlayersWin()){
				context.getBroadcastStrategy().sendToAllPlayers(new CombatEndCommand(CombatEndStatus.WIN));
				int totalXP = context.getCombatManager().getParticipants().stream()
					.filter(entity -> entity instanceof Monster && !entity.isAlive())
					.mapToInt(entity -> ((Monster) entity).getXpValue())
					.sum();
				LevelManager levelManager = new LevelManager();
				levelManager.addExperience(totalXP, context.getRoomState());
				// TODO: Handle prompt for the DM to end the combat
				DungeonMasterResponse response = context.getDungeonMaster().handleEndCombat(true);
				DialogueManager.handleDungeonMasterResponse(response, context);
			} else {
				context.getBroadcastStrategy().sendToAllPlayers(new CombatEndCommand(CombatEndStatus.LOSE));
			}
		}
}