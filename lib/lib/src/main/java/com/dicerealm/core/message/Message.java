package com.dicerealm.core.message;

import java.util.UUID;

public class Message {
		private String message;
		private UUID messageId;
		private String senderName;

		public Message(String message, String senderName) {
				this.message = message;
				this.messageId = UUID.randomUUID();
				this.senderName = senderName;
		}

		public String getMessage() {
				return message;
		}

		public void appendMessage(String message) {
				this.message += message;
		}

		public UUID getMessageId() {
				return messageId;
		}

		public String getSenderName() {
				return senderName;
		}

		public String toString() {
				return senderName + ": " + message;
		}
}
