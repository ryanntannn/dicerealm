package com.dicerealm.core.item.weapons;

import java.util.Map;

import com.dicerealm.core.combat.ActionType;
import com.dicerealm.core.entity.Stat;
import com.dicerealm.core.entity.StatsMap;
import com.dicerealm.core.item.Weapon;
import com.dicerealm.core.item.WeaponClass;

public class DummyWeapon extends Weapon {
    public DummyWeapon() {
        super("Dummy Weapon", "Dummy Weapon", ActionType.MELEE, WeaponClass.AXE, new StatsMap(Map.of(Stat.STRENGTH, 0)), 1);
    }

}
