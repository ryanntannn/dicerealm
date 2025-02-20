package com.dicerealm.core;

import java.util.Map;

import com.dicerealm.core.entity.BodyPart;
import com.dicerealm.core.entity.StatsMap;
import com.dicerealm.core.entity.Stat;
import com.dicerealm.core.item.Dummy;
import com.dicerealm.core.item.Helmet;

/**
 * Factory for creating preset monsters
 * 
 * @see Monster
 */
public class PresetMonsterFactory {
	public static final String[] CHARACTER_NAMES = {
		"Skeleton",
		"Zombie",
		"Thrall",
		"Garold",
		"Mindflayer",
	};

	public static String getRandomCharacterName() {
		return CHARACTER_NAMES[(int) (Math.random() * CHARACTER_NAMES.length)];
	}

	public static final String[] CHARACTER_RACE = {
			"Human",
			"Dwarf",
			"Elf",
			"Tiefling",
			"Demon",
	};

	public static String getRandomCharacterRace() {
		return CHARACTER_RACE[(int) (Math.random() * CHARACTER_RACE.length)];
	}

	public static final String[] CHARACTER_CLASS = {
			"Warrior",
			"Ranger",
			"Rogue",
			"Wizard",
			"Cleric",
	};

	public static String getRandomCharacterClass() {
		return CHARACTER_CLASS[(int) (Math.random() * CHARACTER_CLASS.length)];
	}

	/**
	 * Choose a random character preset and create a monster with that preset
	 * @return Monster
	 * @see Monster
	 */
	public static Monster createPresetMonster() {
		StatsMap baseStats = new StatsMap(Map.of(
			Stat.MAX_HEALTH, 20,
			Stat.ARMOUR_CLASS, 12,
			Stat.STRENGTH, 11,
			Stat.DEXTERITY, 12,
			Stat.CONSTITUTION, 11,
			Stat.INTELLIGENCE, 6,
			Stat.WISDOM, 6,
			Stat.CHARISMA, 6
		));
		Monster monster = new Monster(getRandomCharacterName(), getRandomCharacterRace(), getRandomCharacterClass(), baseStats);
		monster.displayStats();
		return monster;
	}
}
