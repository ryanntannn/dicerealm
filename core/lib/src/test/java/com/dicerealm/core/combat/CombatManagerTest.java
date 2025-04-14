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
import com.dicerealm.core.combat.managers.MonsterAI;
import com.dicerealm.core.combat.systems.InitiativeResult;
import com.dicerealm.core.dice.FixedD20;
import com.dicerealm.core.entity.BodyPart;
import com.dicerealm.core.entity.Entity;
import com.dicerealm.core.entity.EntityClass;
import com.dicerealm.core.entity.Race;
import com.dicerealm.core.entity.Stat;
import com.dicerealm.core.entity.StatsMap;
import com.dicerealm.core.item.Weapon;
import com.dicerealm.core.item.WeaponClass;
import com.dicerealm.core.monster.Monster;
import com.dicerealm.core.player.Player;
import com.dicerealm.core.skills.DummySkill;
import com.dicerealm.core.skills.Skill;

class CombatManagerTest {

    private CombatManager combatManager;
    private MonsterAI monsterAI;
    private Player player_1;
    private Player player_2;
    private Monster monster;
    private Weapon weapon;
    private Skill skill;
    private Skill skill_2;
    private Skill skill_3;

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
        skill = new Skill("Fireball", "A massive ball of fire", EntityClass.WIZARD, ActionType.MAGIC, 3,2,1,2);
        skill_2 = new DummySkill(); // Dummy skill for testing
        skill_3 = new DummySkill(); // Dummy skill for testing
        monster.getInventory().addItem(weapon); // Add weapon to monster's inventory
        monster.equipItem(BodyPart.LEFT_HAND, weapon);
        player_1.addSkill(skill); // Add skill to player_1's inventory
        player_2.addSkill(skill_2); // Add skill to player_2's inventory
        monster.addSkill(skill_3);
        // Add them to a list of participants
        List<Entity> participants = new ArrayList<>();
        participants.add(player_1);
        participants.add(player_2);
        participants.add(monster);

        // Initialize the CombatManager with the participants
        combatManager = new CombatManager(participants);
        monsterAI = new MonsterAI();
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
        CombatResult result = combatManager.executeRiggedCombatTurn(attacker, target, skill_2, new FixedD20(20));
        assertEquals(result.getAttacker(), player_2, "The attacker should be Player 2.");
        assertEquals(result.getTarget(), monster, "The target should be the monster.");
        assertEquals(result.getDamageLog(), "Don casts Dummy Skill on Demon King for 2 damage!");
    }

    @Test
    void testCombatOverWhenAllMonstersAreDead() { 
        combatManager.startCombat();
        monster.takeDamage(25); 
        combatManager.endTurn();
        boolean combatOver = combatManager.isCombatOver();
        assertTrue(combatOver, "Combat should be over when all players are dead");
        assertEquals(combatManager.getTurnOrderIds().length, 2);
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

    @Test
    void testMonsterAITargetSelection() {

        combatManager.startRiggedCombat();
        monsterAI.setCombatManager(combatManager);
        Entity attacker = combatManager.getCurrentTurnEntity();

        // Ensure the attacker is the monster
        assertEquals(monster, attacker, "The first turn should belong to the monster.");
        player_1.takeDamage(2);
        // Use MonsterAI to determine the target and action
        assertEquals(combatManager.getParticipants().size(), 3, "There should be 3 participants in the combat.");
        CombatResult result = monsterAI.handleMonsterTurn(combatManager.getParticipants(), monster);

        // Verify the target is the player with the lowest health
        Entity target = result.getTarget();
        assertEquals(monster, result.getAttacker(), "The attacker should be the monster.");
        assertEquals(player_2, target, "The monster should target the player with the highest health.");

    }

    @Test
    void testSkillCooldownActivation() {
        combatManager.startRiggedCombat();
        combatManager.startRound();
        combatManager.startTurn();

        // Use a skill and activate its cooldown
        Entity attacker = combatManager.getCurrentTurnEntity();
        CombatResult result = combatManager.executeCombatTurn(attacker, player_1, skill_3);
        assertEquals(skill_3.getRemainingCooldown(), skill_3.getCooldown(), "The skill's cooldown should be activated after use.");
    }

    @Test
    void testSkillCooldownReductionAfterFullRound() {
        combatManager.startRiggedCombat();
        combatManager.startRound();
        combatManager.startTurn();

        // Use a skill and activate its cooldown
        Entity attacker = combatManager.getCurrentTurnEntity();
        combatManager.executeCombatTurn(attacker, player_1, skill_3);
        assertEquals(skill_3.getRemainingCooldown(), skill_3.getCooldown(), "The skill's cooldown should be activated after use.");

        // Complete a full round
        combatManager.endTurn();
        combatManager.startTurn();
        combatManager.endTurn();
        combatManager.startTurn();
        combatManager.endTurn();

        // Verify the cooldown is reduced
        assertEquals(skill_3.getCooldown() - 1, skill_3.getRemainingCooldown(), "The skill's cooldown should be reduced by 1 after a full round.");
    }

    @Test
    void testSkillCannotBeUsedWhileOnCooldown() {
        combatManager.startRiggedCombat();
        combatManager.startRound();

        // Use a skill and activate its cooldown
        Entity attacker = combatManager.getCurrentTurnEntity();
        combatManager.executeCombatTurn(attacker, player_1, skill_3);
        assertEquals(skill_3.getRemainingCooldown(), skill_3.getCooldown(), "The skill's cooldown should be activated after use.");

        // Attempt to use the skill again while it's on cooldown
        CombatResult result = combatManager.executeCombatTurn(attacker, player_1, skill_3);
        assertNull(result, "The skill should not be usable while on cooldown.");
    }

    @Test
    void testSkillCooldownFullyExpires() {
        combatManager.startRiggedCombat();
        combatManager.startRound();

        // Use a skill and activate its cooldown
        Entity attacker = combatManager.getCurrentTurnEntity();
        combatManager.executeCombatTurn(attacker, player_1, skill_3);
        assertEquals(skill_3.getRemainingCooldown(), skill_3.getCooldown(), "The skill's cooldown should be activated after use.");

      
        combatManager.endTurn(); // Monster's turn ends
        combatManager.endTurn(); // Player 2's turn ends
        combatManager.endTurn(); // Player 1's turn ends (round ends)
        assertEquals(2, combatManager.getCurrentRoundIndex(), "The current round index should be incremented after a full round.");
        assertEquals(4, skill_3.getRemainingCooldown(), "The skill's cooldown should be reduced after a full round.");
        combatManager.endTurn(); // Monster's turn ends
        combatManager.endTurn(); // Player 2's turn ends
        combatManager.endTurn(); // Player 1's turn ends (round ends)
        assertEquals(3, combatManager.getCurrentRoundIndex(), "The current round index should be incremented after a full round.");
       
        for (int i = 0; i < 3; i++) {
            combatManager.endTurn(); // Monster's turn ends
            combatManager.endTurn(); // Player 2's turn ends
            combatManager.endTurn(); // Player 1's turn ends (round ends)
        }
        
        // Verify the cooldown is fully expired
        assertEquals(0, skill_3.getRemainingCooldown(), "The skill's cooldown should be fully expired after the required number of rounds.");
        assertTrue(skill_3.isUsable(), "The skill should be usable after its cooldown expires.");
    }
}
