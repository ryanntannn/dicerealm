package com.dicerealm.core.combat;

import com.dicerealm.core.entity.Entity;
import com.dicerealm.core.item.Weapon;
import com.dicerealm.core.skills.Skill;

/**
 * DamageCalculator has main 2 methods applyWeaponDamage & applySkillDamage w/ 4 Args attacker, target, weapon/skill, isCrit
 * attacker is the entity performing the action, target is the who's being targeted, weapon/skill is the item/skill used, isCrit is a boolean for whether the attack Crit
 * applyWeaponDamage is used for Weapons, applySkillDamage is used for Skills, both methods will switch from normal/crit depending on if isCritHit is True or False
 * Actual DamageCalc is done using helper methods, which uses the inbuilt rollDamage() methods built into Weapons and SKill classes
 * @see Weapon
 * @see Skill
 * each method updates target hp after damageCalc is done
 * readout method for printing and logging
 *
 * @author Darren
 */

public class DamageCalculator {

    private static String damageLog = "";

    public static void applyWeaponDamage(Entity attacker, Entity target, Weapon weapon, boolean isCritHit) {
        int damage = isCritHit ? calculateCritDamage(weapon) : calculateNormalDamage(weapon);

        target.takeDamage(damage); // Update target's HP see @Entity
        damageLog = (attacker.getDisplayName() + " hits " + target.getDisplayName() + " with " + weapon.getDisplayName() +
                " for " + damage + " damage!");
    }

    //TODO: Probably best to split the SkillDamage into like Spells/Others
    public static void applySkillDamage(Entity attacker, Entity target, Skill skill, boolean isCritHit) {
        int damage = isCritHit ? calculateCritDamage(skill) : calculateNormalDamage(skill);

        target.takeDamage(damage);
        damageLog = (attacker.getDisplayName() + " casts " + skill.getDisplayName() + " on " +
                target.getDisplayName() + " for " + damage + " damage!");
    }

    //Helper Methods to roll for each Damaage Type
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

    //Helper Method to print damageLog
    public static String readout() {
        return damageLog;
    }
}
