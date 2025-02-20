package com.dicerealm.core.inventory;

public class InventoryOf<T extends Identifiable> extends Inventory<T> {
    public InventoryOf() {
        super();
    }
    public InventoryOf(int inventorySize) {
        super(inventorySize);
    }
}
