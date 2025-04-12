package com.dicerealm.core.combat.managers;

import com.dicerealm.core.combat.ActionType;
import com.dicerealm.core.combat.CombatLog;
import com.dicerealm.core.combat.CombatResult;
import com.dicerealm.core.combat.systems.AttackResult;
import com.dicerealm.core.combat.systems.DamageCalculator;
import com.dicerealm.core.combat.systems.DamageResult;
import com.dicerealm.core.combat.systems.HitCalculator;
import com.dicerealm.core.combat.systems.HitResult;
import com.dicerealm.core.dice.D20;
import com.dicerealm.core.entity.Entity;
import com.dicerealm.core.item.Potion;
import com.dicerealm.core.item.Scroll;
import com.dicerealm.core.item.Weapon;
import com.dicerealm.core.skills.Skill;

//TODO:Currently Incomplete Pushing Old Version with CombatLog to test if Logger Works Properly
public class ActionManager {
    private CombatLog combatLog;
    private CombatResult combatResult;
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
    public CombatResult performAttack(Entity attacker, Entity target, Weapon weapon) {
        ActionType actionType = determineWeaponActionType(weapon);
        HitResult hitResult = hitCalculator.doesAttackHit(attacker, target, actionType);
        combatLog.log(hitResult.getHitLog());
        combatResult = new CombatResult(attacker, target, weapon);
        combatResult.fromHitResult(hitResult);

        if (hitResult.getAttackResult() == AttackResult.HIT || hitResult.getAttackResult() == AttackResult.CRIT_HIT) {
            boolean isCritical = hitResult.getAttackResult() == AttackResult.CRIT_HIT;
            DamageResult damageResult = damageCalculator.applyWeaponDamage(attacker, target, weapon, isCritical);
            combatResult.fromDamageResult(damageResult);
            combatLog.log(damageCalculator.readout());
        }

        return combatResult;
    }

    /**
     * Handles skill-based attacks.
     */
    public CombatResult performSkillAttack(Entity caster, Entity target, Skill skill) {
        ActionType actionType = ActionType.SKILL; // All skills are of type Skill attack
        HitResult hitResult = hitCalculator.doesAttackHit(caster, target, actionType);
        combatResult = new CombatResult(caster, target, skill);
        combatLog.log(hitResult.getHitLog());
        combatResult.fromHitResult(hitResult);


        if (hitResult.getAttackResult() == AttackResult.HIT || hitResult.getAttackResult() == AttackResult.CRIT_HIT) {
            boolean isCritical = hitResult.getAttackResult() == AttackResult.CRIT_HIT;
            DamageResult damageResult = damageCalculator.applySkillDamage(caster, target, skill, isCritical);
            combatResult.fromDamageResult(damageResult);
            combatLog.log(damageCalculator.readout());
        }

        return combatResult;
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

    public CombatResult usePotion(Entity user, Entity target, Potion potion) {
        boolean useable = potion.useOn(target);
        if (useable) {
            int damage = potion.rollDamage();
            target.takeDamage(-damage);
            combatResult = new CombatResult(user, target, potion);
            //Temp as all potions will be healing 
            combatResult.setPotionLog((user.getDisplayName() + " uses " + potion.getDisplayName() + " on " +
            target.getDisplayName() + " for " + damage + " health!"));
            user.getInventory().removeItem(potion);
        }
        return combatResult;

    }

    public CombatResult useScroll(Entity caster, Entity target, Scroll scroll) {
        boolean useable = false;
        useable = scroll.useOn(target);

        if (useable) {
            ActionType actionType = ActionType.MAGIC;
            HitResult hitResult = hitCalculator.doesAttackHit(caster, target, actionType);
            combatResult = new CombatResult(caster, target, scroll);
            if (hitResult.getAttackResult() == AttackResult.HIT || hitResult.getAttackResult() == AttackResult.CRIT_HIT) {
                boolean isCritical = hitResult.getAttackResult() == AttackResult.CRIT_HIT;
                DamageResult damageResult = damageCalculator.applyScrollDamage(caster, target, scroll, isCritical);
                combatResult.fromDamageResult(damageResult);
                combatLog.log(damageCalculator.readout());
            }
            caster.getInventory().removeItem(scroll);
        }
        
        return combatResult;
    }

}

