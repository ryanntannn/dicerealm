package com.dicerealm.core.player;

import java.util.List;
import java.util.Map;
import java.util.Random;

import com.dicerealm.core.entity.BodyPart;
import com.dicerealm.core.entity.EntityClass;
import com.dicerealm.core.entity.Race;
import com.dicerealm.core.entity.Stat;
import com.dicerealm.core.entity.StatsMap;
import com.dicerealm.core.item.Chestpiece;
import com.dicerealm.core.item.Helmet;
import com.dicerealm.core.item.Necklace;
import com.dicerealm.core.item.Weapon;
import com.dicerealm.core.item.potions.MinorHealthPotion;
import com.dicerealm.core.item.scrolls.FireballScroll;
import com.dicerealm.core.item.weapons.AxeFactory;
import com.dicerealm.core.item.weapons.BowFactory;
import com.dicerealm.core.item.weapons.DaggerFactory;
import com.dicerealm.core.item.weapons.StaffFactory;
import com.dicerealm.core.item.weapons.SwordFactory;
import com.dicerealm.core.skills.Skill;
import com.dicerealm.core.skills.SkillsRepository;

/**
 * Factory for creating preset players
 * 
 * @see Player
 */
public class PresetPlayerFactory {
	private static final Random random = new Random();

	public static final String[] CHARACTER_NAMES = {
			"Kael'thas Sunstrider",
			"Jaina Proudmoore",
			"Thrall",
			"Garrosh Hellscream",
			"Uther Lightbringer",
			"Sylvanas Windrunner",
			"Illidan Stormrage",
			"Baine Bronze",
			"Renard Moon",
			"Arnien Grey",
			"Reagan Arc",
			"Donotello",
			"Wellington",
			"Ragnarok",
			"Starknight",
	};

	public static String getRandomCharacterName() {
		return CHARACTER_NAMES[random.nextInt(CHARACTER_NAMES.length)];
	}

	public static final Race[] CHARACTER_RACE = Race.values();

	public static Race getRandomCharacterRace() {
		return CHARACTER_RACE[(int) (Math.random() * CHARACTER_RACE.length)];
	}

	public static final EntityClass[] CHARACTER_CLASS = EntityClass.values();

	public static EntityClass getRandomCharacterClass() {
		return CHARACTER_CLASS[(int) (Math.random() * CHARACTER_CLASS.length)];
	}

	/**
	 * Choose a random character preset and create a player with that preset
	 * 
	 * @return Player
	 * @see Player
	 */
	public static Player createPresetPlayer() {
		StatsMap baseStats = new StatsMap(Map.of(
				Stat.MAX_HEALTH, 20,
				Stat.ARMOUR_CLASS, 0,
				Stat.STRENGTH, 0,
				Stat.DEXTERITY, 0,
				Stat.CONSTITUTION, 0,
				Stat.INTELLIGENCE, 0,
				Stat.WISDOM, 0,
				Stat.CHARISMA, 0));
		Player player = new Player(getRandomCharacterName(), getRandomCharacterRace(), getRandomCharacterClass(),
				baseStats);
		addDefaultItems(player);
		addDefaultSkills(player);
		return player;
	}

	/**
	 * Add default items to the player
	 * 
	 * @param player
	 */
	public static void addDefaultItems(Player player) {
		EntityClass entityClass = player.getEntityClass();

        switch (entityClass) {
            case WARRIOR -> {
                Weapon sword = SwordFactory.createSword(1); 
                Weapon axe = AxeFactory.createAxe(1);
                Helmet helmet = new Helmet("Iron Helmet", 2);
				Chestpiece chestpiece = new Chestpiece("Iron Chestplate", 2);
                player.getInventory().addItem(sword);
                player.getInventory().addItem(axe);
                player.getInventory().addItem(helmet);
				player.getInventory().addItem(chestpiece);
                player.equipItem(BodyPart.RIGHT_HAND, sword);
                player.equipItem(BodyPart.LEFT_HAND, axe);
                player.equipItem(BodyPart.HEAD, helmet);
				player.equipItem(BodyPart.TORSO, chestpiece);
				

				player.getInventory().addItem(new MinorHealthPotion());
            }
            case WIZARD -> {
                Weapon staff = StaffFactory.createStaff(1);
				Necklace necklace = new Necklace("Necklace of Wizardy", Stat.INTELLIGENCE, 1);
				Chestpiece chestpiece = new Chestpiece("Cloak of Wizardy", 1);
                player.getInventory().addItem(staff);
                player.getInventory().addItem(new FireballScroll());
				player.getInventory().addItem(necklace);
                player.equipItem(BodyPart.RIGHT_HAND, staff);
				player.equipItem(BodyPart.NECK, necklace);
				player.equipItem(BodyPart.TORSO, chestpiece);
				
				player.getInventory().addItem(new MinorHealthPotion());
            }
            case ROGUE -> {
                Weapon dagger = DaggerFactory.createDagger(1);
				Necklace necklace = new Necklace("Rogue's Stealth", Stat.DEXTERITY, 2);
                player.getInventory().addItem(dagger);
				player.getInventory().addItem(necklace);
                player.equipItem(BodyPart.RIGHT_HAND, dagger);
				player.equipItem(BodyPart.NECK, necklace);
			
				player.getInventory().addItem(new MinorHealthPotion());
            }
            case RANGER -> {
                Weapon bow = BowFactory.createBow(1);
				Necklace necklace = new Necklace("Ranger's Shot", Stat.DEXTERITY, 1);
				Chestpiece chestpiece = new Chestpiece("Ranger's Chestplate", 2);
                player.getInventory().addItem(bow);
				player.equipItem(BodyPart.NECK, necklace);
                player.equipItem(BodyPart.RIGHT_HAND, bow);
				player.equipItem(BodyPart.TORSO, chestpiece);
				player.equipItem(BodyPart.NECK, necklace);
				
            }
            case CLERIC -> {
				Weapon staff = StaffFactory.createStaff(1); 
                Helmet helmet = new Helmet("Cleric's Helmet", 2);
				Necklace necklace = new Necklace("Necklace of Wisdom", Stat.WISDOM, 1);
				Chestpiece chestpiece = new Chestpiece("Cloak of Wisdom", 1);
                player.getInventory().addItem(staff);
                player.getInventory().addItem(helmet);
				player.getInventory().addItem(chestpiece);
				player.getInventory().addItem(necklace);
                player.equipItem(BodyPart.RIGHT_HAND, staff);
                player.equipItem(BodyPart.HEAD, helmet);
				player.equipItem(BodyPart.NECK, necklace);
				player.equipItem(BodyPart.TORSO, chestpiece);
			
				player.getInventory().addItem(new MinorHealthPotion());
            }

        }
	}

	public static void addDefaultSkills(Player player) {
		EntityClass entityClass = player.getEntityClass();

		// Retrieve level 1 skills for the player's class
		List<Skill> level1Skills = SkillsRepository.getNewSkillsForLevel(entityClass, 1);

		// Add all level 1 skills to the player's skill inventory
		for (Skill skill : level1Skills) {
			player.getSkillsInventory().addItem(skill);
		}
	}	
	
}
