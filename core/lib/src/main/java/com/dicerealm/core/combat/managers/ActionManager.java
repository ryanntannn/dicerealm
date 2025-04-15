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
        Weapon equippedWeapon = attacker.getEquippedItems().values().stream()
            .filter(item -> item instanceof Weapon && item.getId().equals(weapon.getId()))
            .map(item -> (Weapon) item)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Attacker does not have the specified weapon equipped."));

        ActionType actionType = determineWeaponActionType(equippedWeapon);
        HitResult hitResult = hitCalculator.doesAttackHit(attacker, target, actionType);
        combatLog.log(hitResult.getHitLog());
        combatResult = new CombatResult(attacker, target, equippedWeapon);
        combatResult.fromHitResult(hitResult);

        if (hitResult.getAttackResult() == AttackResult.HIT || hitResult.getAttackResult() == AttackResult.CRIT_HIT) {
            boolean isCritical = hitResult.getAttackResult() == AttackResult.CRIT_HIT;
            DamageResult damageResult = damageCalculator.applyWeaponDamage(attacker, target, equippedWeapon, isCritical);
            combatResult.fromDamageResult(damageResult);
            combatLog.log(damageCalculator.readout());
        }

        return combatResult;
    }

    /**
     * Handles skill-based attacks.
     */
    public CombatResult performSkillAttack(Entity caster, Entity target, Skill skill) {
        ActionType actionType = skill.getActionType();
        HitResult hitResult = hitCalculator.doesAttackHit(caster, target, actionType);
        
        // Check for skill in caster's inventory and then activates its cooldown
        Skill usedSkill = caster.getSkillsInventory().getItem(skill.getId());
        usedSkill.activateCooldown();

        combatResult = new CombatResult(caster, target, usedSkill);
        combatLog.log(hitResult.getHitLog());
        combatResult.fromHitResult(hitResult);


        if (hitResult.getAttackResult() == AttackResult.HIT || hitResult.getAttackResult() == AttackResult.CRIT_HIT) {
            boolean isCritical = hitResult.getAttackResult() == AttackResult.CRIT_HIT;
            DamageResult damageResult = damageCalculator.applySkillDamage(caster, target, usedSkill, isCritical);
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

    public CombatResult usePotion(Entity user, Potion potion) {
        // Check if the potion is in the user's inventory
        Potion potionInInventory = (Potion) user.getInventory().getItem(potion.getId());
        Entity target = user; // Potions are self-targeting for now

        boolean useable = potionInInventory.useOn(target);
        if (useable) {
            int damage = potionInInventory.rollDamage();
            target.takeDamage(-damage);
            user.getInventory().removeItem(potionInInventory);
            combatResult = new CombatResult(user, target, potionInInventory);
            //Temp as all potions will be healing 
            combatResult.setPotionLog((user.getDisplayName() + " uses " + potionInInventory.getDisplayName() + " on " +
            target.getDisplayName() + " for " + damage + " health!"));
        }
        return combatResult;

    }

    public CombatResult useScroll(Entity caster, Entity target, Scroll scroll) {
        Scroll scrollInInventory = (Scroll) caster.getInventory().getItem(scroll.getId());
        
        boolean useable = scrollInInventory.useOn(target);

        if (useable) {
            ActionType actionType = ActionType.MAGIC;
            HitResult hitResult = hitCalculator.doesAttackHit(caster, target, actionType);
            caster.getInventory().removeItem(scrollInInventory);
            combatResult = new CombatResult(caster, target, scrollInInventory);
            if (hitResult.getAttackResult() == AttackResult.HIT || hitResult.getAttackResult() == AttackResult.CRIT_HIT) {
                boolean isCritical = hitResult.getAttackResult() == AttackResult.CRIT_HIT;
                DamageResult damageResult = damageCalculator.applyScrollDamage(caster, target, scrollInInventory, isCritical);
                combatResult.fromDamageResult(damageResult);
                combatLog.log(damageCalculator.readout());
            }
        }
        
        return combatResult;
    }

}

