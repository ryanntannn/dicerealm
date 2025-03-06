package com.dicerealm.core.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.dicerealm.core.command.Command;
import com.dicerealm.core.room.RoomContext;
import com.dicerealm.core.strategy.JsonSerializationStrategy;

public class CommandRouter {
	private Map<String, CommandHandler<?>> handlers = new HashMap<>();
	private JsonSerializationStrategy strategy;

	public CommandRouter(JsonSerializationStrategy strategy) {
		this.strategy = strategy;
	}

	/**
	 * Route a json command string to the appropriate command handler
	 * @param json - Json command string
	 */
	public void handlePlayerCommand(UUID playerId, String json, RoomContext context) {
		try {
			// Deserialize the command
			Command command = strategy.deserialize(json, Command.class);

			String commandType = command.type;

			// Check if the command type is recognized
			if (!handlers.containsKey(commandType)) {
				throw new IllegalArgumentException("Command type not recognized: " + commandType);
			}

			// Get the handler
			@SuppressWarnings("unchecked")
			CommandHandler<Command> handler = (CommandHandler<Command>) handlers.get(command.type);

			// Use reflection to find the command class from the handler
			Class<? extends Command> commandClass = handler.getCommandClass();

			// Deserialize the command into the appropriate command class
			command = strategy.deserialize(json, commandClass);

			// Handle the command
			handler.handle(playerId, command, context);
		} catch (Exception e) {
			throw new IllegalArgumentException("Failed to handle command: " + json, e);
		}
	}


	/**
	 * Register a command handler
	 * @param type - Command type
	 * @param handler - Command handler
	 */
	public CommandRouter registerHandler(CommandHandler<?> handler) {
		handlers.put(handler.getType(), handler);
		return this;
	}
}
