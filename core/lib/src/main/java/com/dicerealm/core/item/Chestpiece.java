package com.dicerealm.core.item;

import java.util.Map;

import com.dicerealm.core.entity.BodyPart;
import com.dicerealm.core.entity.Stat;
import com.dicerealm.core.entity.StatsMap;

public class Chestpiece extends EquippableItem {


	public Chestpiece(String name, int armourClass) {
		super(name, "Armour for the body", new BodyPart[] { BodyPart.TORSO }, new StatsMap(Map.of(Stat.ARMOUR_CLASS, armourClass)));
	}
	
}

