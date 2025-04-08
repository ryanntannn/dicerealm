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
public class SpearFactory extends WeaponFactory {

    /**
     * Creates a spear scaled to the given level.
     *
     * @param level The level to scale the spear's damage.
     * @return A spear with scaled damage and unique attributes.
     */
    public Weapon createWeapon(int level) {
        String name = "Spear of Precision";
        String description = "A finely crafted spear for piercing attacks.";
        int damageDice = calculateDamageDice(level);
        int diceSides = calculateDiceSides(level);
        int dexterityBonus = calculateStatBonus(level);

        return new Weapon(
            name,
            description,
            ActionType.MELEE,
            WeaponClass.SPEAR,
            new StatsMap(Map.of(Stat.DEXTERITY, dexterityBonus)),
            damageDice,
            diceSides
        );
    }
}