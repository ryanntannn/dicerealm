package com.dicerealm.core.command;

import java.util.HashMap;
import java.util.Map;

public class CommandMap {
	protected Map<String, Class<? extends Command>> commandMap = new HashMap<String, Class<? extends Command>>();

	/**
	 * Register a command type with a command class
	 * @param type - Unique identifier for the command
	 * @param command - Command object
	 * @throws IllegalArgumentException
	 */
	public void registerCommand(String type, Class<? extends Command> command) throws IllegalArgumentException {
		// check if type is already registered
		if (commandMap.containsKey(type)) {
			throw new IllegalArgumentException("Command type already registered: " + type);
		}
		commandMap.put(type, command);
	}

	public CommandMap() {
		// Register all incoming command types here
		registerCommand("MESSAGE", MessageCommand.class);
		registerCommand("PLAYER_EQUIP_ITEM_REQUEST", PlayerEquipItemRequest.class);
	}
}
