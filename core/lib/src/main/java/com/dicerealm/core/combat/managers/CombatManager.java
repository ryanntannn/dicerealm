package com.dicerealm.core.combat.managers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.dicerealm.core.combat.ActionType;
import com.dicerealm.core.combat.CombatLog;
import com.dicerealm.core.combat.CombatResult;
import com.dicerealm.core.combat.systems.InitiativeCalculator;
import com.dicerealm.core.combat.systems.InitiativeResult;
import com.dicerealm.core.dice.D20;
import com.dicerealm.core.dice.FixedD20;
import com.dicerealm.core.entity.Entity;
import com.dicerealm.core.entity.Stat;
import com.dicerealm.core.entity.StatsMap;
import com.dicerealm.core.item.Potion;
import com.dicerealm.core.item.Scroll;
import com.dicerealm.core.item.Weapon;
import com.dicerealm.core.item.WeaponClass;
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
    private int currentRoundIndex;

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
        } else if (attacker instanceof Monster) {
            return performMonsterAction((Monster) attacker, target, action);
        } else 
        return null; // Return null or handle invalid action appropriately
    }

    public CombatResult executeRiggedCombatTurn(Entity attacker, Entity target, Object action, D20 d20) {
        if (!isValidAction(attacker)) {
            combatLog.log(attacker.getDisplayName() + " attempted to act out of turn!");
            return null; // Return null or handle invalid action appropriately
        }
        if (attacker instanceof Player) {
            return performRiggedPlayerAction((Player) attacker, target, action, d20);
        } else if (attacker instanceof Monster){
            actionManager.rigDice(d20);
            return performRiggedMonsterAction((Monster) attacker, target, action, d20);
        }

        return null; // Return null or handle invalid action appropriately
    }

    private CombatResult performRiggedPlayerAction(Player player, Entity target, Object action, D20 d20) {
        switch (action) {
            case Skill skill -> {
                if (skill.isUsable()) {
                    skill.activateCooldown(); // Start the cooldown for the skill
                    actionManager.rigDice(d20);
                    return actionManager.performSkillAttack(player, target, (Skill) action);
                } else {
                    combatLog.log(player.getDisplayName() + " tried to use " + skill.getDisplayName() + ", but it's on cooldown!");
                }
            }
            case Weapon weapon -> {
                actionManager.rigDice(d20);
                return actionManager.performAttack(player, target, weapon);
            }
            case Scroll scroll -> {
                actionManager.rigDice(d20);
                return actionManager.useScroll(player, target, scroll);
            }
            case Potion potion -> {
                actionManager.rigDice(d20);
                return actionManager.usePotion(player, target, potion);
            }
            default -> {
                combatLog.log(player.getDisplayName() + " attempted an unknown action!");
                return null;
            }
        }
        return null;
    }

    private CombatResult performPlayerAction(Player player, Entity target, Object action) {
        switch (action) {
            case Skill skill -> {
                if (skill.isUsable()) {
                    skill.activateCooldown(); // Start the cooldown for the skill
                    return actionManager.performSkillAttack(player, target, (Skill) action);
                } else {
                    combatLog.log(player.getDisplayName() + " tried to use " + skill.getDisplayName() + ", but it's on cooldown!");
                }
            }
            case Weapon weapon -> {
                return actionManager.performAttack(player, target, weapon);
            }
            case Scroll scroll -> {
                return actionManager.useScroll(player, target, scroll);
            }
            case Potion potion -> {
                return actionManager.usePotion(player, target, potion);
            }
            default -> {
                // combatLog.log(player.getDisplayName() + " attempted an unknown action!");
                // return null;
                return actionManager.performAttack(player, target, new Weapon("Fists", "Fists of Furry", ActionType.MELEE, WeaponClass.SWORD, new StatsMap(Map.of(Stat.STRENGTH, 0)), 4 )); // Default to a basic attack if action is unknown
            }
        }
        return null;
    }

    private CombatResult performMonsterAction(Monster monster, Entity target, Object action) {
        switch (action) {
            case Skill skill -> {
                if (skill.isUsable()) {
                    skill.activateCooldown(); // Start the cooldown for the skill
                    return actionManager.performSkillAttack(monster, target, (Skill) action);
                } else {
                    combatLog.log(monster.getDisplayName() + " tried to use " + skill.getDisplayName() + ", but it's on cooldown!");
                }
            }
            case Weapon weapon -> {
                return actionManager.performAttack(monster, target, weapon);
            }
            default -> {
                combatLog.log(monster.getDisplayName() + " attempted an unknown action!");
                return null;
            }
        }
        return null;
    }

    private CombatResult performRiggedMonsterAction(Monster monster, Entity target, Object action, D20 d20) {
        switch (action) {
            case Skill skill -> {
                if (skill.isUsable()) {
                    skill.activateCooldown(); // Start the cooldown for the skill
                    actionManager.rigDice(d20); // Example of rigging the dice
                    return actionManager.performSkillAttack(monster, target, (Skill) action);
                } else {
                    combatLog.log(monster.getDisplayName() + " tried to use " + skill.getDisplayName() + ", but it's on cooldown!");
                }
            }
            case Weapon weapon -> {
                actionManager.rigDice(d20); // Example of rigging the dice
                return actionManager.performAttack(monster, target, weapon);
            }
            default -> {
                combatLog.log(monster.getDisplayName() + " attempted an unknown action!");
                return null;
            }
        }
        return null;
    }

    
    private void removeDeadEntities() {
        turnOrder.removeIf(entity -> !entity.isAlive());
    }

    public boolean isCombatOver() {
        int alivePlayers = 0, aliveMonsters = 0;
        for (Entity entity : participants) {
            if (entity.isAlive()) {
                if (entity instanceof Player) alivePlayers++;
                else if (entity instanceof Monster) aliveMonsters++;
            }
        }
        boolean combatOver = alivePlayers == 0 || aliveMonsters == 0;
        return combatOver;
    }

		public boolean isPlayersWin() {
			int alivePlayers = 0, aliveMonsters = 0;
			for (Entity entity : participants) {
				if (entity.isAlive()) {
					if (entity instanceof Player) alivePlayers++;
					else if (entity instanceof Monster) aliveMonsters++;
				}
			}
			return alivePlayers > 0 && aliveMonsters == 0;
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
        removeDeadEntities(); // Remove dead entities from the turn order

        // If all participants have acted, the round ends
        if (currentTurnIndex >= turnOrder.size()) {
            combatLog.log("The round has ended.");
            reduceAllSkillCooldowns();
            startRound(); // Start a new round
            currentRoundIndex++;
        }
    }

    public void reduceAllSkillCooldowns() {
        combatLog.log("A full round has ended. Reducing skill cooldowns for all participants.");
        for (Entity entity : participants) {
            entity.getSkillsInventory().getItems().forEach(Skill::reduceCooldown);
        }
    }

    public void removePlayerFromCombat(UUID playerId){
        Entity playerToRemove = participants.stream()
        .filter(entity -> entity.getId().equals(playerId))
        .findFirst()
        .orElse(null);

    if (playerToRemove != null) {
        participants.remove(playerToRemove);
        turnOrder.remove(playerToRemove);

        combatLog.log(playerToRemove.getDisplayName() + " has left the combat.");

        if (currentTurnIndex >= turnOrder.size()) {
            currentTurnIndex = 0;
        }

        if (isCombatOver()) {
            combatLog.log("Combat has ended due to player leaving.");
        }
    }

    }

    public List<InitiativeResult> getInitiativeResults() {
        return new ArrayList<>(initiativeResults);
    }
    public Integer getCurrentTurnIndex() {
        return currentTurnIndex;
    }

    public Integer getCurrentRoundIndex() {
        return currentRoundIndex;
    }

    public void newCombat(List<Entity> participants) {
        this.participants = participants;
        this.initiativeResults = new ArrayList<>();
        this.turnOrder = new ArrayList<>();
        this.currentTurnIndex = 0;
        this.combatLog = new CombatLog();
        this.actionManager = new ActionManager(combatLog);
        this.currentRoundIndex = 1;
    }

    public List<Entity> getParticipants() {
        return participants;
    }

    public UUID[] getTurnOrderIds() {
        UUID[] ids = new UUID[turnOrder.size()];
        for (int i = 0; i < turnOrder.size(); i++) {
            ids[i] = turnOrder.get(i).getId();
        }
        return ids;
    }

    public Entity getEntityById(UUID id) {
        for (Entity entity : participants) {
            if (entity.getId().equals(id)) {
                return entity;
            }
        }
        return null;
    }
}
