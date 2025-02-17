package com.dicerealm.core.item;

import com.dicerealm.core.entity.BodyPart;

/**
 * Base class for all wearable items
 */
public class WearableItem extends Item {

	private BodyPart[] suitableBodyParts;

	public WearableItem(String name, String description, BodyPart[] suitableBodyParts) {
		super(name, description);
		this.suitableBodyParts = suitableBodyParts;
	}

	public boolean isSuitableFor(BodyPart bodyPart) {
		for (BodyPart suitableBodyPart : suitableBodyParts) {
			if (suitableBodyPart == bodyPart) {
				return true;
			}
		}
		return false;
	}
}
