package com.dicerealm.core.combat;

import com.dicerealm.core.entity.Entity;
import com.dicerealm.core.entity.StatsMap;
import com.dicerealm.core.entity.Stat;
import com.dicerealm.core.entity.AbilityModifierCalculator;
public class AttackBonusCalculator {

    public static int getAttackBonus(Entity attacker, ActionType actionType){

        switch (actionType) {
            case MELEE:
                // Uses Strength modifier for melee attacks
                return  AbilityModifierCalculator.getStrengthModifier(attacker.getStats());
            case RANGED:
                // Uses Dexterity modifier for ranged attacks
                return  AbilityModifierCalculator.getDexterityModifier(attacker.getStats());
            case SPELL:
                // TODO: Implement proper spell-casting modifiers, placeholder Intelligence for now
                return  AbilityModifierCalculator.getIntelligenceModifier(attacker.getStats());

        }
    }
}
