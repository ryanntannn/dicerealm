package com.dicerealm.core.combat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.dicerealm.core.combat.managers.MonsterGenerator;
import com.dicerealm.core.entity.EntityClass;
import com.dicerealm.core.entity.Race;
import com.dicerealm.core.entity.Stat;
import com.dicerealm.core.entity.StatsMap;
import com.dicerealm.core.item.Weapon;
import com.dicerealm.core.monster.Monster;

public class MonsterGeneratorTest {

    @Test
    public void testGenerateMonster() {
        // Arrange
        String monsterName = "Goblin Warrior";
        EntityClass entityClass = EntityClass.WARRIOR;
        Race race = Race.DEMON;
        int roomLevel = 5;

        // Act
        Monster monster = MonsterGenerator.generateMonster(monsterName, entityClass, race, roomLevel);

        // Assert
        assertNotNull(monster, "Monster should not be null");
        assertEquals(monsterName, monster.getDisplayName(), "Monster name should match");
        assertEquals(entityClass, monster.getEntityClass(), "Monster class should match");
        assertEquals(race, monster.getRace(), "Monster race should match");

        // Verify stats scaling
        StatsMap stats = monster.getStats();
        assertTrue(stats.get(Stat.MAX_HEALTH) > 0, "Monster should have positive health");
        assertTrue(stats.get(Stat.STRENGTH) > 0, "Monster should have positive strength");

        // Verify skills assignment
        assertTrue(!monster.getSkillsInventory().getItems().isEmpty(), "Monster should have skills assigned");

        // Verify weapon assignment
        Weapon equippedWeapon = (Weapon) monster.getEquippedItems().getOrDefault("RIGHT_HAND", null);
        assertNotNull(equippedWeapon, "Monster should have a weapon equipped");
        assertEquals("Sword", equippedWeapon.getWeaponClass().toString(), "Weapon class should match monster class");

        // Verify skills assignment
        assertTrue(!monster.getSkillsInventory().getItems().isEmpty(), "Monster should have skills assigned");
    }
}