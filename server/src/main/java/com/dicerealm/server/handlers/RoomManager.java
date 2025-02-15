package com.dicerealm.server.handlers;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.dicerealm.core.MockLLMStrategy;
import com.dicerealm.core.Player;
import com.dicerealm.core.Room;
import com.dicerealm.core.command.CommandDeserializerStrategy;

/**
 * Manages a single room and all the players in it and their websocket sessions.
 */
public class RoomManager {

	private Map<UUID, WebSocketSession> playerSessions = Collections.synchronizedMap(new HashMap<UUID, WebSocketSession>());
	private Map<String, UUID> sessionIdToPlayerIdMap = Collections.synchronizedMap(new HashMap<String, UUID>());
	
	// We use the OpenAI LLMStrategy and the WebsocketBroadcaster BroadcastStrategy for this room
	// private OpenAI llm = new OpenAI();
	private MockLLMStrategy llm = new MockLLMStrategy("{\"displayText\": \"mock response\", \"actionChoices\":[]}");
	private WebsocketBroadcaster broadcaster = new WebsocketBroadcaster(playerSessions);
	private CommandDeserializerStrategy deserializer = new GsonDeserializer();
	private Room room = Room.builder()
		.setBroadcastStrategy(broadcaster)
		.setLLMStrategy(llm)
		.setCommandDeserializerStrategy(deserializer)
		.build();

	
	public void onJoin(WebSocketSession session) {
		Player newPlayer = new Player();
		playerSessions.put(newPlayer.getId(), session);
		sessionIdToPlayerIdMap.put(session.getId(), newPlayer.getId());
		room.addPlayer(newPlayer);
	}

	public void onLeave(WebSocketSession session) {
		UUID playerId = sessionIdToPlayerIdMap.get(session.getId());
		playerSessions.remove(playerId);
		sessionIdToPlayerIdMap.remove(session.getId());
		room.removePlayerById(playerId);
	}


	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
		try {
			UUID playerId = sessionIdToPlayerIdMap.get(session.getId());
			Object payload = message.getPayload();
	
			// check if payload is a string
			if (!(payload instanceof String)) {
				throw new IllegalArgumentException("Payload must be a string");
			}
			room.handlePlayerCommand(playerId, (String) payload);
		} catch (Exception e) {
			sendErrorMessage(session, e.getMessage());
		}
	}

	private void sendErrorMessage(WebSocketSession session, String message) {
		try {
			session.sendMessage(new TextMessage("ERROR: " + message));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
