package com.dicerealm.core.item.weapons;

import java.util.Map;

import com.dicerealm.core.combat.ActionType;
import com.dicerealm.core.entity.Stat;
import com.dicerealm.core.entity.StatsMap;
import com.dicerealm.core.item.Weapon;
import com.dicerealm.core.item.WeaponClass;

/**
 * Factory for creating staffs with scaling damage and unique names.
 */
public class StaffFactory extends WeaponFactory {

    /**
     * Creates a staff scaled to the given level.
     *
     * @param level The level to scale the staff's damage.
     * @return A staff with scaled damage and unique attributes.
     */
    public Weapon createWeapon(int level) {
        String name = "Staff of Arcane Power";
        String description = "A staff imbued with magical energy.";
        int damageDice = calculateDamageDice(level);
        int diceSides = calculateDiceSides(level);
        int intelligenceBonus = calculateStatBonus(level);

        return new Weapon(
            name,
            description,
            ActionType.MAGIC,
            WeaponClass.STAFF,
            new StatsMap(Map.of(Stat.INTELLIGENCE, intelligenceBonus)),
            damageDice,
            diceSides
        );
    }
}