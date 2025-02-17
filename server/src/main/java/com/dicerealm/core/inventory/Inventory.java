package com.dicerealm.core.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.dicerealm.core.item.Item;

public class Inventory {
	private int inventorySize;
	private List<Item> items = new ArrayList<Item>();

	public Inventory() {
		this.inventorySize = 5;
	}

	public Inventory(int inventorySize) {
		this.inventorySize = inventorySize;
	}

	public boolean addItem(Item item) {
		if (items.size() < inventorySize) {
			items.add(item);
			return true;
		}
		return false;
	}

	public boolean removeItem(Item item) {
		if (items.contains(item)) {
			items.remove(item);
			return true;
		}
		return false;
	}

	public boolean containsItem(Item item) {
		return items.contains(item);
	}

	public String toString() {
		return items.toString();
	}

	public Item getItem(UUID itemId) {
		for (Item item : items) {
			if (item.getId().equals(itemId)) {
				return item;
			}
		}
		return null;
	}
}
