package com.dicerealm.core.combat;

import com.dicerealm.core.entity.Entity;
import com.dicerealm.core.item.Weapon;
import com.dicerealm.core.skills.Skill;

public class DamageCalculator {

    public static void applyWeaponDamage(Entity attacker, Entity target, Weapon weapon, boolean isCritHit) {
        int damage = isCritHit ? calculateCritDamage(weapon) : calculateNormalDamage(weapon);

        target.takeDamage(damage); // Update target's HP see @Entity
        System.out.println(attacker.getDisplayName() + " hits " + target.getDisplayName() + " with " + weapon.getDisplayName() +
                " for " + damage + " damage!");
    }

    //TODO: Probably best to split the SkillDamage into like Spells/Others
    public static void applySkillDamage(Entity attacker, Entity target, Skill skill, boolean isCritHit) {
        int damage = isCritHit ? calculateCritDamage(skill) : calculateNormalDamage(skill);

        target.takeDamage(damage);
        System.out.println(attacker.getDisplayName() + " casts " + skill.getDisplayName() + " on " +
                target.getDisplayName() + " for " + damage + " damage!");
    }

    private static int calculateNormalDamage(Weapon weapon) {
        return weapon.rollDamage();
    }

    private static int calculateCritDamage(Weapon weapon) {
        return weapon.rollDamage() + weapon.rollDamage(); // DND 5E Crit 2 x Die Roll
    }

    private static int calculateNormalDamage(Skill skill) {
        return skill.rollDamage();
    }

    private static int calculateCritDamage(Skill skill) {
        return skill.rollDamage() + skill.rollDamage(); // DND 5E Crit 2 x Die Roll
    }

}
