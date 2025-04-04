package com.dicerealm.core.skills;

import com.dicerealm.core.combat.ActionType;
import com.dicerealm.core.entity.EntityClass;

public class Fireball extends Skill {
    public Fireball() {
        super("Fireball", "A massive ball of fire", EntityClass.WIZARD, ActionType.MAGIC, 3,2,15,3);
    }
}