package com.dicerealm.core.locations;

import java.util.ArrayList;
import java.util.List;

import com.dicerealm.core.entity.Entity;
import com.dicerealm.core.util.graph.Node;

public class Location extends Node {
	private String displayName;
	private String description;

	private List<Entity> entities = new ArrayList<Entity>();

	public Location(String displayName, String description) {
		this.displayName = displayName;
		this.description = description;
	}

	public Location(String displayName, String description, List<Entity> entities) {
		this(displayName, description);
		this.entities = entities;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getDescription() {
		return description;
	}

	public List<Entity> getEntities() {
		return entities;
	}

	public String getSummary() {
		StringBuilder summary = new StringBuilder();
		summary.append("id: ").append(getId().toString()).append("\n");
		summary.append("Location: ").append(displayName).append("\n");
		summary.append("Description: ").append(description).append("\n");
		summary.append("Entities: ");
		if (entities.isEmpty()) {
			summary.append("None");
		} else {
			for (Entity entity : entities) {
				summary.append(entity.getSummary()).append(", ");
			}
			summary.setLength(summary.length() - 2); // Remove the last comma and space
		}
		return summary.toString();
	}
}
