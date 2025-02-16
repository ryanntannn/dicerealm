package com.dicerealm.server.handlers;

import java.util.Map;
import java.util.UUID;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.dicerealm.core.BroadcastStrategy;
import com.dicerealm.core.JsonSerializationStrategy;
import com.dicerealm.core.Player;
import com.dicerealm.core.command.Command;
public class WebsocketBroadcaster implements BroadcastStrategy {
	private Map<UUID, WebSocketSession> playerSessions;

	private JsonSerializationStrategy serializationStrategy;

	WebsocketBroadcaster(Map<UUID, WebSocketSession> playerSessions, JsonSerializationStrategy serializationStrategy) {
		this.playerSessions = playerSessions;
		this.serializationStrategy = serializationStrategy;
	}

	private void sendTextMessage(WebSocketSession session, TextMessage textMessage) {
		try {
			session.sendMessage(textMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendToAllPlayers(Command command) {
		// make a text message
		TextMessage	textMessage = new TextMessage(serializationStrategy.serialize(command));
		playerSessions.values().forEach(session -> {
			sendTextMessage(session, textMessage);
		});
	}

	@Override
	public void sendToPlayer(Command command, Player player) {
		WebSocketSession session = playerSessions.get(player.getId());
		TextMessage textMessage = new TextMessage(serializationStrategy.serialize(command));
		if (session != null) {
			sendTextMessage(session, textMessage);
		}
	}

	@Override
	public void sendToAllPlayersExcept(Command command, Player player) {
		WebSocketSession playerSession = playerSessions.get(player.getId());
		TextMessage textMessage = new TextMessage(serializationStrategy.serialize(command));
		playerSessions.values().forEach(session -> {
			if (session != playerSession) {
				sendTextMessage(session, textMessage);
			}
		});
	}
}
