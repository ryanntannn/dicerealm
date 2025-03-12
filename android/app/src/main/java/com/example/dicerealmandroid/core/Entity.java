package com.example.dicerealmandroid.core;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Entity {
    public enum Race {
        HUMAN,
        ELF,
        DEMON,
        DWARF,
        TIEFLING
    }

    public enum EntityClass {
        WARRIOR,
        WIZARD,
        CLERIC,
        ROGUE,
        RANGER
    }

    public enum Stat {
        MAX_HEALTH,
        ARMOUR_CLASS,
        STRENGTH,
        DEXTERITY,
        CONSTITUTION,
        INTELLIGENCE,
        WISDOM,
        CHARISMA
    }

    public interface Stats {
        public abstract int getStat(Stat stat);
    }


    public static class StatsMap extends HashMap<Stat, Integer> implements Stats {
        @Override
        public int getStat(Stat stat) {
            return super.get(stat);
        }

        public StatsMap() {
            super();
        }

        public StatsMap(Map<Stat, Integer> stats) {
            super.putAll(stats);
        }
    }

    public static class ClassStats {
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



    private UUID id;
    private String displayName;
    private Race race;
    private EntityClass entityClass;
    private int health;
    private StatsMap baseStats;
    private StatsMap stats;

    public Entity(String displayName, Race race, EntityClass entityClass, StatsMap baseStats) {
        this.id = UUID.randomUUID();
        this.displayName = displayName;
        this.race = race;
        this.entityClass = entityClass;

        //Get base stats from ClassStats class
        this.baseStats = ClassStats.getStatsForClass(entityClass);
        this.health = baseStats.get(Stat.MAX_HEALTH);

        // Initialize the stats map by copying baseStats initially
        this.stats = new StatsMap(baseStats);
    }

    public UUID getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Race getRace() {
        return race;
    }

    public EntityClass getEntityClass() {
        return entityClass;
    }

    public int getHealth() {
        return health;
    }

    public boolean isAlive() {
        return health > 0;
    }
}
