package com.dicerealm.core.combat.managers;

import com.dicerealm.core.combat.CombatLog;
import com.dicerealm.core.combat.CombatResult;
import com.dicerealm.core.entity.Entity;
import com.dicerealm.core.item.Weapon;
import com.dicerealm.core.monster.Monster;
import com.dicerealm.core.player.Player;
import com.dicerealm.core.skills.Skill;
import com.dicerealm.core.util.queue.Queue;
import java.util.List;
import com.dicerealm.core.combat.systems.InitiativeCalculator;
import com.dicerealm.core.combat.systems.InitiativeResult;
import java.util.ArrayList;
import java.util.Comparator;

//TODO: Properly Do CombatManager Sequencing

public class CombatManager {
    private Queue<Entity> turnQueue;
    private ActionManager actionManager;
    private CombatLog combatLog;

    public CombatManager(){}
    public CombatManager(List<Entity> participants) {
        this.turnQueue = new Queue<>();
        this.combatLog = new CombatLog();
        this.actionManager = new ActionManager(combatLog);
        initializeTurnQueue(participants);
    }

    private void initializeTurnQueue(List<Entity> participants) {
        InitiativeCalculator initiativeCalculator = new InitiativeCalculator();
        List<InitiativeResult> initiativeResults = new ArrayList<>();

        for (Entity entity : participants) {
            initiativeResults.add(initiativeCalculator.rollInitiative(entity));
        }

        initiativeResults.sort(Comparator.comparingInt(InitiativeResult::getTotalInitiative).reversed());

        for (InitiativeResult result : initiativeResults) {
            turnQueue.enqueue(result.getEntity());
        }
    }

    public CombatResult startCombat(Object action) {
        while (!isCombatOver()) {
            Entity currentTurn = turnQueue.dequeue();
            if (currentTurn == null || !currentTurn.isAlive()) continue;

            Entity target = getTarget(currentTurn);
            if (target != null) {
                CombatResult combatResult = executeCombatTurn(currentTurn, target, action);
                turnQueue.enqueue(currentTurn);
                return combatResult; // Return the result before the next turn is queued
            }
        }

        System.out.println("Final Combat Log:");
        combatLog.printAllLogs();
        return null; // Return null if combat is over
    }

    private CombatResult executeCombatTurn(Entity attacker, Entity target, Object action) {
        if (attacker instanceof Player) {
            return performPlayerAction((Player) attacker, target, action);
        } else {
            return actionManager.performAttack(attacker, target, attacker.getWeapon());
        }
    }

    private CombatResult performPlayerAction(Player player, Entity target, Object action) {
        if (action instanceof Skill) {
            return actionManager.performSkillAttack(player, target, (Skill) action);
        } else if (action instanceof Weapon) {
            return actionManager.performAttack(player, target, (Weapon) action);
        }
        return null;
    }

    public boolean isCombatOver() {
        int alivePlayers = 0, aliveMonsters = 0;
        List<Entity> entities = new ArrayList<>();

        while (!turnQueue.isEmpty()) {
            Entity entity = turnQueue.dequeue();
            if (entity.isAlive()) {
                if (entity instanceof Player) alivePlayers++;
                else if (entity instanceof Monster) aliveMonsters++;
            }
            entities.add(entity);
        }

        for (Entity entity : entities) {
            turnQueue.enqueue(entity);
        }

        return alivePlayers == 0 || aliveMonsters == 0;
    }

    private Entity getTarget(Entity attacker) {
        List<Entity> entities = new ArrayList<>();
        Entity target = null;

        while (!turnQueue.isEmpty()) {
            Entity entity = turnQueue.dequeue();
            entities.add(entity);
            if (!entity.equals(attacker) && entity.isAlive() && isValidTarget(attacker, entity) && target == null) {
                target = entity;
            }
        }

        for (Entity entity : entities) {
            turnQueue.enqueue(entity);
        }

        return target;
    }

    private boolean isValidTarget(Entity attacker, Entity target) {
        return (attacker instanceof Player && target instanceof Monster) ||
                (attacker instanceof Monster && target instanceof Player);
    }
}
