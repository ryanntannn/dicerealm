package com.dicerealm.core.command;

public class ShowPlayerActionsCommand extends Command {
	public String[] actions;
	public ShowPlayerActionsCommand(String[] actions) {
		super.type = "SHOW_PLAYER_ACTIONS";
		this.actions = actions;
	}
}
