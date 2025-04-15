package com.dicerealm.core.command;

public class UpdateThemeCommand extends Command {
	private String theme;

	public UpdateThemeCommand(String theme) {
		this.theme = theme;
		this.type = "UPDATE_THEME";
	}

	public String getTheme() {
		return theme;
	}
}
