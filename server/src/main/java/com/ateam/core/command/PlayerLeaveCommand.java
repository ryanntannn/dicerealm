package com.ateam.core.command;

import com.ateam.core.Player;

public class PlayerLeaveCommand extends Command {
	public String playerId;
	public PlayerLeaveCommand(Player player) {
		super.type = "PLAYER_LEAVE";
		this.playerId = player.getId().toString();
	}
}
