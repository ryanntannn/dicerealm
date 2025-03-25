package com.dicerealm.core.util.graph;

public class DistanceGraph <N extends Node, E extends DistanceEdge<N>> extends Graph<N , E> {
	public DistanceGraph() {
		super();
	}
	
	@Override
	public N[] shortestPath(N source, N target) {
		// TODO: Implement Dijkstra's algorithm instead of using the superclass implementation
		return super.shortestPath(source, target);
	}
}
