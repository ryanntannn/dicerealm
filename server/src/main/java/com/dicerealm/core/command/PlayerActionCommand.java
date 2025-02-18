package com.dicerealm.core.command;

import com.dicerealm.core.entity.StatsMap;

public class PlayerActionCommand extends Command {
	public String action;
	public StatsMap skillCheck;
	public PlayerActionCommand(String action, StatsMap skillCheck) {
		super.type = "PLAYER_ACTION";
		this.action = action;
		this.skillCheck = skillCheck;
	}
}
