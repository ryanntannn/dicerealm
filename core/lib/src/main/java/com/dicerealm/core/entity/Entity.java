package com.dicerealm.core.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.dicerealm.core.inventory.InventoryOf;
import com.dicerealm.core.item.EquippableItem;
import com.dicerealm.core.item.Item;
import com.dicerealm.core.item.Weapon;
import com.dicerealm.core.skills.Skill;

/**
 * Base class for all entities in the game. Entities can be players, monsters, NPCs, etc.
 * 
 * @see EquippableItem - represents an item that can be worn by an entity
 * @see InventoryOf - represents the inventory of an entity
 */
public class Entity {

	public static enum Allegiance {
		PLAYER, FRIENDLY, ENEMY
	}

	private UUID id;
	private String displayName;
	private Race race;
	private EntityClass entityClass;
	private int health;
	private Map<BodyPart, EquippableItem> equippedItems = new HashMap<BodyPart, EquippableItem>();
	private StatsMap baseStats;
	private StatsMap stats;
	protected Allegiance allegiance = Allegiance.FRIENDLY;

	private InventoryOf<Item> inventory = new InventoryOf<Item>("ITEM");

	private InventoryOf<Skill> skillsInventory = new InventoryOf<Skill>("SKILL",4);

	public Entity(String displayName, Race race, EntityClass entityClass, StatsMap baseStats) {
		this.id = UUID.randomUUID();
		this.displayName = displayName;
		this.race = race;
		this.entityClass = entityClass;

		//Get base stats from ClassStats class
		this.baseStats = ClassStats.getStatsForClass(entityClass);
		this.health = baseStats.get(Stat.MAX_HEALTH);

		// Initialize the stats map by copying baseStats initially
		this.stats = new StatsMap(baseStats);
		updateStats();
		updateMaxHealth();
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

	public InventoryOf<Item> getInventory() {
		return inventory;
	}

	public InventoryOf<Skill> getSkillsInventory(){
		return skillsInventory;
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

	public void updateMaxHealth() {
		this.health = stats.get(Stat.MAX_HEALTH);
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

	public void setId(UUID id) {
		this.id = id;
	}

	public void takeDamage(int damage) {
		this.health = Math.max(0, this.health - damage); // HP can't go below 0
	}

	public Weapon getWeapon() {
		EquippableItem rightHandWeapon = equippedItems.get(BodyPart.RIGHT_HAND);
		EquippableItem leftHandWeapon = equippedItems.get(BodyPart.LEFT_HAND);

		if (rightHandWeapon != null) {
			return (Weapon) rightHandWeapon;
		} else if (leftHandWeapon != null) {
			return (Weapon) leftHandWeapon;
		}

		return null; // No weapon equipped
	}

	public void addSkill(Skill skill) {
		skillsInventory.addItem(skill);
	}

	public Map<BodyPart, EquippableItem> getEquippedItems() {
		return equippedItems;
	}

	public Allegiance getAllegiance() {
		return this.allegiance;
	}

}

