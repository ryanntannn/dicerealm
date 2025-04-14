package com.example.dicerealmandroid.game.combat;

public class CombatSequence {
    private String name;
    private int initiative;

    private int health;
    public CombatSequence(String name, int initiative , int health) {
        this.name = name;
        this.initiative = initiative;
        this.health = health;
    }
    public String getName() {
        return name;
    }
    public int getInitiative() {
        return initiative;
    }

    public int getHealth() {return health; }
}
