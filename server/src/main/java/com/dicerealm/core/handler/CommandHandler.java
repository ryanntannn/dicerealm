package com.dicerealm.core.handler;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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

	/**
	 * Use reflection to get the command class of this handler 
	 * @return Command class
	 */
	@SuppressWarnings("unchecked")
	public Class<C> getCommandClass() {
		try {
			Type superclass = getClass().getGenericSuperclass();
			Type parameterized = ((ParameterizedType) superclass).getActualTypeArguments()[0];
			return (Class<C>) parameterized;
		} catch (Exception e) {
			throw new IllegalArgumentException("Failed to get command class: " + type, e);
		}
	}
}
