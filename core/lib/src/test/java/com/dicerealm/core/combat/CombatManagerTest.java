package com.dicerealm.core.combat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.dicerealm.core.combat.managers.CombatManager;
import com.dicerealm.core.combat.systems.InitiativeResult;
import com.dicerealm.core.dice.FixedD20;
import com.dicerealm.core.entity.Entity;
import com.dicerealm.core.entity.EntityClass;
import com.dicerealm.core.entity.Race;
import com.dicerealm.core.entity.Stat;
import com.dicerealm.core.entity.StatsMap;
import com.dicerealm.core.item.Weapon;
import com.dicerealm.core.item.WeaponClass;
import com.dicerealm.core.monster.Monster;
import com.dicerealm.core.player.Player;
import com.dicerealm.core.skills.Skill;

class CombatManagerTest {

    private CombatManager combatManager;
    private Player player_1;
    private Player player_2;
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
        player_1 = new Player("Darren", Race.HUMAN, EntityClass.WARRIOR, baseStats);
        player_2 = new Player("Don", Race.HUMAN, EntityClass.WARRIOR, baseStats);
        monster = new Monster("Demon King", Race.DEMON, EntityClass.WARRIOR, baseStats); // Assume Monster class exists
        weapon = new Weapon("Sword", "Iron Sword forged from the Great Dwarfen Forges", ActionType.MELEE, WeaponClass.SWORD, new StatsMap(Map.of(Stat.STRENGTH, 1)), 1);
        skill = new Skill("Fireball", "A massive ball of fire", EntityClass.WIZARD, 3,2,1);

        // Add them to a list of participants
        List<Entity> participants = new ArrayList<>();
        participants.add(player_1);
        participants.add(player_2);
        participants.add(monster);

        // Initialize the CombatManager with the participants
        combatManager = new CombatManager(participants);
    }

    @Test
    void testStartCombatInitializesTurnOrder() {
        combatManager.startRiggedCombat();
        List<InitiativeResult> initiativeResults = combatManager.getInitiativeResults();
        assertEquals(monster, initiativeResults.get(0).getEntity(), "Monster should go first.");
        assertEquals(player_2, initiativeResults.get(1).getEntity(), "Player 2 should go second.");
        assertEquals(player_1, initiativeResults.get(2).getEntity(), "Player 1 should go third.");
    }

    @Test
    void testExecuteCombatTurnValid() {
        combatManager.startRiggedCombat();
        Entity attacker = combatManager.getCurrentTurnEntity();
        Entity target = player_1;
        CombatResult result = combatManager.executeRiggedCombatTurn(attacker, target, weapon, new FixedD20(20));
        assertEquals(result.getDamageLog(), "Demon King hits Darren with Sword for 2 damage!");
    }

    @Test
    void testExecuteCombatTurnInvalid() {
        combatManager.startRiggedCombat();
        Entity attacker = player_2;
        Entity target = player_1;
        CombatResult result = combatManager.executeCombatTurn(attacker, target, weapon);
        assertNull(result, "Attacker should not act out of turn.");
    }

    @Test
    void testExecuteRiggedSkillCombatTurn() {
        combatManager.startRiggedCombat();
        combatManager.startRound();
        combatManager.startTurn(); 
        combatManager.endTurn(); 
        Entity attacker = combatManager.getCurrentTurnEntity();
        Entity target = monster;
        CombatResult result = combatManager.executeRiggedCombatTurn(attacker, target, skill, new FixedD20(20));
        assertEquals(result.getDamageLog(), "Don casts Fireball on Demon King for 4 damage!");
    }

    @Test
    void testCombatOverWhenAllMonstersAreDead() {
        monster.takeDamage(25); 
        combatManager.startCombat();
        boolean combatOver = combatManager.isCombatOver();
        assertTrue(combatOver, "Combat should be over when all players are dead");
    }

    @Test
    void testStartRound() {
        combatManager.startRiggedCombat(); 
        combatManager.startRound();
        assertEquals(0, combatManager.getCurrentTurnIndex(), "The current turn index should be reset to 0 at the start of a round.");
        assertEquals(monster, combatManager.getCurrentTurnEntity(), "The first entity in the turn order should be the current turn entity.");
    }
    
    @Test
    void testStartTurn() {
        combatManager.startRiggedCombat();
        combatManager.startRound();
        combatManager.startTurn();
        assertEquals(monster, combatManager.getCurrentTurnEntity(), "The first entity in the turn order should be the current turn entity.");
    }
    
    @Test
    void testEndTurn() {
        combatManager.startRiggedCombat();
        combatManager.startRound(); 
        combatManager.startTurn();
        combatManager.endTurn();
        assertEquals(1, combatManager.getCurrentTurnIndex(), "The current turn index should be incremented after ending a turn.");
        assertEquals(player_2, combatManager.getCurrentTurnEntity(), "The next entity in the turn order should be the current turn entity.");
    }
    
    @Test
    void testEndTurnWrapsToNextRound() {
        combatManager.startRiggedCombat(); 
        combatManager.startRound();
        combatManager.startTurn();
        combatManager.endTurn();
        combatManager.startTurn();
        combatManager.endTurn();
        combatManager.startTurn();
        combatManager.endTurn();
        assertEquals(0, combatManager.getCurrentTurnIndex(), "The current turn index should wrap back to 0 after all turns are completed.");
        assertEquals(monster, combatManager.getCurrentTurnEntity(), "The first entity in the turn order should be the current turn entity at the start of a new round.");
    }
}
