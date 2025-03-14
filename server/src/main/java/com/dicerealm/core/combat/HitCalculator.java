package com.dicerealm.core.combat;

import static com.dicerealm.core.combat.AttackBonusCalculator.getAttackBonus;

import com.dicerealm.core.dice.D20;
import com.dicerealm.core.entity.Entity;
import com.dicerealm.core.entity.Stat;
public class HitCalculator {
    private D20 d20 = new D20();
    // TODO: Reorganise Weapon/Skill System to include ActionTypes
    // TODO: Possibly include Proficiencies
    public boolean doesAttackHit(Entity attacker, Entity target, ActionType actionType) {
        int attackRoll = d20.roll();
        int attackBonus = getAttackBonus(attacker, actionType);
        int targetAC = target.getStat(Stat.ARMOUR_CLASS);
        int totalRoll = attackRoll + attackBonus;

        String HitLog = String.format("%s rolls a d20: %d + Attack Bonus (%d) = %d vs AC %d -> %s",
                attacker.getDisplayName(), attackRoll, attackBonus, totalRoll, targetAC,
                (totalRoll >= targetAC ? "HIT!" : "MISS!"));
        System.out.println(HitLog);
        return (attackRoll) >= targetAC;
    }
}
