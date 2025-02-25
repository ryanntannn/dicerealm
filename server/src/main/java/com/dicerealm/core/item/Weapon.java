package com.dicerealm.core.item;

import com.dicerealm.core.dice.Dice;
import com.dicerealm.core.entity.BodyPart;
import com.dicerealm.core.entity.StatsMap;

public class Weapon extends EquippableItem{

    private WeaponType weaponType;
    private Dice damageDice;

    public Weapon(String name, String description, WeaponType weaponType, StatsMap stats, int diceSides) {
        super(name, description, new BodyPart[]{BodyPart.LEFT_HAND,BodyPart.RIGHT_HAND}, stats);
        this.weaponType = weaponType;
        this.damageDice = new Dice(diceSides);
    }

    public WeaponType getWeaponType() { return weaponType; }

    public Dice getDamageDice(){ return damageDice; }

    public int rollDamage(){ return damageDice.roll(); }

    @Override
    public String toString() { return getDisplayName() + " (Damage: " + damageDice + ")"; }

}
