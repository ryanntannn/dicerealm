package com.dicerealm.core;

import com.dicerealm.core.item.Dummy;

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

	public static Player createPresetPlayer() {
		Player player = new Player(getRandomCharacterName());
		player.getInventory().addItem(new Dummy());
		return player;
	}
}
