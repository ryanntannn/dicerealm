package com.dicerealm.core.player;

import java.util.Map;

import com.dicerealm.core.entity.Entity;
import com.dicerealm.core.entity.EntityClass;
import com.dicerealm.core.entity.Race;
import com.dicerealm.core.entity.Stat;
import com.dicerealm.core.entity.StatsMap;
/**
 * Represents a player in the game
 * @see Entity
 */
public class Player extends Entity {
	public Player() {
		super("Player", Race.HUMAN, EntityClass.WARRIOR, new StatsMap(Map.of(
			Stat.MAX_HEALTH, 20,
			Stat.ARMOUR_CLASS, 0,
			Stat.STRENGTH, 0,
			Stat.DEXTERITY, 0,
			Stat.CONSTITUTION, 0,
			Stat.INTELLIGENCE, 0,
			Stat.WISDOM, 0,
			Stat.CHARISMA, 0
		)));
		this.allegiance = Allegiance.PLAYER;
	}

	public Player(String name, Race race, EntityClass entityClass, StatsMap baseStats) {
		super(name, race, entityClass, baseStats);
		this.allegiance = Allegiance.PLAYER;

	}
}
