package com.dicerealm.core.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.dicerealm.core.inventory.Inventory;
import com.dicerealm.core.item.EquippableItem;

/**
 * Base class for all entities in the game. Entities can be players, monsters, NPCs, etc.
 * 
 * @see EquippableItem - represents an item that can be worn by an entity
 * @see Inventory - represents the inventory of an entity
 */
public abstract class Entity {
	private UUID id;
	private String displayName;
	private Race race;
	private EntityClass entityClass;
	private int health;
	private Map<BodyPart, EquippableItem> equippedItems = new HashMap<BodyPart, EquippableItem>();
	private StatsMap baseStats = new StatsMap();
	private StatsMap stats = new StatsMap();

	private Inventory inventory = new Inventory();

	public Entity(String displayName, Race race, EntityClass entityClass, StatsMap baseStats) {
		this.id = UUID.randomUUID();
		this.displayName = displayName;
		this.race = race;
		this.entityClass = entityClass;
		this.baseStats = baseStats;
		this.health = baseStats.get(Stat.MAX_HEALTH);
		updateStats();
	}

	public UUID getId() {
		return id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public Race getRace() {
		return race;
	}

	public EntityClass getEntityClass() {
		return entityClass;
	}

	public int getHealth() {
		return health;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public boolean isAlive() {
		return health > 0;
	}

	public boolean equipItem(BodyPart bodyPart, EquippableItem item) {
		// Check if item is in inventory
		if (!inventory.containsItem(item)) {
			return false;
		}

		if (!item.isSuitableFor(bodyPart)) {
			return false;
		}

		inventory.removeItem(item);

		// check if the body part is already equipped
		if (equippedItems.containsKey(bodyPart)) {
			// un-equip the item
			inventory.addItem(equippedItems.get(bodyPart));
		}

		equippedItems.put(bodyPart, item);
		updateStats();

		return true;
	}

	public void updateStats() {
		stats.clear();
		for (Stat stat : baseStats.keySet()) {
			stats.put(stat, getStat(stat));
		}
	}

	public int getStat(Stat stat) {
		int out = baseStats.get(stat);
		for (EquippableItem item : equippedItems.values()) {
			out += item.getStat(stat);
		}
		return out;
	}

	public void displayStats(){
		System.out.println("Name: " + getDisplayName());
		System.out.println("Race: " + getRace());
		System.out.println("Class: " + getEntityClass());
		System.out.println("Entity Stats:");
		for (Stat stat : getStats().keySet()) {
			System.out.println(stat + ": " + getStats().get(stat));
		}
	}

	public StatsMap getStats() {
		return stats;
	}
}
