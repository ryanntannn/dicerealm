package com.dicerealm.core.item;

import com.dicerealm.core.combat.ActionType;
import com.dicerealm.core.dice.Dice;
import com.dicerealm.core.entity.BodyPart;
import com.dicerealm.core.entity.StatsMap;

public class Weapon extends EquippableItem{

    private ActionType actionType;
    private WeaponClass weaponClass;
    private Dice damageDice;

    public Weapon(String name, String description, ActionType actionType, WeaponClass weaponClass, StatsMap stats, int diceSides) {
        super(name, description, new BodyPart[]{BodyPart.LEFT_HAND,BodyPart.RIGHT_HAND}, stats);
        this.actionType = actionType;
        this.weaponClass = weaponClass;
        this.damageDice = new Dice(diceSides);
    }

    public ActionType getActionType() { return actionType; }
    public WeaponClass getWeaponClass() { return weaponClass; }

    public Dice getDamageDice(){ return damageDice; }

    public int rollDamage(){ return damageDice.roll(); }

    @Override
    public String toString() { return getDisplayName() + " (Damage: " + damageDice + ")"; }

}
