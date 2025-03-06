package com.dicerealm.core.command;

import com.dicerealm.core.strategy.JsonSerializationStrategy;
import com.google.gson.JsonSyntaxException;

public class CommandDeserializer {
	private JsonSerializationStrategy jsonSerializationStrategy;
	private CommandMap commandMap = new CommandMap();

	public CommandDeserializer(JsonSerializationStrategy jsonSerializationStrategy) {
		this.jsonSerializationStrategy = jsonSerializationStrategy;
	}

	/**
	 * Deserialize a json command string into a Command object.
	 */
	public Command deserialize(String json) throws JsonSyntaxException, IllegalArgumentException {
		Command command = jsonSerializationStrategy.deserialize(json, Command.class);
		Class<? extends Command> commandClass = commandMap.get(command.type);
		if (commandClass == null) {
			throw new IllegalArgumentException("Command type not recognized: " + command.type);
		}
		return jsonSerializationStrategy.deserialize(json, commandClass);
	}
}
