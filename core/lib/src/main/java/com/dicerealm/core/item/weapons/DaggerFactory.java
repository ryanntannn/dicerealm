package com.dicerealm.core.item.weapons;

import java.util.Map;

import com.dicerealm.core.combat.ActionType;
import com.dicerealm.core.entity.Stat;
import com.dicerealm.core.entity.StatsMap;
import com.dicerealm.core.item.Weapon;
import com.dicerealm.core.item.WeaponClass;

/**
 * Factory for creating spears with scaling damage and unique names.
 */
public class DaggerFactory {

    /**
     * Creates a spear scaled to the given level.
     *
     * @param level The level to scale the spear's damage.
     * @return A spear with scaled damage and unique attributes.
     */
    public static Weapon createDagger(int level) {
        String name = "Dagger of Precision";
        String description = "A finely crafted Dagger for Stealth attacks.";
        int damageDice = calculateDamageDice(level);
        int diceSides = calculateDiceSides(level);
        int dexterityBonus = calculateStatBonus(level);

        return new Weapon(
            name,
            description,
            ActionType.MELEE,
            WeaponClass.DAGGER,
            new StatsMap(Map.of(Stat.DEXTERITY, dexterityBonus)),
            damageDice,
            diceSides
        );
    }

    private static int calculateDamageDice(int level) {
        return 2 + (level / 5); // Add 1 die every 5 levels
    }

    private static int calculateDiceSides(int level) {
        return 2 + (level / 3); // Add 1 side every 3 levels
    }

    private static int calculateStatBonus(int level) {
        return 1 + (level / 4); // Add 1 stat bonus every 4 levels
    }
}