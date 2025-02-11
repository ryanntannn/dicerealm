package com.ateam.server.handlers;

import java.util.Map;
import java.util.UUID;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.ateam.core.BroadcastStrategy;
import com.ateam.core.Player;
import com.ateam.core.command.Command;
import com.google.gson.Gson;

public class WebsocketBroadcaster implements BroadcastStrategy {
	private Map<UUID, WebSocketSession> playerSessions;

	private Gson gson = new Gson();

	WebsocketBroadcaster(Map<UUID, WebSocketSession> playerSessions) {
		this.playerSessions = playerSessions;
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
		TextMessage	textMessage = new TextMessage(gson.toJson(command));
		playerSessions.values().forEach(session -> {
			sendTextMessage(session, textMessage);
		});
	}

	@Override
	public void sendToPlayer(Command command, Player player) {
		WebSocketSession session = playerSessions.get(player.getId());
		TextMessage textMessage = new TextMessage(gson.toJson(command));
		if (session != null) {
			sendTextMessage(session, textMessage);
		}
	}

	@Override
	public void sendToAllPlayersExcept(Command command, Player player) {
		WebSocketSession playerSession = playerSessions.get(player.getId());
		TextMessage textMessage = new TextMessage(gson.toJson(command));
		playerSessions.values().forEach(session -> {
			if (session != playerSession) {
				sendTextMessage(session, textMessage);
			}
		});
	}
}
