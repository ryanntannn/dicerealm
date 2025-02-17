package com.dicerealm.core.item;

import java.util.Map;

import com.dicerealm.core.entity.BodyPart;

public class Helmet extends WearableItem {


	public Helmet(String name, int armourClass) {
		super(name, "A helmet to protect your head", new BodyPart[] { BodyPart.HEAD }, Map.of(Stat.ARMOUR_CLASS, armourClass));
	}
	
}
