package com.dicerealm.core.combat;

import com.dicerealm.core.entity.Entity;
import com.dicerealm.core.util.queue.Queue;
import java.util.List;

//TODO: Properly Do CombatManager Sequencing

public class CombatManager {
    private Queue<Entity> turnQueue; // Use custom queue
    private ActionManager actionManager;
    private CombatLog combatLog;

    public CombatManager(){}
    public CombatManager(List<Entity> participants) {
        this.turnQueue = new Queue<>(); // Initialize queue
        this.combatLog = new CombatLog();
        this.actionManager = new ActionManager(combatLog);

        for (Entity entity : participants) {
            turnQueue.enqueue(entity); // Add participants to queue
        }
    }

    public void startCombat() {
        while (!isCombatOver()) {
            Entity currentTurn = turnQueue.dequeue(); // Get next entity in queue
            if (currentTurn == null || !currentTurn.isAlive()) continue; // Skip dead entities

            Entity target = getTarget(currentTurn);
            if (target != null) {
                actionManager.performAttack(currentTurn, target, currentTurn.getWeapon());
            }

            turnQueue.enqueue(currentTurn); // Add entity back to end of queue
        }

        System.out.println("Final Combat Log:");
        combatLog.printAllLogs();
    }

    private boolean isCombatOver() {
        int aliveCount = 0;
        Queue<Entity> tempQueue = new Queue<>();

        while (!turnQueue.isEmpty()) {
            Entity entity = turnQueue.dequeue();
            if (entity.isAlive()) aliveCount++;
            tempQueue.enqueue(entity);
        }

        while (!tempQueue.isEmpty()) {
            turnQueue.enqueue(tempQueue.dequeue());
        }

        return aliveCount <= 1; // Combat ends when only one entity remains
    }

    private Entity getTarget(Entity attacker) {
        Queue<Entity> tempQueue = new Queue<>();
        Entity target = null;

        while (!turnQueue.isEmpty()) {
            Entity entity = turnQueue.dequeue();
            if (!entity.equals(attacker) && entity.isAlive() && target == null) {
                target = entity;
            }
            tempQueue.enqueue(entity);
        }

        while (!tempQueue.isEmpty()) {
            turnQueue.enqueue(tempQueue.dequeue());
        }

        return target;
    }
}

