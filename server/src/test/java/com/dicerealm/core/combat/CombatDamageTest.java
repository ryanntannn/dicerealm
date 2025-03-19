package com.dicerealm.core.combat;

import com.dicerealm.core.entity.EntityClass;
import com.dicerealm.core.entity.Race;
import com.dicerealm.core.entity.StatsMap;
import com.dicerealm.core.entity.Stat;
import com.dicerealm.core.item.Weapon;
import com.dicerealm.core.item.WeaponClass;
import com.dicerealm.core.skills.Skill;
import com.dicerealm.core.monster.Monster;
import com.dicerealm.core.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


public class CombatDamageTest {

    private Player player;
    private Monster monster;
    private Weapon weapon;
    private Skill skill;
    private CombatLog combatLog;
    private DamageCalculator damageCalculator;

    @BeforeEach
    void setUp() {
        // Initialize stats and entities for the tests
        StatsMap baseStats = new StatsMap(Map.of(
                Stat.MAX_HEALTH, 20,
                Stat.ARMOUR_CLASS, 10,
                Stat.STRENGTH, 5,
                Stat.DEXTERITY, 5,
                Stat.CONSTITUTION, 5,
                Stat.INTELLIGENCE, 5,
                Stat.WISDOM, 5,
                Stat.CHARISMA, 5
        ));

        player = new Player("Darren", Race.HUMAN, EntityClass.WARRIOR, baseStats);
        monster = new Monster("Demon King", Race.DEMON, EntityClass.WARRIOR, baseStats);
        combatLog = new CombatLog();
        damageCalculator = new DamageCalculator();
        // Initialize weapon and skill for testing
        weapon = new Weapon("Sword", "Iron Sword forged from the Great Dwarfen Forges", ActionType.MELEE, WeaponClass.SWORD, new StatsMap(Map.of(Stat.STRENGTH, 1)), 1);
        skill = new Skill("Fireball", "A massive ball of fire", EntityClass.WIZARD, 3,2,1);
    }

    @Test
    void testWeaponNormalDamage() {
        // Use a normal roll for weapon damage
        damageCalculator.applyWeaponDamage(player, monster, weapon, false);
        combatLog.log(damageCalculator.readout());
        // The damage is based on the weapon's roll, which is 1 in this case
        assertEquals("Darren hits Demon King with Sword for 1 damage!", combatLog.printLatestReadout());
    }

    @Test
    void testWeaponCritDamage() {
        // Apply a critical hit for weapon damage
        damageCalculator.applyWeaponDamage(player, monster, weapon, true);
        combatLog.log(damageCalculator.readout());
        // Critical hit doubles the weapon damage (1 + 1)
        assertEquals("Darren hits Demon King with Sword for 2 damage!", combatLog.printLatestReadout());
    }

    @Test
    void testSkillNormalDamage() {
        // Use a normal roll for skill damage
        damageCalculator.applySkillDamage(player, monster, skill, false);
        combatLog.log(damageCalculator.readout());
        // The damage is based on the skill's roll, which is 2 in this case
        assertEquals("Darren casts Fireball on Demon King for 2 damage!", combatLog.printLatestReadout());
    }

    @Test
    void testSkillCritDamage() {
        // Apply a critical hit for skill damage
        damageCalculator.applySkillDamage(player, monster, skill, true);
        combatLog.log(damageCalculator.readout());
        // Critical hit doubles the skill damage (2 + 2)
        assertEquals("Darren casts Fireball on Demon King for 4 damage!", combatLog.printLatestReadout());
    }

    @Test
    void testWeaponDamageWithTargetTakingDamage() {
        // Let's assume the weapon rolls for 1 damage (1d1)
        damageCalculator.applyWeaponDamage(player, monster, weapon, false);

        // Verifying that the target (monster) received the expected damage
        // In this case, the monster should have taken 1 damage
        assertEquals(23, monster.getHealth()); // Monster's health should reduce by 1
    }

    @Test
    void testSkillDamageWithTargetTakingDamage() {
        // Let's assume the skill rolls for 2 damage (2d1)
        damageCalculator.applySkillDamage(player, monster, skill, false);

        // Verifying that the target (monster) received the expected damage
        assertEquals(22, monster.getHealth()); // Monster's health should reduce by 2
    }
}
