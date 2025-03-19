package com.dicerealm.core.combat;

import com.dicerealm.core.entity.Entity;
import com.dicerealm.core.entity.Stat;

import java.util.UUID;

public class CombatResult {
    private Entity attacker;
    private Entity target;
    private UUID attackerID;
    private UUID targetID;
    private AttackResult attackResult;
    private int attackRoll;
    private int attackBonus;
    private int targetAC;
    private int damageRoll;
    private String hitLog;
    private String damageLog;

    // Default Constructor
    public CombatResult(){};
    public CombatResult(Entity attacker, Entity target){

        targetAC = target.getStat(Stat.ARMOUR_CLASS);
    }

    public void fromHitResult(HitResult hitResult) {
        this.attackResult = hitResult.getAttackResult();
        this.attackRoll = hitResult.getAttackRoll();
        this.attackBonus = hitResult.getAttackBonus();
        this.hitLog = hitResult.getHitLog();
    }

    public void fromDamageResult(DamageResult damageResult){
        this.damageRoll = damageResult.getDamageRoll();
        this.damageLog =  damageResult.getDamageLog();
    }

    public void setAttackRoll(int attackRoll){this.attackRoll = attackRoll;}
    public void setAttackBonus(int attackBonus){this.attackBonus = attackBonus;}

}
