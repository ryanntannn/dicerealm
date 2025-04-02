package com.dicerealm.core.combat.managers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import com.dicerealm.core.combat.CombatLog;
import com.dicerealm.core.combat.CombatResult;
import com.dicerealm.core.combat.systems.InitiativeCalculator;
import com.dicerealm.core.combat.systems.InitiativeResult;
import com.dicerealm.core.dice.D20;
import com.dicerealm.core.dice.FixedD20;
import com.dicerealm.core.entity.Entity;
import com.dicerealm.core.item.Scroll;
import com.dicerealm.core.item.Weapon;
import com.dicerealm.core.monster.Monster;
import com.dicerealm.core.player.Player;
import com.dicerealm.core.skills.Skill;

//TODO: Properly Do CombatManager Sequencing

public class CombatManager {
    private List<Entity> participants;
    private List<Entity> turnOrder;
    private List<InitiativeResult> initiativeResults;
    private ActionManager actionManager;
    private CombatLog combatLog;
    private int currentTurnIndex;

    public CombatManager(){
		}
    public CombatManager(List<Entity> participants) {
				newCombat(participants);
    }

    private List<InitiativeResult> initializeTurnOrder(List<Entity> participants) {
        InitiativeCalculator initiativeCalculator = new InitiativeCalculator();

        for (Entity entity : participants) {
            initiativeResults.add(initiativeCalculator.rollInitiative(entity));
        }

        initiativeResults.sort(Comparator.comparingInt(InitiativeResult::getTotalInitiative).reversed());
        turnOrder.clear();
        for (InitiativeResult result : initiativeResults) {
            turnOrder.add(result.getEntity());
        }       
        currentTurnIndex = 0;
        return initiativeResults;
    }

    private List<InitiativeResult>  initializeRiggedTurnOrder(List<Entity> participants) {
        InitiativeCalculator initiativeCalculator = new InitiativeCalculator();

        for (int i = 0; i < participants.size(); i++) {
            initiativeResults.add(initiativeCalculator.rollRiggedInitiative(participants.get(i), new FixedD20(i + 1)));
        }

        initiativeResults.sort(Comparator.comparingInt(InitiativeResult::getTotalInitiative).reversed());
        turnOrder.clear();
        for (InitiativeResult result : initiativeResults) {
            turnOrder.add(result.getEntity());
        }       
        currentTurnIndex = 0;
        return initiativeResults;
    }

    public void startCombat() {
        initializeTurnOrder(participants);
    }

    public void startRiggedCombat() {
        initializeRiggedTurnOrder(participants);
    }

    public CombatResult executeCombatTurn(Entity attacker, Entity target, Object action) {
        if (!isValidAction(attacker)) {
            combatLog.log(attacker.getDisplayName() + " attempted to act out of turn!");
            return null; // Return null or handle invalid action appropriately
        }
        if (attacker instanceof Player) {
            return performPlayerAction((Player) attacker, target, action);
        } else {
            return actionManager.performAttack(attacker, target, (Weapon) action);
        }
    }

    public CombatResult executeRiggedCombatTurn(Entity attacker, Entity target, Object action, D20 d20) {
        if (!isValidAction(attacker)) {
            combatLog.log(attacker.getDisplayName() + " attempted to act out of turn!");
            return null; // Return null or handle invalid action appropriately
        }
        if (attacker instanceof Player) {
            return performRiggedPlayerAction((Player) attacker, target, action, d20);
        } else {
            actionManager.rigDice(d20);
            return actionManager.performAttack(attacker, target, (Weapon) action);
        }
    }

    private CombatResult performRiggedPlayerAction(Player player, Entity target, Object action, D20 d20) {
        if (action instanceof Skill) {
            actionManager.rigDice(d20); // Set the rigged dice for the action manager
            return actionManager.performSkillAttack(player, target, (Skill) action);
        } else if (action instanceof Weapon) {
            actionManager.rigDice(d20); // Set the rigged dice for the action manager
            return actionManager.performAttack(player, target, (Weapon) action);
        } else if (action instanceof Scroll) {
            actionManager.rigDice(d20); // Set the rigged dice for the action manager
            return actionManager.useScroll(player, target, (Scroll) action);
        }
        return null;
    }

    private CombatResult performPlayerAction(Player player, Entity target, Object action) {
        if (action instanceof Skill) {
            return actionManager.performSkillAttack(player, target, (Skill) action);
        } else if (action instanceof Weapon) {
            return actionManager.performAttack(player, target, (Weapon) action);
        } else if (action instanceof Scroll) {
            return actionManager.useScroll(player, target, (Scroll) action);
        }
        return null;
    }

    public boolean isCombatOver() {
        int alivePlayers = 0, aliveMonsters = 0;
        for (Entity entity : turnOrder) {
            if (entity.isAlive()) {
                if (entity instanceof Player) alivePlayers++;
                else if (entity instanceof Monster) aliveMonsters++;
            }
        }
        boolean combatOver = alivePlayers == 0 || aliveMonsters == 0;
        return combatOver;
    }

    public boolean isValidAction(Entity attacker) {
        return !turnOrder.isEmpty() && turnOrder.get(currentTurnIndex).equals(attacker);
    }

    public Entity getCurrentTurnEntity() {
        return turnOrder.get(currentTurnIndex);
    }

    public void startRound() {
        combatLog.log("A new round of combat begins!");
        currentTurnIndex = 0; // Reset to the first participant
    }

    public void startTurn() {
        Entity currentEntity = getCurrentTurnEntity();
        combatLog.log(currentEntity.getDisplayName() + "'s turn has started.");
        // Additional logic for starting a turn
    }

    public void endTurn() {
        Entity currentEntity = getCurrentTurnEntity();
        combatLog.log(currentEntity.getDisplayName() + "'s turn has ended.");
        currentTurnIndex++;

        // If all participants have acted, the round ends
        if (currentTurnIndex >= participants.size()) {
            combatLog.log("The round has ended.");
            startRound(); // Start a new round
        }
    }

    public List<InitiativeResult> getInitiativeResults() {
        return new ArrayList<>(initiativeResults);
    }
    public Integer getCurrentTurnIndex() {
        return currentTurnIndex;
    }

		public void newCombat(List<Entity> participants) {
			this.participants = participants;
			this.initiativeResults = new ArrayList<>();
			this.turnOrder = new ArrayList<>();
			this.currentTurnIndex = 0;
			this.combatLog = new CombatLog();
			this.actionManager = new ActionManager(combatLog);
		}

		public UUID[] getTurnOrderIds() {
			UUID[] ids = new UUID[turnOrder.size()];
			for (int i = 0; i < turnOrder.size(); i++) {
				ids[i] = turnOrder.get(i).getId();
			}
			return ids;
		}
}
