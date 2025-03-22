package com.example.dicerealmandroid.core.entity;

import com.example.dicerealmandroid.core.item.EquippableItem;
import com.example.dicerealmandroid.core.item.InventoryOf;
import com.example.dicerealmandroid.core.item.Item;
import com.example.dicerealmandroid.core.skill.Skill;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class Entity {

    public enum BodyPart {
        HEAD,
        NECK,
        TORSO,
        LEGS,
        LEFT_HAND,
        RIGHT_HAND,
    }

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
    private Map<BodyPart, EquippableItem> equippedItems = new HashMap<BodyPart, EquippableItem>();
    private StatsMap baseStats;
    private StatsMap stats;

    private InventoryOf<Item> inventory = new InventoryOf<>();
    private InventoryOf<Skill> skillsInventory = new InventoryOf<>(4);


    public Entity(String displayName, Race race, EntityClass entityClass, StatsMap baseStats) {
        this.id = UUID.randomUUID();
        this.displayName = displayName;
        this.race = race;
        this.entityClass = entityClass;

        // Get base stats from ClassStats class
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

    public InventoryOf<Item> getInventory() {
        return inventory;
    }

    public void updateInventory(InventoryOf<Item> inventory){
        this.inventory = inventory;
    }

    public InventoryOf<Skill> getSkillsInventory(){
        return skillsInventory;
    }

    public void updateSkillsInventory(InventoryOf<Skill> skillsInventory){
        this.skillsInventory = skillsInventory;
    }

    public boolean equipItem(BodyPart bodyPart, EquippableItem item) {
        // Check if item is in inventory
        if (!inventory.containsItem(item)) {
            return false;
        }

        if (!item.isSuitableFor(bodyPart)) {
            return false;
        }

        inventory.removeItem(item);

        // check if the body part is already equipped
        if (equippedItems.containsKey(bodyPart)) {
            // un-equip the item
            inventory.addItem(equippedItems.get(bodyPart));
        }

        equippedItems.put(bodyPart, item);
        updateStats();

        return true;
    }

    public void updateMaxHealth() {
        this.health = stats.get(Stat.MAX_HEALTH);
    }

    public void updateEntityStats(Stats stats){
        this.stats.clear();
        for(Stat stat: baseStats.keySet()){
            this.stats.put(stat, stats.getStat(stat));
        }
    }

    public void updateStats() {
        stats.clear();
        for (Stat stat : baseStats.keySet()) {
            stats.put(stat, getStat(stat));
        }
    }

    public int getStat(Stat stat) {
        int out = baseStats.get(stat);
        for (EquippableItem item : equippedItems.values()) {
            out += item.getStat(stat);
        }
        return out;
    }

    public void displayStats(){
        System.out.println("Name: " + getDisplayName());
        System.out.println("Race: " + getRace());
        System.out.println("Class: " + getEntityClass());
        System.out.println("Entity Stats:");
        for (Stat stat : getStats().keySet()) {
            System.out.println(stat + ": " + getStats().get(stat));
        }
    }

    public StatsMap getStats() {
        return stats;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
