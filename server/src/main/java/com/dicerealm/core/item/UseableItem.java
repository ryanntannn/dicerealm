package com.dicerealm.core.item;

import com.dicerealm.core.entity.Entity;

public abstract class UseableItem extends Item {
	public UseableItem(String name, String description) {
		super(name, description);
	}
	
	/**
	 * Use the item on a target entity. Returns true if the item was used successfully.
	 * @param target
	 * @return boolean - true if the item was used successfully on the target entity.
	 */
	public abstract boolean useOn(Entity target);
}
