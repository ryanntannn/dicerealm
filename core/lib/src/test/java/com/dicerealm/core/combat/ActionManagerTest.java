package com.dicerealm.core.combat;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.dicerealm.core.combat.managers.ActionManager;
import com.dicerealm.core.dice.FixedD20;
import com.dicerealm.core.entity.BodyPart;
import com.dicerealm.core.entity.EntityClass;
import com.dicerealm.core.entity.Race;
import com.dicerealm.core.entity.Stat;
import com.dicerealm.core.entity.StatsMap;
import com.dicerealm.core.item.Potion;
import com.dicerealm.core.item.Scroll;
import com.dicerealm.core.item.Weapon;
import com.dicerealm.core.item.WeaponClass;
import com.dicerealm.core.monster.Monster;
import com.dicerealm.core.player.Player;
import com.dicerealm.core.skills.Skill;

public class ActionManagerTest {
    private Player player;
    private Monster monster;
    private ActionManager actionManager;
    private CombatLog combatLog;
    private Weapon weapon;
    private Skill skill;
    private Scroll scroll;
    private Potion potion;
    private CombatResult combatResult;

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
        skill = new Skill("Fireball", "A massive ball of fire", EntityClass.WIZARD, ActionType.MAGIC, 3, 2, 1, 2);
        scroll = new Scroll("Fireball Scroll", "A scroll containing a powerful fireball spell", 1, 1);
        potion = new Potion("Health Potion", "Health Potion", 1, 1);
        player.getInventory().addItem(weapon);
        player.equipItem(BodyPart.RIGHT_HAND, weapon);
        player.addSkill(skill);
        player.getInventory().addItem(scroll);
        player.getInventory().addItem(potion);
    }

    @Test
    void testPerformAttack_Hit() {
        actionManager.rigDice(new FixedD20(19)); // Rolls 19, which should be a hit
        combatResult = actionManager.performAttack(player, monster, weapon);

        assertEquals("Darren hits Demon King with Sword for 1 damage (max 1d1)!", combatResult.getDamageLog());
        assertEquals(27, monster.getHealth());
    }

    @Test
    void testPerformAttack_CriticalHit() {
        actionManager.rigDice(new FixedD20(20));  // Rolls 20, which should be a crit
        combatResult = actionManager.performAttack(player, monster, weapon);

        assertEquals("Darren hits Demon King with Sword for 2 damage (max 1d1)!", combatResult.getDamageLog());
        assertEquals(26, monster.getHealth());
    }

    @Test
    void testPerformAttack_Miss() {
        actionManager.rigDice(new FixedD20(2)); // Rolls 2, which should be a miss
        combatResult = actionManager.performAttack(player, monster, weapon);

        String log = combatResult.getHitLog();
        assertTrue(log.contains("MISS"), "Expected log to contain 'MISS'");
    }

    @Test
    void testPerformSkillAttack_Hit() {
        actionManager.rigDice(new FixedD20(19)); // Rolls 19, should hit
        combatResult = actionManager.performSkillAttack(player, monster, skill);

        assertEquals("Darren casts Fireball on Demon King for 2 damage (max 2d1)!", combatResult.getDamageLog());
        assertEquals(26, monster.getHealth());
    }

    @Test
    void testPerformSkillAttack_CriticalHit() {
        actionManager.rigDice(new FixedD20(20)); // Rolls 20, should be a crit
        combatResult = actionManager.performSkillAttack(player, monster, skill);

        assertEquals("Darren casts Fireball on Demon King for 4 damage (max 2d1)!", combatResult.getDamageLog());
        assertEquals(24, monster.getHealth());
    }

    @Test
    void testPerformSkillAttack_Miss() {
        actionManager.rigDice(new FixedD20(2)); // Rolls 2, should miss
        combatResult = actionManager.performSkillAttack(player, monster, skill);

        String log = combatResult.getHitLog();
        assertTrue(log.contains("MISS"), "Expected log to contain 'MISS'");
    }

    @Test
    void testPotionUsage() {
        assertNotNull(player.getInventory().getItem(potion.getId()));
        player.takeDamage(1);
        actionManager.usePotion(player, potion);
        assertEquals(28, player.getHealth(), "Player should be at max health after using potion");
        assertNull(player.getInventory().getItem(potion.getId()));
    }

    @Test
    void testScrollUsage() {
        assertNotNull(player.getInventory().getItem(scroll.getId()));
        actionManager.rigDice(new FixedD20(19));
        actionManager.useScroll(player, monster, scroll);
        assertEquals(27, monster.getHealth(), "Monster should have taken damage from the scroll");
        assertNull(player.getInventory().getItem(scroll.getId()));
    }
}