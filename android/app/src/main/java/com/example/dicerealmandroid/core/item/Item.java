package com.example.dicerealmandroid.core.item;

import com.example.dicerealmandroid.core.Identifiable;

import java.util.UUID;

public class Item implements Identifiable {
    private UUID id;
    private String displayName;
    private String description;

    public Item(String name, String description) {
        this.id = UUID.randomUUID();
        this.displayName = name;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public UUID getId() {
        return id;
    }
}