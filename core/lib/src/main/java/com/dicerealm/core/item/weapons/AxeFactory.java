package com.dicerealm.core.item.weapons;

import java.util.Map;

import com.dicerealm.core.combat.ActionType;
import com.dicerealm.core.entity.Stat;
import com.dicerealm.core.entity.StatsMap;
import com.dicerealm.core.item.Weapon;
import com.dicerealm.core.item.WeaponClass;

/**
 * Factory for creating axes with scaling damage and unique names.
 */
public class AxeFactory {

    /**
     * Creates an axe scaled to the given level.
     *
     * @param level The level to scale the axe's damage.
     * @return An axe with scaled damage and unique attributes.
     */
    public static Weapon createAxe(int level) {
        String name = "Axe of the Berserker";
        String description = "A heavy axe that strikes fear into enemies.";
        int damageDice = calculateDamageDice(level);
        int diceSides = calculateDiceSides(level);
        int strengthBonus = calculateStatBonus(level);

        return new Weapon(
            name,
            description,
            ActionType.MELEE,
            WeaponClass.AXE,
            new StatsMap(Map.of(Stat.STRENGTH, strengthBonus)),
            damageDice,
            diceSides
        );
    }

    private static int calculateDamageDice(int level) {
        return 1 + (level / 5); // Add 1 die every 5 levels
    }

    private static int calculateDiceSides(int level) {
        return 5 + (level / 3); // Add 1 side every 3 levels
    }

    private static int calculateStatBonus(int level) {
        return 1 + (level / 4); // Add 1 stat bonus every 4 levels
    }
}