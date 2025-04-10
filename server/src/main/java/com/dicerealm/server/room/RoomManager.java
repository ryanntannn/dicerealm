package com.dicerealm.server.room;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.dicerealm.core.player.Player;
import com.dicerealm.core.player.PresetPlayerFactory;
import com.dicerealm.core.room.Room;
import com.dicerealm.core.strategy.JsonSerializationStrategy;
import com.dicerealm.mock.MockLLMStrategy;
import com.dicerealm.server.strategy.GsonSerializer;
import com.dicerealm.server.strategy.OpenAI;
import com.dicerealm.server.strategy.WebsocketBroadcaster;

/**
 * Manages a single room and all the players in it and their websocket sessions.
 */
public class RoomManager {

	private Logger logger = LoggerFactory.getLogger(RoomManager.class);
	private Map<UUID, WebSocketSession> playerSessions = Collections.synchronizedMap(new HashMap<UUID, WebSocketSession>());
	private Map<String, UUID> sessionIdToPlayerIdMap = Collections.synchronizedMap(new HashMap<String, UUID>());
	
	// We use the OpenAI LLMStrategy and the WebsocketBroadcaster BroadcastStrategy for this room
	private JsonSerializationStrategy serializer = new GsonSerializer();
	// private MockLLMStrategy llm = new MockLLMStrategy(serializer);
	private OpenAI llm = new OpenAI(serializer);
	private WebsocketBroadcaster broadcaster = new WebsocketBroadcaster(playerSessions, serializer);

	private Room room = Room.builder()
		.setBroadcastStrategy(broadcaster)
		.setLLMStrategy(llm)
		.setJsonSerializationStrategy(serializer)
		.build();

	// public RoomManager() {
	// 	llm.setResponse("{\"displayText\": \"This is a mock response\", \"actionChoices\":[]}");
	// }

	
	public void onJoin(WebSocketSession session) {
		Player newPlayer = PresetPlayerFactory.createPresetPlayer();
		playerSessions.put(newPlayer.getId(), session);
		sessionIdToPlayerIdMap.put(session.getId(), newPlayer.getId());
		room.addPlayer(newPlayer);	

		logger.info("Player joined room: " + newPlayer.getId() + " with session: " + session.getId());
	}

	public void onLeave(WebSocketSession session) {
		UUID playerId = sessionIdToPlayerIdMap.get(session.getId());
		playerSessions.remove(playerId);
		sessionIdToPlayerIdMap.remove(session.getId());
		room.removePlayerById(playerId);

		logger.info("Player left room: " + playerId);
	}

	public boolean isEmpty() {
		return room.isEmpty();
	}


	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
		try {
			UUID playerId = sessionIdToPlayerIdMap.get(session.getId());
			Object payload = message.getPayload();
			logger.info("Received message from player: " + playerId + " with payload: " + (String) payload);
			// check if payload is a string
			if (!(payload instanceof String)) {
				throw new IllegalArgumentException("Payload must be a string");
			}
			room.handlePlayerCommand(playerId, (String) payload);
		} catch (Exception e) {
			logger.error("Error handling message", e);
		}
	}

}
