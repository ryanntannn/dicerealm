package com.example.dicerealmandroid.core.item;

import com.example.dicerealmandroid.core.Identifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InventoryOf<T extends Identifiable> {
    private int inventorySize;
    private List<T> items = new ArrayList<T>();

    public InventoryOf() {
        this.inventorySize = 5;
    }

    public InventoryOf(int inventorySize) {
        this.inventorySize = inventorySize;
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
}
