package com.dicerealm.core.item;

import com.dicerealm.core.inventory.Identifiable;

import java.util.UUID;

/**
 * Base class for all items
 */
public abstract class Item implements Identifiable {
	protected String type = "ITEM";
	private UUID id;
	private String displayName;
	private String description;

	public Item(String name, String description) {
		this.id = UUID.randomUUID();
		this.displayName = name;
		this.description = description;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getDescription() {
		return description;
	}

	public UUID getId() {
		return id;
	}

	@Override
	public String getType() {
		return type;
	}
}
