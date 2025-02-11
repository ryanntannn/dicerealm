package com.ateam.server.handlers;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

// Socket-Connection Configuration class
public class SocketConnectionHandler extends TextWebSocketHandler {
		private RoomManager roomManager = new RoomManager();
		
    @Override
    public void
    afterConnectionEstablished(WebSocketSession session)
        throws Exception
    {
				super.afterConnectionEstablished(session);
				roomManager.onJoin(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session,
                          CloseStatus status)throws Exception
    {
				super.afterConnectionClosed(session, status);
				roomManager.onLeave(session);
    }

    @Override
    public void handleMessage(WebSocketSession session,
                              WebSocketMessage<?> message)
        throws Exception
    {
        super.handleMessage(session, message);
				roomManager.handleMessage(session, message);
    }
}