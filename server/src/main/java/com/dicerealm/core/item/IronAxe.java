package com.dicerealm.core.item;

import com.dicerealm.core.entity.Stat;
import com.dicerealm.core.entity.StatsMap;

import java.util.Map;

public class IronAxe extends Weapon{
    public IronAxe(int strength) {
        super("Iron Axe", "Iron Axe forged from the Great Dwarfen Forges", WeaponType.AXE, new StatsMap(Map.of(Stat.STRENGTH, strength)), 10);
    }
}
