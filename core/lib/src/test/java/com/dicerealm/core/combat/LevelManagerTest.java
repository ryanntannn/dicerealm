package com.dicerealm.core.combat;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.dicerealm.core.combat.managers.LevelManager;
import com.dicerealm.core.entity.EntityClass;
import com.dicerealm.core.entity.Race;
import com.dicerealm.core.entity.Stat;
import com.dicerealm.core.entity.StatsMap;
import com.dicerealm.core.player.Player;
import com.dicerealm.core.room.RoomState;
import com.dicerealm.core.skills.Skill;
import com.dicerealm.core.skills.SkillsRepository;

public class LevelManagerTest {

    private LevelManager levelManager;
    private RoomState roomState;
    private Player player_warrior;
    private Player player_wizard;

    @BeforeEach
    public void setUp() {
        // Initialize the level manager
        levelManager = new LevelManager();
        
        // Initialize room state
        roomState = new RoomState();
        roomState.setRoomLevel(1);

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
        
        // Create test players with different classes
        player_warrior = new Player("Darren", Race.HUMAN, EntityClass.WARRIOR, baseStats);
        player_wizard = new Player("Don", Race.HUMAN, EntityClass.WIZARD, baseStats);
        
    }
    
    @Test
    public void testAddExperienceAndLevelUp() {
        // Initial state
        assertEquals(1, roomState.getRoomLevel());
        assertEquals(0, roomState.getRoomExperience());
        
        // Add some XP but not enough to level up
        levelManager.addExperience(50, roomState);
        assertEquals(1, roomState.getRoomLevel());
        assertEquals(50, roomState.getRoomExperience());
        
        // Add more XP to trigger level up
        levelManager.addExperience(50, roomState);
        boolean levelledUp = levelManager.checkLevelUp(roomState);
        assertTrue(levelledUp, "Room should level up when enough XP is added");
        assertEquals(2, roomState.getRoomLevel());
        assertEquals(0, roomState.getRoomExperience());
        
        // Add XP for another level up plus some overflow
        levelManager.addExperience(250, roomState);
        levelledUp = levelManager.checkLevelUp(roomState);
        assertTrue(levelledUp,"Room should level up when reaching enough XP");
        assertEquals(3, roomState.getRoomLevel());
        assertEquals(50, roomState.getRoomExperience()); // 250 - 200 = 50 overflow
    }
    
    @Test
    public void testXpRequiredForNextLevel() {
        // Level 1 requires 100 XP
        assertEquals(100, levelManager.getXpForNextLevel(roomState));
        
        // Level 2 requires 200 XP
        roomState.setRoomLevel(2);
        assertEquals(200, levelManager.getXpForNextLevel(roomState));
        
        // Level 5 requires 500 XP
        roomState.setRoomLevel(5);
        assertEquals(500, levelManager.getXpForNextLevel(roomState));
    }
    
    @Test
    public void testPrepareSkillSelection() {
        // Set room to level 2 for more interesting skill selection
        roomState.setRoomLevel(2);
        
        // Test warrior skill selection
        List<Skill> warriorSkills = levelManager.preparePlayerSkillSelection(player_warrior, roomState.getRoomLevel());
        assertNotNull(warriorSkills, "Should return skills for warrior");
        assertFalse(warriorSkills.isEmpty(),"Should have available skills");
        assertTrue(levelManager.hasPendingSkillSelection(player_warrior.getId()), "Warrior should have pending skill selection");
        
        // Test wizard skill selection
        List<Skill> wizardSkills = levelManager.preparePlayerSkillSelection(player_wizard, roomState.getRoomLevel());
        assertNotNull(wizardSkills, "Should return skills for wizard");
        assertFalse(wizardSkills.isEmpty(),"Should have available skills");
        assertTrue(levelManager.hasPendingSkillSelection(player_wizard.getId()), "Wizard should have pending skill selection");
        
        // Verify different classes get different skills
        assertNotEquals(warriorSkills.get(0).getDisplayName(),
                      wizardSkills.get(0).getDisplayName(),
                      "Warrior and wizard should get different skills");
    }
    
    @Test
    public void testProcessSkillSelection_AddSkill() {
        // Setup
        roomState.setRoomLevel(1);
        List<Skill> warriorSkills = levelManager.preparePlayerSkillSelection(player_warrior, roomState.getRoomLevel());
        
        // Verify initial state
        int initialSkillCount = player_warrior.getSkillsInventory().getItems().size();
        assertEquals(initialSkillCount, 0, "Player should start with no skills");
        
        // Select the first available skill
        Skill selectedSkill = warriorSkills.get(0);
        boolean success = levelManager.processSkillSelection(
            player_warrior.getId(),
            selectedSkill.getId(),
            null, // no replacement
            player_warrior
        );
        
        // Verify results
        assertTrue(success, "Skill selection should succeed");
        List<Skill> playerSkills = player_warrior.getSkillsInventory().getItems();
        assertEquals(1, playerSkills.size(),"Player should now have 1 skill");
        assertEquals(selectedSkill.getDisplayName(), playerSkills.get(0).getDisplayName(), "Player should have the selected skill");
        assertFalse(levelManager.hasPendingSkillSelection(player_warrior.getId()), "Pending skill selection should be cleared");
    }
    
    @Test
    public void testProcessSkillSelection_ReplaceSkill() {
        // Setup - fill player's skill inventory to capacity
        for (int i = 0; i < 4; i++) { // Using MAX_SKILLS=4 from LevelManager
            Skill fillerSkill = createTestSkill("FillerSkill" + i, EntityClass.WARRIOR);
            player_warrior.getSkillsInventory().addItem(fillerSkill);
        }
        
        // Prepare skill selection
        roomState.setRoomLevel(2);
        List<Skill> warriorSkills = levelManager.preparePlayerSkillSelection(player_warrior, roomState.getRoomLevel());
        
        // Get a skill to replace and a new skill to add
        Skill skillToReplace = player_warrior.getSkillsInventory().getItems().get(0);
        Skill newSkill = warriorSkills.get(0);
        
        // Replace skill
        boolean success = levelManager.processSkillSelection(
            player_warrior.getId(),
            newSkill.getId(),
            skillToReplace.getId(),
            player_warrior
        );
        
        // Verify results
        assertTrue(success, "Skill replacement should succeed");
        List<Skill> playerSkills = player_warrior.getSkillsInventory().getItems();
        assertEquals(4, playerSkills.size(), "Player should still have max skills");
        assertEquals(newSkill.getDisplayName(), playerSkills.get(0).getDisplayName(), "Player should have the new skill");
        assertFalse(hasSkillWithName(playerSkills, skillToReplace.getDisplayName()),"Player should not have the replaced skill");
        assertFalse(levelManager.hasPendingSkillSelection(player_warrior.getId()), "Pending skill selection should be cleared");
    }
    
    @Test
    public void testProcessSkillSelection_FullInventory() {
        // Fill player's skill inventory to capacity
        for (int i = 0; i < 4; i++) { // MAX_SKILLS=4
            Skill fillerSkill = createTestSkill("FillerSkill" + i, EntityClass.WARRIOR);
            player_warrior.getSkillsInventory().addItem(fillerSkill);
        }
        
        // Prepare skill selection
        roomState.setRoomLevel(2);
        List<Skill> warriorSkills = levelManager.preparePlayerSkillSelection(player_warrior, roomState.getRoomLevel());
        
        // Try to add skill without replacing (should fail)
        boolean success = levelManager.processSkillSelection(
            player_warrior.getId(),
            warriorSkills.get(0).getId(),
            null, // no replacement specified
            player_warrior
        );
        
        // Verify failure
        assertFalse(success, "Skill selection should fail when inventory full with no replacement");
        assertEquals(4, player_warrior.getSkillsInventory().getItems().size(),"Player should still have 4 skills");
    }
    
    @Test
    public void testSkillsRepository_FilterNewSkills() {
        // Create skills
        Skill skill1 = createTestSkill("Slash", EntityClass.WARRIOR);
        Skill skill2 = createTestSkill("Shield Bash", EntityClass.WARRIOR);
        Skill skill3 = createTestSkill("Cleave", EntityClass.WARRIOR);
        
        // Add skill1 to player's inventory
        player_warrior.getSkillsInventory().addItem(skill1);
        
        // Available skills contains all three
        List<Skill> availableSkills = List.of(skill1, skill2, skill3);
        
        // Filter new skills
        List<Skill> filteredSkills = SkillsRepository.filterNewSkills(
            availableSkills, 
            player_warrior.getSkillsInventory().getItems()
        );
        
        // Verify filtering
        assertEquals(2, filteredSkills.size(), "Should only include skills player doesn't have");
        assertFalse(hasSkillWithName(filteredSkills, skill1.getDisplayName()), "Should not include skill1 in filtered skills");
        assertTrue(hasSkillWithName(filteredSkills, skill2.getDisplayName()), "Should include skill2 in filtered skills");
        assertTrue(hasSkillWithName(filteredSkills, skill3.getDisplayName()), "Should include skill3 in filtered skills");
    }
    
    static class TestSkill extends Skill {
            
        public TestSkill(String name, EntityClass requiredClass) {
            super(name, "Test description", requiredClass, ActionType.SKILL, 0, 1, 0, 0);
        }
    }

    private Skill createTestSkill(String name, EntityClass requiredClass) {
        return new TestSkill(name, requiredClass);
    }

    private boolean hasSkillWithName(List<Skill> skills, String name) {
        return skills.stream().anyMatch(skill -> skill.getDisplayName().equals(name));
    }

}