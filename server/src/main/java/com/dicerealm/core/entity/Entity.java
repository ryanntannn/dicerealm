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
public abstract class Entity {
	private UUID id;
	private String displayName;
	private int health;
	private int maxHealth;
	
	private Map<BodyPart, WearableItem> equippedItems = new HashMap<BodyPart, WearableItem>();

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
		if (!item.isSuitableFor(bodyPart)) {
			return false;
		}
		equippedItems.put(bodyPart, item);
		return true;
	}
}
