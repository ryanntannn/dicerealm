package com.dicerealm.core.skills;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.dicerealm.core.combat.ActionType;
import com.dicerealm.core.entity.EntityClass;

/**
 * Repository of skills organized by class and level requirements.
 */
public class SkillsRepository {
    private static final Map<EntityClass, Map<Integer, List<Skill>>> skillsByClassAndLevel = new HashMap<>();

    static {
        
        // TODO: Initialize skills for each class and level
        // Initialize Warrior skills
        Map<Integer, List<Skill>> warriorSkills = new HashMap<>();
        warriorSkills.put(1, List.of(
            new Skill("Slash", "A powerful slash attack", EntityClass.WARRIOR, ActionType.SKILL, 0, 1, 6, 1),
            new Skill("Shield Bash", "Bash enemy with your shield, stunning them", EntityClass.WARRIOR, ActionType.MELEE, 0, 1, 4, 2)
        ));
        warriorSkills.put(2, List.of(
            new Skill("Cleave", "Attack multiple enemies at once", EntityClass.WARRIOR, ActionType.SKILL, 0, 2, 5, 3)
        ));
        warriorSkills.put(3, List.of(
            new Skill("Whirlwind", "Spin attack hitting all nearby enemies", EntityClass.WARRIOR, ActionType.SKILL, 0, 3, 5, 4)
        ));
        skillsByClassAndLevel.put(EntityClass.WARRIOR, warriorSkills);

        // Initialize Wizard skills
        Map<Integer, List<Skill>> wizardSkills = new HashMap<>();
        wizardSkills.put(1, List.of(
            new Skill("Magic Missile", "A reliable magical projectile", EntityClass.WIZARD, ActionType.MAGIC, 1, 1, 4, 1),
            new Skill("Frost Ray", "A ray of frost that slows enemies", EntityClass.WIZARD, ActionType.MAGIC, 1, 1, 3, 1)
        ));
        wizardSkills.put(2, List.of(
            new Skill("Fireball", "A massive ball of fire", EntityClass.WIZARD, ActionType.MAGIC, 3, 2, 6, 3)
        ));
        wizardSkills.put(3, List.of(
            new Skill("Lightning Bolt", "A powerful bolt of lightning", EntityClass.WIZARD, ActionType.MAGIC, 3, 3, 6, 4)
        ));
        skillsByClassAndLevel.put(EntityClass.WIZARD, wizardSkills);

        Map<Integer, List<Skill>> rogueSkills = new HashMap<>();
        rogueSkills.put(1, List.of(
            new Skill("Backstab", "A deadly attack from behind", EntityClass.ROGUE, ActionType.SKILL, 0, 10, 3)
        ));
        rogueSkills.put(2, List.of(
            new Skill("Rapid Strike", "Strike Rapidly", EntityClass.ROGUE, ActionType.SKILL, 0, 4, 3, 4)
        ));
        skillsByClassAndLevel.put(EntityClass.ROGUE, rogueSkills);

        Map<Integer, List<Skill>> rangerSkills = new HashMap<>();
        rangerSkills.put(1, List.of(
            new Skill("Arrow Shot", "A precise arrow attack", EntityClass.RANGER, ActionType.SKILL, 0, 2,4, 6),
            new Skill("Hunter's Mark", "Mark an enemy to deal extra damage", EntityClass.RANGER, ActionType.SKILL, 0, 8, 0)
        ));
        rangerSkills.put(2, List.of(
            new Skill("Multi-Shot", "Shoot multiple arrows at once", EntityClass.RANGER, ActionType.SKILL, 0, 4, 4, 8)
        ));
        skillsByClassAndLevel.put(EntityClass.RANGER, rangerSkills);

        Map<Integer, List<Skill>> clericSkills = new HashMap<>();
        clericSkills.put(1, List.of(
            new Skill("Smite", "A divine attack against enemies", EntityClass.CLERIC, ActionType.MAGIC, 2, 1, 8, 6),
            new Skill("Holy Light", "A beam of Light", EntityClass.CLERIC, ActionType.MAGIC, 2, 1, 5, 8)
        ));
        clericSkills.put(2, List.of(
            new Skill("Holy Blast", "Blasting Spell", EntityClass.CLERIC, ActionType.MAGIC, 0, 1, 8)
        ));
        skillsByClassAndLevel.put(EntityClass.CLERIC, clericSkills);
        
    }

    public static List<Skill> getAvailableSkills(EntityClass entityClass, int maxLevel) {
        Map<Integer, List<Skill>> skillsByLevel = skillsByClassAndLevel.get(entityClass);
        if (skillsByLevel == null) {
            return new ArrayList<>();
        }

        List<Skill> availableSkills = new ArrayList<>();
        for (int i = 1; i <= maxLevel; i++) {
            if (skillsByLevel.containsKey(i)) {
                availableSkills.addAll(skillsByLevel.get(i));
            }
        }
        return availableSkills;
    }
    
    public static List<Skill> getNewSkillsForLevel(EntityClass entityClass, int level) {
        Map<Integer, List<Skill>> skillsByLevel = skillsByClassAndLevel.get(entityClass);
        if (skillsByLevel == null || !skillsByLevel.containsKey(level)) {
            return new ArrayList<>();
        }
        
        return skillsByLevel.get(level);
    }
    
    public static List<Skill> filterNewSkills(List<Skill> availableSkills, List<Skill> knownSkills) {
        List<String> knownSkillNames = knownSkills.stream()
            .map(Skill::getDisplayName)
            .collect(Collectors.toList());
            
        return availableSkills.stream()
            .filter(skill -> !knownSkillNames.contains(skill.getDisplayName()))
            .collect(Collectors.toList());
    }
}