package com.dicerealm.core.command;

import com.dicerealm.core.dm.DungeonMasterResponse;

public class ShowPlayerActionsCommand extends Command {
	private DungeonMasterResponse.PlayerAction[] actions;
	public ShowPlayerActionsCommand(DungeonMasterResponse.PlayerAction[] actions) {
		super.type = "SHOW_PLAYER_ACTIONS";
		this.actions = actions;
	}

	public DungeonMasterResponse.PlayerAction[] getActions() {
		return actions;
	}
}
