package com.dicerealm.core.util.graph;

import java.util.UUID;

public class Edge<N extends Node> {
	private UUID id;
	private N source;
	private N target;

	public Edge(N source, N target) {
		this.id = UUID.randomUUID();
		this.source = source;
		this.target = target;
	}

	public UUID getId() {
		return id;
	}

	public N getSource() {
		return source;
	}

	public N getTarget() {
		return target;
	}
}
