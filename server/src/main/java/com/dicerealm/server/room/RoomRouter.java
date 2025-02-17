package com.dicerealm.server.room;

import java.util.HashMap;

import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

public class RoomRouter {

	private HashMap<String, RoomManager> roomManagers = new HashMap<String, RoomManager>();

	/**
	 * Get the RoomManager for the room that the session is in. If the room does not exist, create it.
	 * `roomId` is stored in the session attributes by the WebSocketConfig.
	 * @param session
	 * @return RoomManager
	 * @see com.dicerealm.server.configuration.WebSocketConfig - getInter()
	 */
	private RoomManager getRoomManager(WebSocketSession session) {
		String roomId = (String) session.getAttributes().get("roomId");
		if (!roomManagers.containsKey(roomId)) {
			roomManagers.put(roomId, new RoomManager());
		}
		return roomManagers.get(roomId);
	}
	
	public void onJoin(WebSocketSession session) {
		getRoomManager(session).onJoin(session);
	}

	public void onLeave(WebSocketSession session) {
		getRoomManager(session).onLeave(session);
	}

	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
		getRoomManager(session).handleMessage(session, message);
	}
}
