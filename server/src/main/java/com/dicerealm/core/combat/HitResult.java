package com.dicerealm.core.combat;

/**
 * Stores the attack result and log message from HitCalculator.
 */
public class HitResult {
    private AttackResult attackResult;
    private String logMessage;

    public HitResult(AttackResult attackResult, String logMessage) {
        this.attackResult = attackResult;
        this.logMessage = logMessage;
    }

    public AttackResult getAttackResult() {
        return attackResult;
    }

    public String getLogMessage() {
        return logMessage;
    }
}
