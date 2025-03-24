package com.dicerealm.core.combat.systems;

/**
 * Stores the attack result and log message from HitCalculator.
 */
public class HitResult {
    private AttackResult attackResult;
    private int attackRoll;
    private int attackBonus;
    private String hitLog;

    public HitResult(AttackResult attackResult, int attackRoll, int attackBonus, String hitLog) {
        this.attackResult = attackResult;
        this.attackRoll = attackRoll;
        this.attackBonus = attackBonus;
        this.hitLog = hitLog;
    }


    public AttackResult getAttackResult() {
        return attackResult;
    }
    public int getAttackRoll(){ return attackRoll;}
    public int getAttackBonus(){ return attackBonus;}
    public String getHitLog() {
        return hitLog;
    }

}
