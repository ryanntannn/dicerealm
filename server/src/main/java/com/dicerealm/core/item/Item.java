package com.dicerealm.core.item;

import java.util.UUID;

/**
 * Base class for all items
 */
public abstract class Item {
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
}
