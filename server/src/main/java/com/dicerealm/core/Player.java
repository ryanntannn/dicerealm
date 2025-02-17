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
			Stat.ARMOUR_CLASS, 5,
			Stat.STRENGTH, 10,
			Stat.DEXTERITY, 10,
			Stat.CONSTITUTION, 10,
			Stat.INTELLIGENCE, 10,
			Stat.WISDOM, 10,
			Stat.CHARISMA, 10
		)));
	}

	public Player(String name, StatsMap baseStats) {
		super(name, baseStats);
	}
}
