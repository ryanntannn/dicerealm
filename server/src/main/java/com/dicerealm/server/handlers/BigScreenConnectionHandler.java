package com.dicerealm.server.handlers;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.dicerealm.server.room.RoomRouter;

// Socket-Connection Configuration class
public class BigScreenConnectionHandler extends TextWebSocketHandler {

	private RoomRouter router;

	public BigScreenConnectionHandler(RoomRouter router) {
		this.router = router;
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		super.afterConnectionEstablished(session);
		router.onBigScreenJoin(session);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		super.afterConnectionClosed(session, status);
		router.onBigScreenLeave(session);
	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message)
			throws Exception {
		super.handleMessage(session, message);
		router.handleMessage(session, message);
	}
}
