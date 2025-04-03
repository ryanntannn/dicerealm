package com.dicerealm.core.item;

import com.dicerealm.core.dice.MultiDice;
import com.dicerealm.core.entity.Entity;

public class Scroll extends UseableItem {
    private MultiDice damageDice;

    public Scroll(String name, String description, int numDice, int diceSides) {
        super(name, description);
        this.damageDice = new MultiDice(numDice, diceSides);
				this.type = "SCROLL";
    }

    public MultiDice getDamageDice(){ return damageDice; }

    public int rollDamage(){ return damageDice.roll(); }
    
    @Override
    public boolean useOn(Entity target) {
        if (target.getHealth() <= 0) {
            return false; // Cannot use on a dead entity
        }
        return true;
    }
}