package com.dicerealm.server.room;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

public class RoomRouter {

	private Logger logger = LoggerFactory.getLogger(RoomRouter.class);

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
		// trim and uppercase
		roomId = roomId.trim().toUpperCase();
		if (!roomManagers.containsKey(roomId)) {
			System.out.println("Making new room manager");
			roomManagers.put(roomId, new RoomManager());
		}
		return roomManagers.get(roomId);
	}

	public void onJoin(WebSocketSession session) {
		getRoomManager(session).onJoin(session);
	}

	public void onBigScreenJoin(WebSocketSession session) {
		getRoomManager(session).onBigScreenJoin(session);
	}
	

	public void onLeave(WebSocketSession session) {
		RoomManager roomManager = getRoomManager(session);
		roomManager.onLeave(session);
		if (roomManager.isEmpty()) {
			logger.info("Closing room: " + session.getAttributes().get("roomId") + " because it is empty.");
			roomManagers.remove((String) session.getAttributes().get("roomId"));
		}
	}

	public void onBigScreenLeave(WebSocketSession session) {
		RoomManager roomManager = getRoomManager(session);
		roomManager.onBigScreenLeave(session);
		if (roomManager.isEmpty()) {
			logger.info("Closing room: " + session.getAttributes().get("roomId") + " because it is empty.");
			roomManagers.remove((String) session.getAttributes().get("roomId"));
		}
	}

	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
		try{
			getRoomManager(session).handleMessage(session, message);
		} catch (Exception e) {
			logger.error("Error handling message: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
