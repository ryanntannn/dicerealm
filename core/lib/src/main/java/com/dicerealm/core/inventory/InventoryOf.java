package com.dicerealm.core.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InventoryOf<T extends Identifiable> {
	/**
	 * This is used for deserialization
	 */
	private String type;
	private int inventorySize;
	private List<T> items = new ArrayList<T>();

	public InventoryOf(String type) {
		this.inventorySize = 5;
		this.type = type;
	}

	public InventoryOf( String type, int inventorySize) {
		this.inventorySize = inventorySize;
		this.type = type;
	}

	public int getInventorySize(int inventorySize){
		return inventorySize;
	}
	public void setInventorySize(int inventorySize){
		this.inventorySize = inventorySize;
	}

	public boolean addItem(T item) {
		if (items.size() < inventorySize) {
			items.add(item);
			return true;
		}
		return false;
	}

	public boolean removeItem(T item) {
		if (items.contains(item)){
			items.remove(item);
			return true;
		}
		return false;
	}

	public boolean containsItem(T item) {
		return items.contains(item);
	}

	@Override
	public String toString() {
		return items.toString();
	}

	public T getItem(UUID itemId) {
		for (T item : items) {
			if (item.getId().equals(itemId)) {
				return item;
			}
		}
		return null;
	}

	public String getType() {
		return type;
	}

	public List<T> getItems() {
		return items;
	}
}
