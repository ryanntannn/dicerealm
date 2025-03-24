package com.example.dicerealmandroid.core.item;

import com.example.dicerealmandroid.core.entity.Entity;

public class EquippableItem extends Item {

    private Entity.BodyPart[] suitableBodyParts;
    private Entity.StatsMap stats;

    public EquippableItem(String name, String description, Entity.BodyPart[] suitableBodyParts, Entity.StatsMap stats) {
        super(name, description);
        this.suitableBodyParts = suitableBodyParts;
        this.stats = stats;
    }

    public boolean isSuitableFor(Entity.BodyPart bodyPart) {
        for (Entity.BodyPart suitableBodyPart : suitableBodyParts) {
            if (suitableBodyPart == bodyPart) {
                return true;
            }
        }
        return false;
    }

    public int getStat(Entity.Stat stat) {
        return stats.getOrDefault(stat, 0);
    }


    public Entity.BodyPart[] getSuitableBodyParts() {
        return suitableBodyParts;
    }
}