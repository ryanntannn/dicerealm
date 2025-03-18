package com.dicerealm.core.util.graph;

import java.util.UUID;

public class Node {
	private UUID id;
	public Node() {
		this.id = UUID.randomUUID();
	}
	public UUID getId() {
		return id;
	}
}
