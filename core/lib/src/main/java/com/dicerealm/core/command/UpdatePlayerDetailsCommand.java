package com.dicerealm.core.command;

import com.dicerealm.core.player.Player;

public class UpdatePlayerDetailsCommand extends Command {
	public Player player;
	public UpdatePlayerDetailsCommand(Player player) {
		this.type = "UPDATE_PLAYER_DETAILS";
		this.player = player;
	}
}
