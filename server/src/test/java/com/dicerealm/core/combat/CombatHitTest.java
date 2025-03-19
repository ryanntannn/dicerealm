package com.dicerealm.core.combat;

import static org.junit.jupiter.api.Assertions.*;
import com.dicerealm.core.dice.FixedD20;
import com.dicerealm.core.entity.Stat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.dicerealm.core.entity.EntityClass;
import com.dicerealm.core.entity.Race;

import com.dicerealm.core.entity.StatsMap;
import com.dicerealm.core.monster.Monster;
import com.dicerealm.core.player.Player;


import java.util.Map;

public class CombatHitTest {
    private Player player;
    private Monster monster;

    private CombatLog combatLog;
    private HitCalculator hitCalculator;

    // Custom D20 implementation for testing
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

    }

    @Test
    void testCriticalHit() {
        //Rigs dice for Nat 20
        hitCalculator = new HitCalculator(new FixedD20(20));

        HitResult hitResult = hitCalculator.doesAttackHit(player, monster, ActionType.MELEE);
        combatLog.log(hitResult.getHitLog());
        assertEquals(AttackResult.CRIT_HIT, hitResult.getAttackResult());
        assertEquals("Darren rolls a NATURAL 20! CRITICAL HIT!", combatLog.printLatestReadout());
    }

    @Test
    void testCriticalMiss() {
        //Rigs dice for Nat 1
        hitCalculator = new HitCalculator(new FixedD20(1));
        HitResult hitResult = hitCalculator.doesAttackHit(player, monster,  ActionType.MELEE);
        combatLog.log(hitResult.getHitLog());
        assertEquals(AttackResult.CRIT_MISS, hitResult.getAttackResult());
        assertEquals("Darren rolls a NATURAL 1! CRITICAL MISS!", combatLog.printLatestReadout());
    }

    @Test
    void testNormalHit() {
        //Rigs Dice to guarantee Hit
        hitCalculator = new HitCalculator(new FixedD20(19));
        HitResult hitResult = hitCalculator.doesAttackHit(player, monster,  ActionType.MELEE);
        combatLog.log(hitResult.getHitLog());
        assertEquals(AttackResult.HIT, hitResult.getAttackResult());
        assertTrue(combatLog.printLatestReadout().contains("HIT!"));
    }

    @Test
    void testMiss() {
        //Rigs Dice to guarantee miss
        hitCalculator = new HitCalculator(new FixedD20(2));
        HitResult hitResult = hitCalculator.doesAttackHit(player, monster,  ActionType.MELEE);
        combatLog.log(hitResult.getHitLog());
        assertEquals(AttackResult.MISS, hitResult.getAttackResult());
        assertTrue(combatLog.printLatestReadout().contains("MISS!"));
    }

}
