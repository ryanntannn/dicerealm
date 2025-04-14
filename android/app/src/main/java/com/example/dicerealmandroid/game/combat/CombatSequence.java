package com.example.dicerealmandroid.game.combat;

import java.util.UUID;

public class CombatSequence {
    private String name;
    private int initiative;

    private int health;

    private UUID uuid;
    public CombatSequence(String name, int initiative , int health, UUID uuid) {
        this.name = name;
        this.initiative = initiative;
        this.health = health;
        this.uuid = uuid;
    }
    public String getName() {
        return name;
    }
    public int getInitiative() {
        return initiative;
    }

    public int getHealth() {return health; }

    public UUID getuuid(){return uuid; }

}
