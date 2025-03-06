package com.dicerealm.core.monster;

import java.util.Map;

import com.dicerealm.core.entity.EntityClass;
import com.dicerealm.core.entity.Race;
import com.dicerealm.core.entity.StatsMap;
import com.dicerealm.core.entity.Stat;


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
		"Mind Flayer",
	};

	public static String getRandomCharacterName() {
		return CHARACTER_NAMES[(int) (Math.random() * CHARACTER_NAMES.length)];
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
