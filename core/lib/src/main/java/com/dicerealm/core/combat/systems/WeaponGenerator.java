package com.dicerealm.core.combat.systems;

import com.dicerealm.core.entity.EntityClass;
import com.dicerealm.core.item.Weapon;
import com.dicerealm.core.item.weapons.AxeFactory;
import com.dicerealm.core.item.weapons.BowFactory;
import com.dicerealm.core.item.weapons.SpearFactory;
import com.dicerealm.core.item.weapons.StaffFactory;
import com.dicerealm.core.item.weapons.SwordFactory;

/**
 * Generates weapons based on the entity class and room level.
 */
public class WeaponGenerator {

    /**
     * Generates a weapon for the given entity class, scaled to the room level.
     *
     * @param entityClass The class of the entity for which the weapon is being generated.
     * @param roomLevel   The level of the room, used to scale the weapon's stats.
     * @return A weapon appropriate for the entity class and scaled to the room level.
     */
    public static Weapon generateWeapon(EntityClass entityClass, int roomLevel) {
        return switch (entityClass) {
            case WARRIOR -> AxeFactory.createAxe(roomLevel);
            case WIZARD -> StaffFactory.createStaff(roomLevel);
            case CLERIC -> StaffFactory.createStaff(roomLevel);
            case ROGUE -> SwordFactory.createSword(roomLevel);
            case RANGER -> BowFactory.createBow(roomLevel);
            default -> SpearFactory.createSpear(roomLevel);
        };
    }
}
