package com.dicerealm.core.command;

import com.dicerealm.core.Player;

public class PlayerJoinCommand extends Command {
	public Player player;
	public PlayerJoinCommand(Player player) {
		super.type = "PLAYER_JOIN";
		this.player = player;
	}
}
