package com.dicerealm.core.combat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.dicerealm.core.combat.managers.CombatManager;
import com.dicerealm.core.entity.*;
import com.dicerealm.core.item.Weapon;
import com.dicerealm.core.item.WeaponClass;
import com.dicerealm.core.player.*;
import com.dicerealm.core.monster.Monster;
import com.dicerealm.core.skills.Skill;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

class CombatManagerTest {

    private CombatManager combatManager;
    private Player player;
    private Monster monster;
    private Weapon weapon;
    private Skill skill;

    @BeforeEach
    void setUp() {
        // Create sample entities for testing
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
        monster = new Monster("Demon King", Race.DEMON, EntityClass.WARRIOR, baseStats); // Assume Monster class exists
        weapon = new Weapon("Sword", "Iron Sword forged from the Great Dwarfen Forges", ActionType.MELEE, WeaponClass.SWORD, new StatsMap(Map.of(Stat.STRENGTH, 1)), 1);
        skill = new Skill("Fireball", "A massive ball of fire", EntityClass.WIZARD, 3,2,1);

        // Add them to a list of participants
        List<Entity> participants = new ArrayList<>();
        participants.add(player);
        participants.add(monster);

        // Initialize the CombatManager with the participants
        combatManager = new CombatManager(participants);
    }

    // @Test
    // void testStartCombatWithWeaponAction() {
    //     // Inject the action (weapon) and start the combat
    //     CombatResult result = combatManager.startCombat(weapon);

    //     // Assert that the result is not null, meaning combat occurred
    //     assertNotNull(result);


    // }

    // @Test
    // void testStartCombatWithSkillAction() {


    //     // Inject the action (skill) and start the combat
    //     CombatResult result = combatManager.startCombat(skill);

    //     // Assert that the result is not null, meaning combat occurred
    //     assertNotNull(result);

    // }

    @Test
    void testCombatOverWhenAllPlayersAreDead() {
        // Simulate a scenario where the player is dead
        player.takeDamage(25); // Assuming there's a way to set health in the Player class

        // Inject the action (weapon) and start the combat
        combatManager.startCombat(weapon);

        // Now check if the combat is over
        boolean combatOver = combatManager.isCombatOver();

        // Assert that the combat is over since the player's health is 0
        assertTrue(combatOver, "Combat should be over when all players are dead");
    }

}
