package com.example.dicerealmandroid.game.combat;

public class CombatSequence {
    private String name;
    private int initiative;
    public CombatSequence(String name, int initiative) {
        this.name = name;
        this.initiative = initiative;
    }
    public String getName() {
        return name;
    }
    public int getInitiative() {
        return initiative;
    }
}
