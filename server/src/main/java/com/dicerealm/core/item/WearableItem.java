package com.dicerealm.core.item;

import java.util.Map;

import com.dicerealm.core.entity.BodyPart;
import com.dicerealm.core.entity.Stats;

/**
 * Base class for all wearable items
 */
public class WearableItem extends Item implements Stats {

	private BodyPart[] suitableBodyParts;
	private Map<Stat, Integer> stats;

	public WearableItem(String name, String description, BodyPart[] suitableBodyParts, Map<Stat, Integer> stats) {
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
		return stats.get(stat);
	}
}
