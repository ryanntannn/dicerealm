package com.dicerealm.core.item;

import java.util.Map;

import com.dicerealm.core.entity.BodyPart;
import com.dicerealm.core.entity.Stat;
import com.dicerealm.core.entity.StatsMap;

public class Necklace extends EquippableItem {


	public Necklace(String name, Stat stat, int statValue) {
		super(name, "Necklace of Stats", new BodyPart[] { BodyPart.NECK }, new StatsMap(Map.of(stat, statValue)));
	}
	
}

