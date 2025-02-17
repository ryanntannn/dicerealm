package com.dicerealm.server.handlers;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.dicerealm.core.JsonSerializationStrategy;
import com.dicerealm.core.Player;
import com.dicerealm.core.PresetPlayerFactory;
import com.dicerealm.core.Room;

/**
 * Manages a single room and all the players in it and their websocket sessions.
 */
public class RoomManager {

	private Map<UUID, WebSocketSession> playerSessions = Collections.synchronizedMap(new HashMap<UUID, WebSocketSession>());
	private Map<String, UUID> sessionIdToPlayerIdMap = Collections.synchronizedMap(new HashMap<String, UUID>());
	
	// We use the OpenAI LLMStrategy and the WebsocketBroadcaster BroadcastStrategy for this room
	// private MockLLMStrategy llm = new MockLLMStrategy("{\"displayText\": \"mock response\", \"actionChoices\":[]}");
	private JsonSerializationStrategy serializer = new GsonSerializer();
	private OpenAI llm = new OpenAI(serializer);
	private WebsocketBroadcaster broadcaster = new WebsocketBroadcaster(playerSessions, serializer);
	private Room room = Room.builder()
		.setBroadcastStrategy(broadcaster)
		.setLLMStrategy(llm)
		.setJsonSerializationStrategy(serializer)
		.build();

	
	public void onJoin(WebSocketSession session) {
		Player newPlayer = PresetPlayerFactory.createPresetPlayer();
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
