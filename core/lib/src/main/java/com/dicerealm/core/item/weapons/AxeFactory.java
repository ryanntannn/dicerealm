package com.dicerealm.core.item.weapons;

import com.dicerealm.core.combat.ActionType;
import com.dicerealm.core.entity.Stat;
import com.dicerealm.core.entity.StatsMap;
import com.dicerealm.core.item.Weapon;
import com.dicerealm.core.item.WeaponClass;

import java.util.Map;

/**
 * Factory for creating axes with scaling damage and unique names.
 */
public class AxeFactory extends WeaponFactory {

    /**
     * Creates an axe scaled to the given level.
     *
     * @param level The level to scale the axe's damage.
     * @return An axe with scaled damage and unique attributes.
     */
    public Weapon createWeapon(int level) {
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
}