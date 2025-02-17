package com.dicerealm.core.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.dicerealm.core.inventory.Inventory;
import com.dicerealm.core.item.WearableItem;

/**
 * Base class for all entities in the game. Entities can be players, monsters, NPCs, etc.
 * 
 * @see WearableItem - represents an item that can be worn by an entity
 * @see Inventory - represents the inventory of an entity
 */
public abstract class Entity implements Stats {
	private UUID id;
	private String displayName;
	private int health;
	private int maxHealth;
	
	private Map<BodyPart, WearableItem> equippedItems = new HashMap<BodyPart, WearableItem>();
	private Map<Stat, Integer> baseStats = new HashMap<Stat, Integer>();

	private Inventory	inventory = new Inventory();

	public Entity(String displayName, int maxHealth) {
		this.id = UUID.randomUUID();
		this.displayName = displayName;
		this.maxHealth = maxHealth;
		this.health = maxHealth;
	}

	public UUID getId() {
		return id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public int getHealth() {
		return health;
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public boolean isAlive() {
		return health > 0;
	}

	public boolean equipItem(BodyPart bodyPart, WearableItem item) {
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
			// unequip the item
			inventory.addItem(equippedItems.get(bodyPart));
		}

		equippedItems.put(bodyPart, item);

		System.out.println(equippedItems.get(bodyPart));

		return true;
	}

	public int getStat(Stat stat) {
		int out = baseStats.get(stat);
		for (WearableItem item : equippedItems.values()) {
			out += item.getStat(stat);
		}
		return out;
	}
}
