package com.dicerealm.core.item;

import com.dicerealm.core.entity.BodyPart;
import com.dicerealm.core.entity.Stat;
import com.dicerealm.core.entity.StatsMap;

import java.util.Map;

public class IronSword extends Weapon{
    public IronSword(int strength) {
        super("Iron Sword", "Iron Sword forged from the Great Dwarfen Forges", WeaponType.SWORD, new BodyPart[] { BodyPart.LEFT_HAND }, new StatsMap(Map.of(Stat.STRENGTH, strength)), 8);
    }
}
