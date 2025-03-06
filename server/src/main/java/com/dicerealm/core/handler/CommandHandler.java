package com.dicerealm.core.handler;

import java.util.UUID;

import com.dicerealm.core.RoomContext;
import com.dicerealm.core.command.Command;

public abstract class CommandHandler<C extends Command> {
	private String type;
	
	/**
	 * Handle a command object
	 * @param command - Command object
	 */
	public abstract void handle(UUID playerId, C command, RoomContext context);

	public CommandHandler(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
