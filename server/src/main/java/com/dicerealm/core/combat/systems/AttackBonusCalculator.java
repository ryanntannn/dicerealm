package com.dicerealm.core.combat.systems;

import com.dicerealm.core.combat.ActionType;
import com.dicerealm.core.entity.Entity;
import com.dicerealm.core.entity.AbilityModifierCalculator;
public class AttackBonusCalculator {

    public static int getAttackBonus(Entity attacker, ActionType actionType){

        return switch (actionType) {
            case MELEE ->
                // Uses Strength modifier for melee attacks
                    AbilityModifierCalculator.getStrengthModifier(attacker.getStats());
            case RANGED ->
                // Uses Dexterity modifier for ranged attacks
                    AbilityModifierCalculator.getDexterityModifier(attacker.getStats());
            case MAGIC, SKILL ->
                // TODO: Implement proper spell-casting modifiers, placeholder Intelligence for now
                    AbilityModifierCalculator.getIntelligenceModifier(attacker.getStats());
        };
    }
}
