package com.example.dicerealmandroid.core.item;

import com.example.dicerealmandroid.core.entity.Entity;

import java.util.Map;

public class Helmet extends EquippableItem {


    public Helmet(String name, int armourClass) {
        super(name, "A helmet to protect your head", new Entity.BodyPart[] { Entity.BodyPart.HEAD }, new Entity.StatsMap(Map.of(Entity.Stat.ARMOUR_CLASS, armourClass)));
    }

}

