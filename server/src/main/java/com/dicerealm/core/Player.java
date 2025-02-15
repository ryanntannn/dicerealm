package com.dicerealm.core;

import java.util.UUID;

public class Player {
	private UUID id;
	private String displayName;

	public Player() {
		this.id = UUID.randomUUID();
		this.displayName = "Player " + id.toString().substring(0, 8);
	}

	public UUID getId() {
		return id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
}
