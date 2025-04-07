package com.dicerealm.core.combat;

import java.util.UUID;

import com.dicerealm.core.combat.systems.AttackResult;
import com.dicerealm.core.combat.systems.DamageResult;
import com.dicerealm.core.combat.systems.HitResult;
import com.dicerealm.core.dice.Dice;
import com.dicerealm.core.entity.Entity;
import com.dicerealm.core.entity.Stat;
import com.dicerealm.core.item.Potion;
import com.dicerealm.core.item.Scroll;
import com.dicerealm.core.item.Weapon;
import com.dicerealm.core.skills.Skill;

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
    private String potionLog;

    // Default Constructor
    public CombatResult(){}
    public CombatResult(Entity attacker, Entity target, Weapon weapon){
        this.attacker = attacker;
        this.target = target;
        attackerID = attacker.getId();
        targetID = target.getId();
        targetAC = target.getStat(Stat.ARMOUR_CLASS);
        this.damageDice = weapon.getDamageDice();
    }

    public CombatResult(Entity attacker, Entity target, Skill skill){
        this.attacker = attacker;
        this.target = target;
        attackerID = attacker.getId();
        targetID = target.getId();
        targetAC = target.getStat(Stat.ARMOUR_CLASS);
        this.damageDice = skill.getDamageDice();
    }

    public CombatResult(Entity attacker, Entity target, Scroll scroll){
        this.attacker = attacker;
        this.target = target;
        attackerID = attacker.getId();
        targetID = target.getId();
        targetAC = target.getStat(Stat.ARMOUR_CLASS);
        this.damageDice = scroll.getDamageDice();
    }

    public CombatResult(Entity attacker, Entity target, Potion potion){
        this.attacker = attacker;
        this.target = target;
        attackerID = attacker.getId();
        targetID = target.getId();
        targetAC = target.getStat(Stat.ARMOUR_CLASS);
        this.damageDice = potion.getDamageDice();
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
    public void setPotionLog(String potionLog){ this.potionLog = potionLog; }
    public Entity getAttacker(){ return attacker; }
    public Entity getTarget(){ return target; }
    public UUID getAttackerID(){ return attackerID; }
    public UUID getTargetID(){ return targetID; }
    public Dice getDamageDice(){ return damageDice; }
    public String getHitLog(){ return hitLog; }
    public String getDamageLog(){ return damageLog; }
		public int getDamageRoll(){ return damageRoll; }
		public int getAttackRoll(){ return attackRoll; }
		public int getAttackBonus(){ return attackBonus; }
		public int getTargetAC(){ return targetAC; }
		public AttackResult getAttackResult(){ return attackResult; }
		public String getPotionLog(){ return potionLog; }
		public Weapon getWeapon(){ return weapon; }
		public Skill getSkill(){ return skill; }
}	
