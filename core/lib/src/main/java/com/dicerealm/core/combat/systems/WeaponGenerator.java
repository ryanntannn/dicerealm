package com.dicerealm.core.combat.systems;

import java.util.HashMap;
import java.util.Map;

import com.dicerealm.core.entity.EntityClass;
import com.dicerealm.core.item.Weapon;
import com.dicerealm.core.item.weapons.AxeFactory;
import com.dicerealm.core.item.weapons.BowFactory;
import com.dicerealm.core.item.weapons.SpearFactory;
import com.dicerealm.core.item.weapons.StaffFactory;
import com.dicerealm.core.item.weapons.SwordFactory;
import com.dicerealm.core.item.weapons.WeaponFactory;

public class WeaponGenerator {
    private static final Map<EntityClass, WeaponFactory> CLASS_WEAPON_MAP = new HashMap<>();

    static {
        CLASS_WEAPON_MAP.put(EntityClass.WARRIOR, new AxeFactory());
        CLASS_WEAPON_MAP.put(EntityClass.WIZARD, new StaffFactory());
        CLASS_WEAPON_MAP.put(EntityClass.CLERIC, new StaffFactory());
        CLASS_WEAPON_MAP.put(EntityClass.ROGUE, new SwordFactory());
        CLASS_WEAPON_MAP.put(EntityClass.RANGER, new BowFactory());
    }
    public static Weapon generateWeapon(EntityClass entityClass, int roomLevel) {
        // Retrieve the appropriate weapon factory based on the entity class
        WeaponFactory weaponFactory = CLASS_WEAPON_MAP.getOrDefault(entityClass, new SpearFactory()); // Default to SpearFactory if no specific factory is found
        // Create the weapon using the factory
        if (weaponFactory != null) {
            Weapon weapon = weaponFactory.createWeapon(roomLevel);
            return weapon;
        } else {
            throw new IllegalArgumentException("No weapon factory found for class: " + entityClass);
        }
    }
}
