package com.dicerealm.core.combat;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.dicerealm.core.dice.FixedD20;
import com.dicerealm.core.entity.*;
import com.dicerealm.core.item.Weapon;
import com.dicerealm.core.item.WeaponClass;
import com.dicerealm.core.monster.Monster;
import com.dicerealm.core.player.Player;
import com.dicerealm.core.skills.Skill;

import java.util.Map;

public class ActionManagerTest {
    private Player player;
    private Monster monster;
    private ActionManager actionManager;
    private CombatLog combatLog;
    private Weapon weapon;
    private Skill skill;

    @BeforeEach
    void setUp() {
        // Initialize stats for player and monster
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
        actionManager = new ActionManager(combatLog);

        // Create a test weapon
        weapon = new Weapon("Sword", "Iron Sword forged from the Great Dwarfen Forges", ActionType.MELEE, WeaponClass.SWORD, new StatsMap(Map.of(Stat.STRENGTH, 1)), 1);
        skill = new Skill("Fireball", "A massive ball of fire", EntityClass.WIZARD, 3,2,1);
        player.equipItem(BodyPart.RIGHT_HAND, weapon);

    }

    @Test
    void testPerformAttack_Hit() {
        actionManager.rigDice(new FixedD20(19)); // Rolls 19, which should be a hit
        actionManager.performAttack(player, monster, weapon);

        assertEquals("Darren hits Demon King with Sword for 1 damage!", combatLog.printLatestReadout());
        assertEquals(23, monster.getHealth());
    }

    @Test
    void testPerformAttack_CriticalHit() {
        actionManager.rigDice(new FixedD20(20));  // Rolls 20, which should be a crit
        actionManager.performAttack(player, monster, weapon);

        assertEquals("Darren hits Demon King with Sword for 2 damage!", combatLog.printLatestReadout());
        assertEquals(22, monster.getHealth());
    }

    @Test
    void testPerformAttack_Miss() {
        actionManager.rigDice(new FixedD20(2)); // Rolls 2, which should be a miss
        actionManager.performAttack(player, monster, weapon);

        String log = combatLog.printLatestReadout();
        assertTrue(log.contains("MISS"), "Expected log to contain 'MISS'");
    }

    @Test
    void testPerformSkillAttack_Hit() {
        actionManager.rigDice(new FixedD20(19)); // Rolls 18, should hit
        actionManager.performSkillAttack(player, monster, skill);

        assertEquals("Darren casts Fireball on Demon King for 2 damage!", combatLog.printLatestReadout());
        assertEquals(22, monster.getHealth());
    }

    @Test
    void testPerformSkillAttack_CriticalHit() {
        actionManager.rigDice(new FixedD20(20)); // Rolls 20, should be a crit
        actionManager.performSkillAttack(player, monster, skill);

        assertEquals("Darren casts Fireball on Demon King for 4 damage!", combatLog.printLatestReadout());
        assertEquals(20, monster.getHealth());
    }

    @Test
    void testPerformSkillAttack_Miss() {
        actionManager.rigDice(new FixedD20(2)); // Rolls 3, should miss
        actionManager.performSkillAttack(player, monster, skill);

        String log = combatLog.printLatestReadout();
        assertTrue(log.contains("MISS"), "Expected log to contain 'MISS'");
    }
}

