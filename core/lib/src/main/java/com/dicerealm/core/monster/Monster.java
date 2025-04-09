package com.dicerealm.core.monster;

import java.util.Map;

import com.dicerealm.core.entity.BodyPart;
import com.dicerealm.core.entity.Entity;
import com.dicerealm.core.entity.EntityClass;
import com.dicerealm.core.entity.Race;
import com.dicerealm.core.entity.Stat;
import com.dicerealm.core.entity.StatsMap;
import com.dicerealm.core.item.Weapon;
import com.dicerealm.core.item.weapons.IronAxe;
import com.dicerealm.core.item.weapons.IronSword;
import com.dicerealm.core.skills.Fireball;

/**
 * Represents a Monster in the game
 * @see Entity
 */
public class Monster extends Entity {
	private int monsterLevel;

	// TODO: Remove this
	public void addDefaults(){
		addSkill(new Fireball());
		Weapon sword = new IronSword(1);
		Weapon axe = new IronAxe(1);
		getInventory().addItem(sword);
		getInventory().addItem(axe);
		equipItem(BodyPart.LEFT_HAND, axe);
		equipItem(BodyPart.RIGHT_HAND, sword);
	}

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
		addDefaults();
	}

	public Monster(String name, Race race, EntityClass entityClass, StatsMap baseStats, int monsterLevel) {
		super(name, race, entityClass, baseStats);
		this.monsterLevel = monsterLevel;
		this.allegiance = Allegiance.ENEMY;
		addDefaults();
	}

	public Monster(String name, Race race, EntityClass entityClass, StatsMap baseStats) {
		super(name, race, entityClass, baseStats);
		this.monsterLevel = 1;
		this.allegiance = Allegiance.ENEMY;
		addDefaults();
	}

	public int getMonsterLevel() {
		return monsterLevel;
	}
	
	public int getXpValue() {
        return monsterLevel * 10;
    }
}
