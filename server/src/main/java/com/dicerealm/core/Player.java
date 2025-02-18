package com.dicerealm.core;

import java.util.Map;

import com.dicerealm.core.entity.Entity;
import com.dicerealm.core.entity.Stat;
import com.dicerealm.core.entity.StatsMap;

/**
 * Represents a player in the game
 * @see Entity
 */
public class Player extends Entity {
	public Player() {
		super("Player", new StatsMap(Map.of(
			Stat.MAX_HEALTH, 20,
			Stat.ARMOUR_CLASS, 0,
			Stat.STRENGTH, 0,
			Stat.DEXTERITY, 0,
			Stat.CONSTITUTION, 0,
			Stat.INTELLIGENCE, 0,
			Stat.WISDOM, 0,
			Stat.CHARISMA, 0
		)));
	}

	public Player(String name, StatsMap baseStats) {
		super(name, baseStats);
	}
}
