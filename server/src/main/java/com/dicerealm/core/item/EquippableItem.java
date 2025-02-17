package com.dicerealm.core.item;

import com.dicerealm.core.entity.BodyPart;
import com.dicerealm.core.entity.Stat;
import com.dicerealm.core.entity.StatsMap;

/**
 * Base class for all wearable items
 */
public class EquippableItem extends Item {

	private BodyPart[] suitableBodyParts;
	private StatsMap stats;

	public EquippableItem(String name, String description, BodyPart[] suitableBodyParts, StatsMap stats) {
		super(name, description);
		this.suitableBodyParts = suitableBodyParts;
		this.stats = stats;
	}

	public boolean isSuitableFor(BodyPart bodyPart) {
		for (BodyPart suitableBodyPart : suitableBodyParts) {
			if (suitableBodyPart == bodyPart) {
				return true;
			}
		}
		return false;
	}
	
	public int getStat(Stat stat) {
		return stats.getOrDefault(stat, 0);
	}
}
