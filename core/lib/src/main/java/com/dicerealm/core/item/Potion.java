package com.dicerealm.core.item;

import com.dicerealm.core.dice.MultiDice;
import com.dicerealm.core.entity.Entity;

public class Potion extends UseableItem {
    private MultiDice effectDice;

    public Potion(String name, String description, int numDice, int diceSides) {
        super(name, description);
        this.effectDice = new MultiDice(numDice, diceSides);
				this.type = "POTION";
    }

    public MultiDice getDamageDice(){ return effectDice; }

    public int rollDamage(){ return effectDice.roll(); }

    @Override
    public boolean useOn(Entity target) {
        if (target.getHealth() <= 0) {
            return false; // Cannot use on a dead entity
        }
        return true;
    }
    
}
