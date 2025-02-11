package com.ateam.server.handlers;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.ateam.core.Player;
import com.ateam.core.Room;
import com.ateam.core.command.Command;
import com.ateam.core.command.MessageCommand;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * Manages a single room and all the players in it and their websocket sessions.
 */
public class RoomManager {

	private Map<UUID, WebSocketSession> playerSessions = Collections.synchronizedMap(new HashMap<UUID, WebSocketSession>());
	private Map<String, UUID> sessionIdToPlayerIdMap = Collections.synchronizedMap(new HashMap<String, UUID>());
	
	// We use the OpenAI LLMStrategy and the WebsocketBroadcaster BroadcastStrategy for this room
	private OpenAI openAI = new OpenAI();
	private WebsocketBroadcaster broadcaster = new WebsocketBroadcaster(playerSessions);
	private Room room = new Room(broadcaster, openAI);
	private Gson gson = new Gson();
	
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

		UUID playerId = sessionIdToPlayerIdMap.get(session.getId());
		Object payload = message.getPayload();

		// check if payload is a string
		if (!(payload instanceof String)) {
			return;
		}

		try {
			Command command = gson.fromJson((String)payload, Command.class);
			switch (command.type) {
				case "MESSAGE":
					MessageCommand messageCommand = gson.fromJson((String)payload, MessageCommand.class);
					room.handleNormalMessage(playerId, messageCommand.message);
					break;
				default:
					sendErrorMessage(session, "Invalid Command Type");
					break;
			}
		} catch (JsonSyntaxException e) {
			sendErrorMessage(session, "Invalid JSON");
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
