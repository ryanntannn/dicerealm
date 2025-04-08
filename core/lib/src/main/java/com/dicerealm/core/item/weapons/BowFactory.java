package com.dicerealm.core.item.weapons;

import com.dicerealm.core.combat.ActionType;
import com.dicerealm.core.entity.Stat;
import com.dicerealm.core.entity.StatsMap;
import com.dicerealm.core.item.Weapon;
import com.dicerealm.core.item.WeaponClass;

import java.util.Map;

/**
 * Factory for creating bows with scaling damage and unique names.
 */
public class BowFactory extends WeaponFactory {

    /**
     * Creates a bow scaled to the given level.
     *
     * @param level The level to scale the bow's damage.
     * @return A bow with scaled damage and unique attributes.
     */
    public Weapon createWeapon(int level) {
        String name = "Longbow of the Eagle";
        String description = "A bow that never misses its mark.";
        int damageDice = calculateDamageDice(level);
        int diceSides = calculateDiceSides(level);
        int dexterityBonus = calculateStatBonus(level);

        return new Weapon(
            name,
            description,
            ActionType.RANGED,
            WeaponClass.BOW,
            new StatsMap(Map.of(Stat.DEXTERITY, dexterityBonus)),
            damageDice,
            diceSides
        );
    }
}