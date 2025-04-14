package com.dicerealm.core.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.dicerealm.core.combat.managers.CombatManager;
import com.dicerealm.core.combat.managers.MonsterAI;
import com.dicerealm.core.command.combat.CombatTurnActionCommand;
import com.dicerealm.core.command.combat.CombatTurnActionCommand.ActionType;
import com.dicerealm.core.entity.BodyPart;
import com.dicerealm.core.entity.Entity;
import com.dicerealm.core.entity.EntityClass;
import com.dicerealm.core.entity.Race;
import com.dicerealm.core.entity.Stat;
import com.dicerealm.core.entity.StatsMap;
import com.dicerealm.core.item.Potion;
import com.dicerealm.core.item.Weapon;
import com.dicerealm.core.item.weapons.DummyWeapon;
import com.dicerealm.core.monster.Monster;
import com.dicerealm.core.player.Player;
import com.dicerealm.core.room.RoomContext;
import com.dicerealm.core.room.RoomState;
import com.dicerealm.core.skills.DummySkill;
import com.dicerealm.core.skills.Skill;
import com.dicerealm.core.strategy.BroadcastStrategy;
import com.dicerealm.mock.MockBroadcastStrategy;

public class CombatTurnActionHandlerTest {

    private CombatTurnActionHandler combatTurnActionHandler;
    private RoomContext roomContext;
    private CombatManager combatManager;
    private BroadcastStrategy broadcastStrategy;
    private MonsterAI monsterAI;
    private RoomState roomState;
    private Player player_1;
    private Player player_2;
    private Monster monster;
    private Skill skill;
    private Weapon weapon;
    private Weapon weapon_2;
    private Potion potion;

    @BeforeEach
    void setUp() {
        // Initialize RoomContext and CombatManager
        broadcastStrategy = new MockBroadcastStrategy();
        combatManager = new CombatManager();
        monsterAI = new MonsterAI();
        roomState = new RoomState();
        roomContext = new RoomContext(roomState, null, broadcastStrategy, null, combatManager, monsterAI, null);
        roomState.setState(RoomState.State.BATTLE);

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
        weapon = new DummyWeapon();
        weapon_2 = new DummyWeapon();
        skill = new DummySkill();
        monster.getInventory().addItem(weapon); // Add weapon to monster's inventory
        monster.equipItem(BodyPart.LEFT_HAND, weapon);
        player_2.getInventory().addItem(weapon_2); // Add weapon to player_2's inventory
        player_2.equipItem(BodyPart.LEFT_HAND, weapon_2);
        // Add them to a list of participants
        List<Entity> participants = new ArrayList<>();
        participants.add(monster);
        participants.add(player_2);
        participants.add(player_1);

        combatTurnActionHandler = new CombatTurnActionHandler();

        // Add participants to the combat manager
        combatManager.newCombat(participants);
        combatManager.startRiggedCombat();

        // Create a potion
        potion = new Potion("Health Potion", "Restores health", 1, 6);
        player_1.getInventory().addItem(potion);
        player_1.getSkillsInventory().addItem(skill);
    }


    @Test
    void testHandleSkillAttackCooldown(){
        
        combatManager = new CombatManager();
        monsterAI = new MonsterAI();
        monsterAI.setCombatManager(combatManager);
        roomContext = new RoomContext(roomState, null, broadcastStrategy, null, combatManager, monsterAI, null);

        List<Entity> participants = new ArrayList<>();
        participants.add(monster);
        participants.add(player_2);
        participants.add(player_1);

        // Add participants to the combat manager
        combatManager.newCombat(participants);
        combatManager.startRiggedCombat();

        Skill skillToUse = player_1.getSkillsInventory().getItems().get(0);

        CombatTurnActionCommand command = new CombatTurnActionCommand(player_1, monster, skillToUse, ActionType.SKILL);

        // Handle the action
        combatTurnActionHandler.handle(player_1.getId(), command, roomContext);
        assertEquals(1, combatManager.getCurrentRoundIndex());

        // Verify the skill is on cooldown
        assertEquals(5, skill.getRemainingCooldown(), "Fireball should be on cooldown after use.");


        command = new CombatTurnActionCommand(player_2, monster, weapon_2, ActionType.SKILL);
        combatTurnActionHandler.handle(player_2.getId(), command, roomContext);

        assertEquals(2, combatManager.getCurrentRoundIndex());
        assertEquals(4, skill.getRemainingCooldown(), "Fireball should be on cooldown after use.");

    }

    @Test
    void testHandlePotionUsage() {
        // Reduce player's health
        player_1.takeDamage(10);

        // Create a combat turn action command for using a potion
        CombatTurnActionCommand command = new CombatTurnActionCommand(player_1, player_1, potion, ActionType.POTION);

        // Handle the action
        combatTurnActionHandler.handle(player_1.getId(), command, roomContext);

        // Verify the player's health is restored
        assertTrue(player_1.getHealth() > 14, "Player's health should be restored after using the potion.");
    }

    @Test
    void testInvalidTurnAction() {
        // Create a combat turn action command for a weapon attack
        CombatTurnActionCommand command = new CombatTurnActionCommand(player_1, monster, weapon, ActionType.WEAPON);

        // Simulate it being the monster's turn
        combatManager.endTurn();

        // Verify that an exception is thrown for an invalid turn
        assertThrows(IllegalArgumentException.class, () -> {
            combatTurnActionHandler.handle(player_1.getId(), command, roomContext);
        }, "Should throw an exception for an invalid turn.");
    }

    @Test
    void testCombatEndsWhenMonsterDies() {
        // Reduce the monster's health to 0
        monster.takeDamage(monster.getHealth());

        // Verify that combat ends
        assertTrue(combatManager.isCombatOver(), "Combat should end when the monster dies.");
    }
}