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
                Stat.MAX_HEALTH, 28,
                Stat.ARMOUR_CLASS, 13,
                Stat.STRENGTH, 16,
                Stat.DEXTERITY, 12,
                Stat.CONSTITUTION, 15,
                Stat.INTELLIGENCE, 8,
                Stat.WISDOM, 11,
                Stat.CHARISMA, 10
        )));

        // Wizard starting stats
        classStatsMap.put(EntityClass.WIZARD, new StatsMap(Map.of(
                Stat.MAX_HEALTH, 21,
                Stat.ARMOUR_CLASS, 10,
                Stat.STRENGTH, 8,
                Stat.DEXTERITY, 11,
                Stat.CONSTITUTION, 10,
                Stat.INTELLIGENCE, 16,
                Stat.WISDOM, 15,
                Stat.CHARISMA, 12
        )));

        // CLERIC starting stats
        classStatsMap.put(EntityClass.CLERIC, new StatsMap(Map.of(
                Stat.MAX_HEALTH, 22,
                Stat.ARMOUR_CLASS, 10,
                Stat.STRENGTH, 8,
                Stat.DEXTERITY, 10,
                Stat.CONSTITUTION, 12,
                Stat.INTELLIGENCE, 15,
                Stat.WISDOM, 16,
                Stat.CHARISMA, 11
        )));

        // Rogue starting stats
        classStatsMap.put(EntityClass.ROGUE, new StatsMap(Map.of(
                Stat.MAX_HEALTH, 20,
                Stat.ARMOUR_CLASS, 10,
                Stat.STRENGTH, 12,
                Stat.DEXTERITY, 16,
                Stat.CONSTITUTION, 8,
                Stat.INTELLIGENCE, 11,
                Stat.WISDOM, 10,
                Stat.CHARISMA, 15
        )));

        // Ranger starting stats
        classStatsMap.put(EntityClass.RANGER, new StatsMap(Map.of(
                Stat.MAX_HEALTH, 22,
                Stat.ARMOUR_CLASS, 10,
                Stat.STRENGTH, 15,
                Stat.DEXTERITY, 16,
                Stat.CONSTITUTION, 12,
                Stat.INTELLIGENCE, 8,
                Stat.WISDOM, 12,
                Stat.CHARISMA, 10
        )));


    }

    // Return the starting stats for the given EntityClass
    public static StatsMap getStatsForClass(EntityClass entityClass) {
        return classStatsMap.getOrDefault(entityClass, new StatsMap()); // Return empty if not found
    }
}
