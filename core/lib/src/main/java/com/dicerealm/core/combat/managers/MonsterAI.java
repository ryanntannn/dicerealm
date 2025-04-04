package com.dicerealm.core.combat.managers;

import java.util.List;
import java.util.Optional;

import com.dicerealm.core.combat.CombatResult;
import com.dicerealm.core.entity.Entity;
import com.dicerealm.core.entity.Entity.Allegiance;
import com.dicerealm.core.item.Weapon;
import com.dicerealm.core.skills.Skill;

public class MonsterAI {

    private CombatManager combatManager;

    public MonsterAI() {
    }

    /**
     * Handles the monster's attack logic.
     *
     * @param participants The list of all combat participants.
     * @param currentEntity The current monster entity taking its turn.
     * @return The result of the combat action.
     */
    public CombatResult handleMonsterTurn(List<Entity> participants, Entity monster) {
        if (monster.getAllegiance() != Allegiance.ENEMY) {
            throw new IllegalArgumentException("Current entity is not an enemy.");
        }

        // Determine the target based on some strategy (e.g., lowest health)
        Entity target = participants.stream()
            .filter(entity -> entity.getAllegiance() == Allegiance.PLAYER)
            .min((e1, e2) -> Integer.compare(e1.getHealth(), e2.getHealth()))
            .orElseThrow(() -> new IllegalArgumentException("No valid player targets available."));

        // Search for a weapon or skill to use
        Optional<Weapon> weapon = monster.getEquippedItems().values().stream()
            .filter(item -> item instanceof Weapon)
            .map(item -> (Weapon) item)
            .max((w1, w2) -> Integer.compare(w1.getDamageDice().getSides(),
                                             w2.getDamageDice().getSides()));

        Optional<Skill> skill = monster.getSkillsInventory().getItems().stream()
            .filter(Skill::isUsable)
            .max((s1, s2) -> Integer.compare(
                s1.getDamageDice().getNumDice() * s1.getDamageDice().getSides(),
                s2.getDamageDice().getNumDice() * s2.getDamageDice().getSides())); // Compare skills by max damage

        Object action = null;
        boolean hasSkill = skill.isPresent();
        boolean hasWeapon = weapon.isPresent();
        boolean skillStrongerThanWeapon = hasSkill && (!hasWeapon || 
            skill.get().getMaxDamage() >= weapon.get().getMaxDamage());

        if (skillStrongerThanWeapon) {
            action = skill.get();
        } else if (weapon.isPresent()) {
            action = weapon.get();
        } else {
            throw new IllegalStateException("Monster has no usable skills or weapons.");
        }

        // Execute the combat turn
        CombatResult result = combatManager.executeCombatTurn(monster, target, action);
        return result;
    }

    public void setCombatManager(CombatManager combatManager) {
        this.combatManager = combatManager;
    }
}