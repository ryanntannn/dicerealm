package com.dicerealm.core.command;

import com.dicerealm.core.message.Message;

public class MessageHistoryCommand extends Command {
	public OutgoingMessageCommand[] messages;
	public MessageHistoryCommand(Message[] messages) {
		super.type = "MESSAGE_HISTORY";
		this.messages = new OutgoingMessageCommand[messages.length];
		for (int i = 0; i < messages.length; i++) {
			this.messages[i] = new OutgoingMessageCommand(messages[i]);
		}
	}
}
