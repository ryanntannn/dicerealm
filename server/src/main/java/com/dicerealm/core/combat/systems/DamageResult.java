package com.dicerealm.core.combat.systems;

public class DamageResult {
    private int damageRoll;
    private String damageLog;

    public DamageResult(int damageRoll, String damageLog) {
        this.damageRoll = damageRoll;
        this.damageLog = damageLog;
    }

    public int getDamageRoll(){ return damageRoll;}
    public String getDamageLog() {
        return damageLog;
    }

}
