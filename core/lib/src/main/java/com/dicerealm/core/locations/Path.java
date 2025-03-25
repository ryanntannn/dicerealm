package com.dicerealm.core.locations;

import com.dicerealm.core.util.graph.DistanceEdge;

public class Path extends DistanceEdge<Location> {
	public Path(Location start, Location end, int distance) {
		super(start, end, distance);
	}
}
