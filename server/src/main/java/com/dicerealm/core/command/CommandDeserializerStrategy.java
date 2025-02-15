package com.dicerealm.core.command;

public interface CommandDeserializerStrategy {
	/**
	 * Deserialize a json command string into a Command object.
	 */
	public abstract Command deserialize(String command);
}
