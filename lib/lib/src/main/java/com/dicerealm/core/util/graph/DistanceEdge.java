package com.dicerealm.core.util.graph;

public class DistanceEdge<N extends Node> extends Edge<N> {
	private double distance;

	public DistanceEdge(N source, N target, double distance) {
		super(source, target);
		this.distance = distance;
	}

	public double getDistance() {
		return distance;
	}
}
