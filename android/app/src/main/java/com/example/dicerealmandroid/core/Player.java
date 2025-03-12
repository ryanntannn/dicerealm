package com.example.dicerealmandroid.core;

/**
 * Represents a player in the game
 * @see Entity
 */
public class Player extends Entity {

    public Player(String name, Entity.Race race, Entity.EntityClass entityClass, Entity.StatsMap baseStats) {
        super(name, race, entityClass, baseStats);
    }
}