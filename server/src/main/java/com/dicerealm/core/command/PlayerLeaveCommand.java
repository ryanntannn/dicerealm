package com.dicerealm.core.command;

import com.dicerealm.core.player.Player;

public class PlayerLeaveCommand extends Command {
	public String playerId;
	public PlayerLeaveCommand(Player player) {
		super.type = "PLAYER_LEAVE";
		this.playerId = player.getId().toString();
	}
}
