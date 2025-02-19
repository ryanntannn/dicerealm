package com.dicerealm.core.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.dicerealm.core.skills.Skill;

public class SkillsInventory {
    private int maxSkills;
    private List<Skill> skills = new ArrayList<Item>();

    public SkillsInventory() {
        this.maxSkills = 4;
    }

    public Inventory(int inventorySize) {
        this.inventorySize = inventorySize;
    }

    public boolean addSkill(Skill skill) {
        if (skills.size() < maxSkills) {
            skills.add(skill);
            return true;
        }
        return false;
    }

    public boolean removeSkill(Skill skill) {
        if (skills.contains(skill)) {
            skills.remove(skill);
            return true;
        }
        return false;
    }

    public boolean hasSkill(Skill skill) {
        return skills.contains(skill);
    }

    public String toString() {
        return skills.toString();
    }

    public Skill getSkill(UUID skillId) {
        for (Skill skill : skills) {
            if (skill.getId().equals(skillId)) {
                return skill;
            }
        }
        return null;
    }
}