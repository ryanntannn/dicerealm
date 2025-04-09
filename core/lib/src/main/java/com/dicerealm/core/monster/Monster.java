package com.dicerealm.core.monster;

import java.util.Map;

import com.dicerealm.core.entity.Entity;
import com.dicerealm.core.entity.EntityClass;
import com.dicerealm.core.entity.Race;
import com.dicerealm.core.entity.Stat;
import com.dicerealm.core.entity.StatsMap;


/**
 * Represents a Monster in the game
 * @see Entity
 */
public class Monster extends Entity {
	private int monsterLevel;

	public Monster() {
		super("Monster", Race.DEMON, EntityClass.WIZARD, new StatsMap(Map.of(
			Stat.MAX_HEALTH, 20,
			Stat.ARMOUR_CLASS, 0,
			Stat.STRENGTH, 0,
			Stat.DEXTERITY, 0,
			Stat.CONSTITUTION, 0,
			Stat.INTELLIGENCE, 0,
			Stat.WISDOM, 0,
			Stat.CHARISMA, 0
		)));
		this.allegiance = Allegiance.ENEMY;
		this.monsterLevel = 1;
	}

	public Monster(String name, Race race, EntityClass entityClass, StatsMap baseStats, int monsterLevel) {
		super(name, race, entityClass, baseStats);
		this.monsterLevel = monsterLevel;
		this.allegiance = Allegiance.ENEMY;
	}

	public Monster(String name, Race race, EntityClass entityClass, StatsMap baseStats) {
		super(name, race, entityClass, baseStats);
		this.monsterLevel = 1;
		this.allegiance = Allegiance.ENEMY;
	}

	public int getMonsterLevel() {
		return monsterLevel;
	}
	// Temp No Xp Gain
	public int getXpValue() {
        return monsterLevel * 0;
    }
}
