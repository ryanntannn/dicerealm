package com.dicerealm.core.item.weapons;

import java.util.Map;

import com.dicerealm.core.combat.ActionType;
import com.dicerealm.core.entity.Stat;
import com.dicerealm.core.entity.StatsMap;
import com.dicerealm.core.item.Weapon;
import com.dicerealm.core.item.WeaponClass;

/**
 * Factory for creating swords with scaling damage and unique names.
 */
public class SwordFactory extends WeaponFactory {

    /**
     * Creates a sword scaled to the given level.
     *
     * @param level The level to scale the sword's damage.
     * @return A sword with scaled damage and unique attributes.
     */
    public Weapon createWeapon(int level) {
        String name = "Blade of Valor";
        String description = "A legendary sword forged for heroes.";
        int damageDice = calculateDamageDice(level);
        int diceSides = calculateDiceSides(level);
        int strengthBonus = calculateStatBonus(level);

        return new Weapon(
            name,
            description,
            ActionType.MELEE,
            WeaponClass.SWORD,
            new StatsMap(Map.of(Stat.STRENGTH, strengthBonus)),
            damageDice,
            diceSides
        );
    }
}