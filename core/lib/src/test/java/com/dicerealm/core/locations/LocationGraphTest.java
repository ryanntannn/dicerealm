package com.dicerealm.core.locations;

import org.junit.jupiter.api.Test;

public class LocationGraphTest {
	@Test
	public void testAddLocation() {
		Location startLocation = new Location("start", "a starting location");
		Location location = new Location("location", "a location");
		LocationGraph graph = new LocationGraph(startLocation);
		graph.addN(startLocation);
		graph.addN(location);
	}

	@Test
	public void testAddPath() {
		Location startLocation = new Location("start", "a starting location");
		Location location = new Location("location", "a location");
		LocationGraph graph = new LocationGraph(startLocation);
		graph.addN(startLocation);
		graph.addN(location);
		graph.addE(new Path(startLocation, location, 1));
	}

	@Test
	public void testGetAdjacentLocations() {
		Location startLocation = new Location("start", "a starting location");
		Location location = new Location("location", "a location");
		LocationGraph graph = new LocationGraph(startLocation);
		graph.addN(startLocation);
		graph.addN(location);
		graph.addE(new Path(startLocation, location, 1));
		graph.getAdjacentLocations();
		assert(graph.getAdjacentLocations().length == 1);
		assert(graph.getAdjacentLocations()[0].equals(location));
	}

	@Test
	public void testSetCurrentLocation() {
		Location startLocation = new Location("start", "a starting location");
		Location location = new Location("location", "a location");
		LocationGraph graph = new LocationGraph(startLocation);
		graph.addN(startLocation);
		graph.addN(location);
		graph.addE(new Path(startLocation, location, 1));
		graph.setCurrentLocation(location);
		assert(graph.getCurrentLocation().equals(location));
	}
}
