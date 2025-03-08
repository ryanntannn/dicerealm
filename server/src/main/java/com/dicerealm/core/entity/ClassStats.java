package com.dicerealm.core.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Default staring classStats for all entities in the game, based on entityClass
 *
 * @see EntityClass - represents all possible Entity Classes
 */
public class ClassStats {
    private static final Map<EntityClass, StatsMap> classStatsMap = new HashMap<>();

    static {
        // Warrior starting stats
        classStatsMap.put(EntityClass.WARRIOR, new StatsMap(Map.of(
                Stat.MAX_HEALTH, 24,
                Stat.ARMOUR_CLASS, 15,
                Stat.STRENGTH, 16,
                Stat.DEXTERITY, 12,
                Stat.CONSTITUTION, 16,
                Stat.INTELLIGENCE, 8,
                Stat.WISDOM, 10,
                Stat.CHARISMA, 10
        )));

        // Wizard starting stats
        classStatsMap.put(EntityClass.WIZARD, new StatsMap(Map.of(
                Stat.MAX_HEALTH, 18,
                Stat.ARMOUR_CLASS, 12,
                Stat.STRENGTH, 8,
                Stat.DEXTERITY, 12,
                Stat.CONSTITUTION, 10,
                Stat.INTELLIGENCE, 18,
                Stat.WISDOM, 12,
                Stat.CHARISMA, 10
        )));

        // CLERIC starting stats
        classStatsMap.put(EntityClass.CLERIC, new StatsMap(Map.of(
                Stat.MAX_HEALTH, 18,
                Stat.ARMOUR_CLASS, 12,
                Stat.STRENGTH, 8,
                Stat.DEXTERITY, 12,
                Stat.CONSTITUTION, 12,
                Stat.INTELLIGENCE, 13,
                Stat.WISDOM, 16,
                Stat.CHARISMA, 14
        )));

        // Rogue starting stats
        classStatsMap.put(EntityClass.ROGUE, new StatsMap(Map.of(
                Stat.MAX_HEALTH, 18,
                Stat.ARMOUR_CLASS, 14,
                Stat.STRENGTH, 12,
                Stat.DEXTERITY, 15,
                Stat.CONSTITUTION, 14,
                Stat.INTELLIGENCE, 10,
                Stat.WISDOM, 10,
                Stat.CHARISMA, 16
        )));

        // Ranger starting stats
        classStatsMap.put(EntityClass.RANGER, new StatsMap(Map.of(
                Stat.MAX_HEALTH, 20,
                Stat.ARMOUR_CLASS, 14,
                Stat.STRENGTH, 12,
                Stat.DEXTERITY, 18,
                Stat.CONSTITUTION, 14,
                Stat.INTELLIGENCE, 10,
                Stat.WISDOM, 10,
                Stat.CHARISMA, 12
        )));


    }

    // Return the starting stats for the given EntityClass
    public static StatsMap getStatsForClass(EntityClass entityClass) {
        return classStatsMap.getOrDefault(entityClass, new StatsMap()); // Return empty if not found
    }
}
