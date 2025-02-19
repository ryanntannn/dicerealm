package com.dicerealm.core;

import java.util.Map;

import com.dicerealm.core.entity.Entity;
import com.dicerealm.core.entity.Stat;
import com.dicerealm.core.entity.StatsMap;

/**
 * Represents a Monster in the game
 * @see Entity
 */
public class Monster extends Entity {
	public Monster() {
		super("Monster", new StatsMap(Map.of(
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

	public Monster(String name, String race, String entityClass StatsMap baseStats) {
		super(name, race, entityClass, baseStats);
	}
}
