package com.ateam.core.command;

import com.ateam.core.Player;

public class PlayerJoinCommand extends Command {
	public String playerId;
	public PlayerJoinCommand(Player player) {
		super.type = "PLAYER_JOIN";
		this.playerId = player.getId().toString();
	}
}
