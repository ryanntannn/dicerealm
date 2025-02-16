package com.dicerealm.core.item;

import java.util.UUID;

public abstract class Item {
	@SuppressWarnings("unused")
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
}
