package com.dicerealm.core.command;

public class MessageCommand extends Command {
		public String message = "";
		public MessageCommand(String message) {
				super.type = "MESSAGE";
				this.message = message;
		}
}