package com.dicerealm.core;

import java.util.Map;

import com.dicerealm.core.entity.BodyPart;
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

	/**
	 * Choose a random character preset and create a player with that preset
	 * @return Player
	 * @see Player
	 */
	public static Player createPresetPlayer() {
		StatsMap baseStats = new StatsMap(Map.of(
			Stat.MAX_HEALTH, 20,
			Stat.ARMOUR_CLASS, 5,
			Stat.STRENGTH, 10,
			Stat.DEXTERITY, 10,
			Stat.CONSTITUTION, 10,
			Stat.INTELLIGENCE, 10,
			Stat.WISDOM, 10,
			Stat.CHARISMA, 10
		));
		Player player = new Player(getRandomCharacterName(), baseStats);
		player.getInventory().addItem(new Dummy());
		Helmet helmet = new Helmet("Iron Helmet", 1);
		player.getInventory().addItem(helmet);
		player.getInventory().addItem(new Helmet("Diamond Helmet", 4));
		player.equipItem(BodyPart.HEAD, helmet);
		return player;
	}
}
