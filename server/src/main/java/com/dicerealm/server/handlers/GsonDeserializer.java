package com.dicerealm.server.handlers;

import com.dicerealm.core.command.Command;
import com.dicerealm.core.command.CommandDeserializerStrategy;
import com.dicerealm.core.command.CommandMap;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class GsonDeserializer extends CommandMap implements CommandDeserializerStrategy {

	private Gson gson = new Gson();

	@Override
	public Command deserialize(String json) throws JsonSyntaxException, IllegalArgumentException {
		Command command = gson.fromJson(json, Command.class);
		Class<? extends Command> commandClass = commandMap.get(command.type);
		if (commandClass == null) {
			throw new IllegalArgumentException("Command type not recognized: " + command.type);
		}
		return gson.fromJson(json, commandClass);
	}
}
