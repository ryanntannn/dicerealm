package com.dicerealm.core.item;

import java.util.Map;

import com.dicerealm.core.entity.BodyPart;
import com.dicerealm.core.entity.Stat;
import com.dicerealm.core.entity.StatsMap;

public class Helmet extends EquippableItem {


	public Helmet(String name, int armourClass) {
		super(name, "A helmet to protect your head", new BodyPart[] { BodyPart.HEAD }, new StatsMap(Map.of(Stat.ARMOUR_CLASS, armourClass)));
	}
	
}
