package com.dicerealm.core.combat;

import com.dicerealm.core.dice.D20;
import com.dicerealm.core.entity.Entity;
import com.dicerealm.core.item.Weapon;
import com.dicerealm.core.skills.Skill;

//TODO:Currently Incomplete Pushing Old Version with CombatLog to test if Logger Works Properly
public class ActionManager {
    private CombatLog combatLog;
    private HitCalculator hitCalculator;
    private DamageCalculator damageCalculator = new DamageCalculator();

    public ActionManager(CombatLog combatLog) {
        this.combatLog = combatLog;
        this.hitCalculator = new HitCalculator(); // Default hit calculator
    }

    // Method to rig the dice for testing
    public void rigDice(D20 riggedDice) {
        this.hitCalculator = new HitCalculator(riggedDice); // Inject custom dice for hit calculations
    }


    /**
     * Handles a basic melee or ranged attack action.
     */
    public void performAttack(Entity attacker, Entity target, Weapon weapon) {
        ActionType actionType = determineWeaponActionType(weapon);
        HitResult hitResult = hitCalculator.doesAttackHit(attacker, target, actionType);
        combatLog.log(hitResult.getLogMessage());

        if (hitResult.getAttackResult() == AttackResult.HIT || hitResult.getAttackResult() == AttackResult.CRIT_HIT) {
            boolean isCritical = hitResult.getAttackResult() == AttackResult.CRIT_HIT;
            damageCalculator.applyWeaponDamage(attacker, target, weapon, isCritical);
            combatLog.log(damageCalculator.readout());
        }
    }

    /**
     * Handles skill-based attacks.
     */
    public void performSkillAttack(Entity caster, Entity target, Skill skill) {
        ActionType actionType = ActionType.SKILL; // All skills are of type Skill attack
        HitResult hitResult = hitCalculator.doesAttackHit(caster, target, actionType);
        combatLog.log(hitResult.getLogMessage());

        if (hitResult.getAttackResult() == AttackResult.HIT || hitResult.getAttackResult() == AttackResult.CRIT_HIT) {
            boolean isCritical = hitResult.getAttackResult() == AttackResult.CRIT_HIT;
            damageCalculator.applySkillDamage(caster, target, skill, isCritical);
            combatLog.log(damageCalculator.readout());
        }
    }

    /**
     * Determines whether a weapon attack is melee or ranged based on the weapon type.
     */
    private ActionType determineWeaponActionType(Weapon weapon) {
        return weapon.getActionType().isMelee() ? ActionType.MELEE: ActionType.RANGED ;
    }

    /*private ActionType determineSkillActionType(Weapon weapon) {
        return Skill.getActionType().isMelee() ? ActionType.MELEE: ActionType.RANGED ;
    }*/
}

