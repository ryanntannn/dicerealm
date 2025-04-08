package com.dicerealm.core.item.weapons;

import com.dicerealm.core.item.Weapon;

public abstract class WeaponFactory {
    public abstract Weapon createWeapon(int level);

    protected static int calculateDamageDice(int level) {
        return 1 + (level / 5); // Add 1 die every 5 levels
    }

    protected static int calculateDiceSides(int level) {
        return 6 + (level / 3); // Add 1 side every 3 levels
    }

    protected static int calculateStatBonus(int level) {
        return 2 + (level / 4); // Add 1 stat bonus every 4 levels
    }
}
