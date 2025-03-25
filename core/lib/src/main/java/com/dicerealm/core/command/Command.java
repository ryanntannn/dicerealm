package com.dicerealm.core.command;


/**
 * Base class for all commands, each command should have a unique type
 * Commands are data objects that are sent between the client and the server, and are serialized/deserialized to JSON
 * 
 * @author Ryan
 */
public class Command {
		public String type;
}