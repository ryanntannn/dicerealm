package com.dicerealm.core.combat;

import com.dicerealm.core.combat.systems.AttackResult;
import com.dicerealm.core.combat.systems.DamageResult;
import com.dicerealm.core.combat.systems.HitResult;
import com.dicerealm.core.dice.Dice;
import com.dicerealm.core.entity.Entity;
import com.dicerealm.core.entity.Stat;
import com.dicerealm.core.item.Weapon;
import com.dicerealm.core.skills.Skill;

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
    private Dice damageDice;
    private Weapon weapon;
    private Skill skill;

    // Default Constructor
    public CombatResult(){}
    public CombatResult(Entity attacker, Entity target, Weapon weapon){
        attackerID = attacker.getId();
        targetID = target.getId();
        targetAC = target.getStat(Stat.ARMOUR_CLASS);
        this.damageDice = weapon.getDamageDice();
    }

    public CombatResult(Entity attacker, Entity target, Skill skill){
        attackerID = attacker.getId();
        targetID = target.getId();
        targetAC = target.getStat(Stat.ARMOUR_CLASS);
        this.damageDice = skill.getDamageDice();
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

    public void setAttackRoll(int attackRoll){ this.attackRoll = attackRoll; }
    public void setAttackBonus(int attackBonus){ this.attackBonus = attackBonus; }
    public Dice getDamageDice(){ return damageDice; }
    public String getHitLog(){ return hitLog; }
    public String getDamageLog(){ return damageLog; }

}
