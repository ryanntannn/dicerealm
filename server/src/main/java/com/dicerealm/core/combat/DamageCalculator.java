package com.dicerealm.core.combat;

import com.dicerealm.core.entity.Entity;
import com.dicerealm.core.item.Weapon;
import com.dicerealm.core.skills.Skill;

public class DamageCalculator {

    public static void applyWeaponDamage(Entity attacker, Entity target, Weapon weapon) {
        int baseDamage = weapon.rollDamage();
        int totalDamage = Math.max(1, baseDamage); // Check just to ensure min is alw 1 damage

        target.takeDamage(totalDamage); // Update target's HP see @Entity
        System.out.println(attacker.getDisplayName() + " hits " + target.getDisplayName() + " with " + weapon.getDisplayName() +
                " for " + totalDamage + " damage!");
    }

    //TODO: Probably best to split the SkillDamage into like Spells/Others
    public static void applySkillDamage(Entity attacker, Entity target, Skill skill) {
        int baseDamage = skill.rollDamage();
        int totalDamage = Math.max(1, baseDamage);

        target.takeDamage(totalDamage);
        System.out.println(attacker.getDisplayName() + " casts " + skill.getDisplayName() + " on " +
                target.getDisplayName() + " for " + totalDamage + " damage!");
    }

}
