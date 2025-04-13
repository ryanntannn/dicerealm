package com.dicerealm.server.strategy;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.dicerealm.core.command.Command;
import com.dicerealm.core.player.Player;
import com.dicerealm.core.strategy.BroadcastStrategy;
import com.dicerealm.core.strategy.JsonSerializationStrategy;
public class WebsocketBroadcaster implements BroadcastStrategy {

	private Logger logger = LoggerFactory.getLogger(WebsocketBroadcaster.class);
	private Map<UUID, WebSocketSession> playerSessions;
	private Optional<WebSocketSession> bigScreenSession = Optional.empty();

	private JsonSerializationStrategy serializationStrategy;

	public WebsocketBroadcaster(Map<UUID, WebSocketSession> playerSessions, Optional<WebSocketSession> bigScreenSession, JsonSerializationStrategy serializationStrategy) {
		this.playerSessions = playerSessions;
		this.serializationStrategy = serializationStrategy;
		this.bigScreenSession = bigScreenSession;
	}

	public void setBigScreenSession(Optional<WebSocketSession> bigScreenSession) {
		this.bigScreenSession = bigScreenSession;
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
		logger.info("Sending message to all players: " + textMessage.getPayload());
		playerSessions.values().forEach(session -> {
			sendTextMessage(session, textMessage);
		});
		if (bigScreenSession.isPresent()) {
			sendTextMessage(bigScreenSession.get(), textMessage);
		}
	}

	@Override
	public void sendToPlayer(Command command, Player player) {
		WebSocketSession session = playerSessions.get(player.getId());
		TextMessage textMessage = new TextMessage(serializationStrategy.serialize(command));
		logger.info("Sending message to player: " + player.getId() + " with session: " + session.getId() + " message: " + textMessage.getPayload());
		if (session != null) {
			sendTextMessage(session, textMessage);
		}
	}

	@Override
	public void sendToAllPlayersExcept(Command command, Player player) {
		WebSocketSession playerSession = playerSessions.get(player.getId());
		TextMessage textMessage = new TextMessage(serializationStrategy.serialize(command));
		logger.info("Sending message to all players except: " + player.getId() + " with session: " + playerSession.getId() + " message: " + textMessage.getPayload());
		if (bigScreenSession.isPresent()) {
			sendTextMessage(bigScreenSession.get(), textMessage);
		}
		playerSessions.values().forEach(session -> {
			if (session != playerSession) {
				sendTextMessage(session, textMessage);
			}
		});
	}

	@Override
	public void sendToBigScreen(Command command) {
		if (bigScreenSession.isPresent()) {
			TextMessage textMessage = new TextMessage(serializationStrategy.serialize(command));
			logger.info("Sending message to big screen: " + textMessage.getPayload());
			sendTextMessage(bigScreenSession.get(), textMessage);
		}
	}
}
