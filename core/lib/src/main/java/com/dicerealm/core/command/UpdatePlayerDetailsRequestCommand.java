package com.dicerealm.core.command;

import com.dicerealm.core.entity.EntityClass;
import com.dicerealm.core.entity.Race;
import com.dicerealm.core.entity.StatsMap;

public class UpdatePlayerDetailsRequestCommand extends Command {
	public String displayName;
	public Race race;
	public EntityClass entityClass;
	public StatsMap baseStats;
	public UpdatePlayerDetailsRequestCommand() {
		this.type = "UPDATE_PLAYER_DETAILS_REQUEST";
	}
	public UpdatePlayerDetailsRequestCommand(String displayName, Race race, EntityClass entityClass, StatsMap baseStats) {
		this.type = "UPDATE_PLAYER_DETAILS_REQUEST";
		this.displayName = displayName;
		this.entityClass = entityClass;
		this.baseStats = baseStats;
	}
}
