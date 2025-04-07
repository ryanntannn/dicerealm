package com.dicerealm.core.combat.managers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dicerealm.core.entity.ClassStats;
import com.dicerealm.core.entity.EntityClass;
import com.dicerealm.core.entity.Race;
import com.dicerealm.core.entity.Stat;
import com.dicerealm.core.entity.StatsMap;
import com.dicerealm.core.monster.Monster;
import com.dicerealm.core.skills.Skill;
import com.dicerealm.core.skills.SkillsRepository;

/**
 * Generates monsters with stats scaled according to room level.
 * Provides methods to create appropriate monsters for different
 * encounter difficulties.
 */
public class MonsterGenerator {

    private static final Map<EntityClass, Float> CLASS_HEALTH_MULTIPLIERS = new HashMap<>();
    private static final Map<EntityClass, Float> CLASS_DAMAGE_MULTIPLIERS = new HashMap<>();
    
    // Base stat scaling factor per level
    private static final float LEVEL_SCALING_FACTOR = 0.2f; // 20% increase per level
    
    static {
        // Class-specific health multipliers
        CLASS_HEALTH_MULTIPLIERS.put(EntityClass.WARRIOR, 1.3f);
        CLASS_HEALTH_MULTIPLIERS.put(EntityClass.WIZARD, 0.8f);
        CLASS_HEALTH_MULTIPLIERS.put(EntityClass.CLERIC, 1.1f);
        CLASS_HEALTH_MULTIPLIERS.put(EntityClass.ROGUE, 0.9f);
        CLASS_HEALTH_MULTIPLIERS.put(EntityClass.RANGER, 1.0f);
        
        // Class-specific damage multipliers
        CLASS_DAMAGE_MULTIPLIERS.put(EntityClass.WARRIOR, 1.2f);
        CLASS_DAMAGE_MULTIPLIERS.put(EntityClass.WIZARD, 1.3f);
        CLASS_DAMAGE_MULTIPLIERS.put(EntityClass.CLERIC, 1.0f);
        CLASS_DAMAGE_MULTIPLIERS.put(EntityClass.ROGUE, 1.4f);
        CLASS_DAMAGE_MULTIPLIERS.put(EntityClass.RANGER, 1.2f);
    }
    
    public static Monster generateMonster(String displayName, EntityClass entityClass, Race race, int roomLevel) {
        // Retrieve base stats from ClassStats
        StatsMap baseStats = ClassStats.getStatsForClass(entityClass);
        
        // Scale stats based on room level
        StatsMap scaledStats = scaleStats(baseStats, entityClass, roomLevel);
        
        // Create monster with scaled stats
        Monster monster = new Monster(displayName, race, entityClass, scaledStats, roomLevel);
        
        // Equip appropriate weapons and skills
        equipSkills(monster, entityClass, roomLevel);
        
        return monster;
    }
    
    /**
     * Scales the base stats of a monster based on its class and the room level.
     */
    private static StatsMap scaleStats(StatsMap baseStats, EntityClass entityClass, int roomLevel) {
        float levelFactor = 1.0f + (LEVEL_SCALING_FACTOR * (roomLevel - 1));
        float healthMultiplier = CLASS_HEALTH_MULTIPLIERS.getOrDefault(entityClass, 1.0f);
        
        // Scale each stat
        StatsMap scaledStats = new StatsMap();
        for (Stat stat : baseStats.keySet()) {
            int baseValue = baseStats.get(stat);
            int scaledValue = baseValue;
            
            if (stat == Stat.MAX_HEALTH) {
                scaledValue = Math.round(baseValue * healthMultiplier * levelFactor);
            } else {
                scaledValue = Math.round(baseValue * levelFactor);
            }
            
            scaledStats.put(stat, scaledValue);
        }
        
        return scaledStats;
    }
    
    /**
     * Equips a monster with appropriate weapons and skills based on class and level.
     */
    private static void equipSkills(Monster monster, EntityClass entityClass, int roomLevel) {
        // Get all available skills for the monster's class up to the current level
        List<Skill> availableSkills = SkillsRepository.getAvailableSkills(entityClass, roomLevel);

        // Filter out skills the monster already knows (if any)
        List<Skill> newSkills = SkillsRepository.filterNewSkills(availableSkills, monster.getSkillsInventory().getItems());

        // Add up to 4 skills to the monster
        int maxSkills = Math.min(newSkills.size(), 4);
        for (int i = 0; i < maxSkills; i++) {
            monster.getSkillsInventory().addItem(newSkills.get(i));
        }
    }

    // Todo: Uncomment and implement the weapon equipping logic
    // private static void equipWeapon(Monster monster, EntityClass entityClass) {
    //     // Use WeaponFactory to create a weapon for the monster's class
    //     Weapon weapon = WeaponFactory.createWeaponForClass(entityClass);

    //     if (weapon != null) {
    //         // Add the weapon to the monster's inventory
    //         monster.getInventory().addItem(weapon);

    //         // Equip the weapon in the monster's right hand
    //         monster.equipItem(BodyPart.RIGHT_HAND, weapon);
    //     }
    // }
    
}