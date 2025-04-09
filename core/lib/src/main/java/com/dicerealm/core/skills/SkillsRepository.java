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
            new Skill("Cleave", "Attack multiple enemies at once", EntityClass.WARRIOR, ActionType.SKILL, 0, 1, 8, 3),
            new Skill("Taunt", "Force enemies to attack you", EntityClass.WARRIOR, ActionType.SKILL, 0, 1, 0, 2)
        ));
        warriorSkills.put(3, List.of(
            new Skill("Whirlwind", "Spin attack hitting all nearby enemies", EntityClass.WARRIOR, ActionType.SKILL, 0, 2, 6, 4),
            new Skill("Intimidating Shout", "Frighten enemies, reducing their attack", EntityClass.WARRIOR, ActionType.SKILL, 0, 1, 0, 3)
        ));
        skillsByClassAndLevel.put(EntityClass.WARRIOR, warriorSkills);

        // Initialize Wizard skills
        Map<Integer, List<Skill>> wizardSkills = new HashMap<>();
        wizardSkills.put(1, List.of(
            new Skill("Magic Missile", "A reliable magical projectile", EntityClass.WIZARD, ActionType.MAGIC, 1, 1, 4, 1),
            new Skill("Frost Ray", "A ray of frost that slows enemies", EntityClass.WIZARD, ActionType.MAGIC, 1, 1, 3, 1)
        ));
        wizardSkills.put(2, List.of(
            new Skill("Fireball", "A massive ball of fire", EntityClass.WIZARD, ActionType.MAGIC, 3, 2, 6, 3),
            new Skill("Arcane Shield", "Protect yourself with arcane energy", EntityClass.WIZARD, ActionType.MAGIC, 2, 0, 0, 2)
        ));
        wizardSkills.put(3, List.of(
            new Skill("Lightning Bolt", "A powerful bolt of lightning", EntityClass.WIZARD, ActionType.MAGIC, 3, 3, 6, 4),
            new Skill("Teleport", "Instantly move to a new location", EntityClass.WIZARD, ActionType.MAGIC, 3, 0, 0, 3)
        ));
        skillsByClassAndLevel.put(EntityClass.WIZARD, wizardSkills);

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