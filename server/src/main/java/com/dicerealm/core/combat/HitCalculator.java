package com.dicerealm.core.combat;

import static com.dicerealm.core.combat.AttackBonusCalculator.getAttackBonus;

import com.dicerealm.core.dice.D20;
import com.dicerealm.core.entity.Entity;
import com.dicerealm.core.entity.Stat;
public class HitCalculator {
    private D20 d20 = new D20();

    private String hitLog = "";

    // Custom D20 For Testing
    public HitCalculator(D20 d20) {
        this.d20 = d20;
    }

    // TODO: Reorganise Weapon/Skill System to include ActionTypes
    // TODO: Possibly include Proficiencies
    public AttackResult doesAttackHit(Entity attacker, Entity target, ActionType actionType) {
        int attackRoll = d20.roll();
        int attackBonus = getAttackBonus(attacker, actionType);
        int targetAC = target.getStat(Stat.ARMOUR_CLASS);
        int totalRoll = attackRoll + attackBonus;

        if (attackRoll == 20) {
            hitLog = (attacker.getDisplayName() + " rolls a NATURAL 20! CRITICAL HIT!");
            return AttackResult.CRIT_HIT;
        }

        if (attackRoll == 1) {
            hitLog = (attacker.getDisplayName() + " rolls a NATURAL 1! CRITICAL MISS!");
            return AttackResult.CRIT_MISS;
        }

        boolean hit = totalRoll >= targetAC;
        hitLog = String.format("%s rolls a d20: %d + Attack Bonus (%d) = %d vs AC %d -> %s",
                attacker.getDisplayName(), attackRoll, attackBonus, totalRoll, targetAC,
                (totalRoll >= targetAC ? "HIT!" : "MISS!"));
        return hit ? AttackResult.HIT : AttackResult.MISS;
    }

    public String readout() {
        return hitLog;
    }
}
