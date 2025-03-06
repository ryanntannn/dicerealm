package com.dicerealm.core.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.dicerealm.core.RoomContext;
import com.dicerealm.core.command.Command;
import com.dicerealm.core.command.CommandDeserializer;
import com.dicerealm.core.strategy.JsonSerializationStrategy;

public class CommandRouter {
	private Map<String, CommandHandler<?>> handlers = new HashMap<>();
	private CommandDeserializer deserializer;

	public CommandRouter(JsonSerializationStrategy strategy) {
		this.deserializer = new CommandDeserializer(strategy);
	}

	/**
	 * Route a json command string to the appropriate command handler
	 * @param json - Json command string
	 */
	public void handlePlayerCommand(UUID playerId, String json, RoomContext context) {
		try {
			// Deserialize the command
			Command command = deserializer.deserialize(json);

			// Get the handler
			@SuppressWarnings("unchecked")
			CommandHandler<Command> handler = (CommandHandler<Command>) handlers.get(command.type);

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
