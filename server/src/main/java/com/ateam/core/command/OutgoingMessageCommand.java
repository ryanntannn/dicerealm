package com.ateam.core.command;

import com.ateam.core.Message;

public class OutgoingMessageCommand extends Command {
	public String message;
	public String senderName;
	public String messageId;
	public OutgoingMessageCommand(Message message) {
		super.type = "OUTGOING_MESSAGE";
		this.message = message.getMessage();
		this.senderName = message.getSenderName();
		this.messageId = message.getMessageId().toString();
	}
}
