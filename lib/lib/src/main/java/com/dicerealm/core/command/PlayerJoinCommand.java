package com.dicerealm.core.command;

import com.dicerealm.core.player.Player;

public class PlayerJoinCommand extends Command {
	public Player player;
	public PlayerJoinCommand(Player player) {
		super.type = "PLAYER_JOIN";
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}
}
