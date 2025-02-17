package com.dicerealm.core;

import com.dicerealm.core.item.Dummy;

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
		Player player = new Player(getRandomCharacterName());
		player.getInventory().addItem(new Dummy());
		return player;
	}
}
