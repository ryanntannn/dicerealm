package com.dicerealm.core.combat;

import static com.dicerealm.core.combat.AttackBonusCalculator.getAttackBonus;

import com.dicerealm.core.dice.D20;
import com.dicerealm.core.entity.Entity;
import com.dicerealm.core.entity.Stat;

/**
 * HitCalculator has 1 main method w/ 3 Args attacker, target, actionType
 * attacker is the entity performing the action, target is the who's being targeted
 * actionType is the type of attack action the attacker is using
 * @see ActionType
 * HitCalculator will output 1 of 4 possible enum outcomes, CRIT_HIT, HIT, CRIT_MISS, MISS
 * @see AttackResult
 * System uses DND5E rules for calculation, ie attackRoll => targetArmor_Class
 *
 * @author Darren
 */

public class HitCalculator {
    private D20 d20 = new D20();

    // Default Constructor
    public HitCalculator() {
    }

    // Custom D20 For Testing
    public HitCalculator(D20 d20) {
        this.d20 = d20;
    }

    // TODO: Possibly include Proficiencies
    public HitResult doesAttackHit(Entity attacker, Entity target, ActionType actionType) {
        int attackRoll = d20.roll();
        int attackBonus = getAttackBonus(attacker, actionType);
        int targetAC = target.getStat(Stat.ARMOUR_CLASS);
        int totalRoll = attackRoll + attackBonus; // Takes into account player modifiers see @AttackBonusCalculator

        AttackResult attackResult;
        String hitLog;

        // Case when a Nat 20 is rolled
        if (attackRoll == 20) {
            attackResult = AttackResult.CRIT_HIT;
            hitLog = attacker.getDisplayName() + " rolls a NATURAL 20! CRITICAL HIT!";
        }
        // Case when a Nat 1 is rolled
        else if (attackRoll == 1) {
            attackResult = AttackResult.CRIT_MISS;
            hitLog = attacker.getDisplayName() + " rolls a NATURAL 1! CRITICAL MISS!";
        }
        // General case for normal hits or misses
        else {
            boolean hit = totalRoll >= targetAC;
            attackResult = hit ? AttackResult.HIT : AttackResult.MISS;
            hitLog = String.format("%s rolls a d20: %d + Attack Bonus (%d) = %d vs AC %d -> %s",
                    attacker.getDisplayName(), attackRoll, attackBonus, totalRoll, targetAC,
                    (hit ? "HIT!" : "MISS!"));

        }

        return new HitResult(attackResult, attackRoll, attackBonus, hitLog);

    }

}
