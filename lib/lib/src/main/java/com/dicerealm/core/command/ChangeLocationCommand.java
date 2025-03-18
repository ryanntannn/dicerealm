package com.dicerealm.core.command;

import com.dicerealm.core.locations.Location;

public class ChangeLocationCommand extends Command {
	private Location location;

	public ChangeLocationCommand(Location newLocation) {
		super.type = "CHANGE_LOCATION";
		this.location = newLocation;
	}

	public Location getLocation() {
		return location;
	}
}
