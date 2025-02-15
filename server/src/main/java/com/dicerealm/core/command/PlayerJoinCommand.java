package com.dicerealm.core.command;

import com.dicerealm.core.Player;

public class PlayerJoinCommand extends Command {
	public String playerId;
	public PlayerJoinCommand(Player player) {
		super.type = "PLAYER_JOIN";
		this.playerId = player.getId().toString();
	}
}
