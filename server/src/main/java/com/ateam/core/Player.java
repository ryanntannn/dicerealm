package com.ateam.core;

import java.util.UUID;

public class Player {
	private UUID id;

	public Player() {
		this.id = UUID.randomUUID();
	}

	public UUID getId() {
		return id;
	}
}
