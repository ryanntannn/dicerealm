package com.dicerealm.core;

import java.util.Map;

import com.dicerealm.core.entity.BodyPart;
import com.dicerealm.core.entity.EntityClass;
import com.dicerealm.core.entity.Race;
import com.dicerealm.core.entity.StatsMap;
import com.dicerealm.core.entity.Stat;
import com.dicerealm.core.item.Dummy;
import com.dicerealm.core.item.Helmet;

/**
 * Factory for creating preset players
 * 
 * @see Player
 */
public class PresetPlayerFactory {
	public static final String[] CHARACTER_NAMES = {
		"Kael'thas Sunstrider",
		"Jaina Proudmoore",
		"Thrall",
		"Garrosh Hellscream",
		"Uther Lightbringer",
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
	 * Choose a random character preset and create a player with that preset
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
			Stat.CHARISMA, 0
		));
		Player player = new Player(getRandomCharacterName(), getRandomCharacterRace(), getRandomCharacterClass(), baseStats);
		player.getInventory().addItem(new Dummy());
		Helmet helmet = new Helmet("Iron Helmet", 1);
		player.getInventory().addItem(helmet);
		player.getInventory().addItem(new Helmet("Diamond Helmet", 4));
		player.equipItem(BodyPart.HEAD, helmet);
		player.displayStats();
		return player;
	}
}
