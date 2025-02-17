package com.dicerealm.core;

import com.dicerealm.core.entity.Entity;

/**
 * Represents a player in the game
 * @see Entity
 */
public class Player extends Entity {
	public Player() {
		super("Player", 5);
	}

	public Player(String name) {
		super(name, 5);
	}
}
