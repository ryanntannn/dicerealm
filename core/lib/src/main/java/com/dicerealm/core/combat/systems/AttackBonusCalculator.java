package com.dicerealm.core.combat.systems;

import com.dicerealm.core.combat.ActionType;
import com.dicerealm.core.entity.AbilityModifierCalculator;
import com.dicerealm.core.entity.Entity;
import com.dicerealm.core.entity.EntityClass;
public class AttackBonusCalculator {

    public static int getAttackBonus(Entity attacker, ActionType actionType){

        EntityClass entityClass = attacker.getEntityClass();

        return switch (actionType) {
            case MELEE ->
                // Uses Strength modifier for melee attacks
                    AbilityModifierCalculator.getStrengthModifier(attacker.getStats());
            case RANGED ->
                // Uses Dexterity modifier for ranged attacks
                    AbilityModifierCalculator.getDexterityModifier(attacker.getStats());
            case MAGIC ->
                // Uses Intelligence modifier for magic attacks
                    AbilityModifierCalculator.getIntelligenceModifier(attacker.getStats());
            case SKILL ->
                // Uses the relevant ability modifier based on the skill used
                switch (entityClass) {
                    case WARRIOR -> AbilityModifierCalculator.getStrengthModifier(attacker.getStats());
                    case WIZARD -> AbilityModifierCalculator.getIntelligenceModifier(attacker.getStats());
                    case CLERIC -> AbilityModifierCalculator.getWisdomModifier(attacker.getStats());
                    case ROGUE -> AbilityModifierCalculator.getDexterityModifier(attacker.getStats());
                    case RANGER -> AbilityModifierCalculator.getDexterityModifier(attacker.getStats());
                };
        };
    }
}
