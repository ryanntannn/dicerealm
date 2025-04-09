package com.dicerealm.core.locations;

import com.dicerealm.core.util.graph.DistanceGraph;

/**
 * A graph representing the possible locations in the game world, and the connections between them.
 */
public class LocationGraph extends DistanceGraph<Location, Path>  {
	private Location currentLocation;

	public LocationGraph(Location startLocation) {
		super();
		this.currentLocation = startLocation;
	}

	/**
	 * Get the locations that are adjacent to the current location. These are the locations that can be traveled to from the current location.
	 * @return An array of locations that are adjacent to the current location.
	 * 
	 * @see Location
	 */
	public Location[] getAdjacentLocations() {
		return super.getNeighbors(this.currentLocation);
	}

	/**
	 * Set the current location of the party
	 * @param location
	 * 
	 * @see Location
	 */
	public void setCurrentLocation(Location location) {
		this.currentLocation = location;
	}

	/**
	 * Get the current location of the party
	 * @return The current location of the party
	 * 
	 * @see Location
	 */
	public Location getCurrentLocation() {
		return this.currentLocation;
	}

	public String getAdjacentLocationSummaries() {
		StringBuilder summaries = new StringBuilder();
		for (Location location : getAdjacentLocations()) {
			summaries.append(location.getSummary()).append("\n");
		}
		return summaries.toString();
	}
}
