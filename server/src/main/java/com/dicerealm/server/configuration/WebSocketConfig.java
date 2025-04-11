package com.dicerealm.server.configuration;
// This is the configuration class for WebSocket
// connections. It enables WebSocket and registers the
// SocketConnectionHandler class as the handler for the
// "/hello" endpoint. It also sets the allowed origins to
// "*" so that other domains can also access the socket.

import com.dicerealm.server.handlers.BigScreenConnectionHandler;
import com.dicerealm.server.handlers.SocketConnectionHandler;
import com.dicerealm.server.room.RoomRouter;

import java.util.Map;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

// web socket connections is handled 
// by this class
@Configuration
@EnableWebSocket
public class WebSocketConfig
    implements WebSocketConfigurer {

		public static RoomRouter router = new RoomRouter();


		private HandshakeInterceptor getInter() {
			return new HandshakeInterceptor() {
				@Override
				public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
					String path = request.getURI().getPath();
					System.out.println("Path: " + path);
					String roomId = path.substring(path.lastIndexOf('/') + 1);
					attributes.put("roomId", roomId);
					return true;
				}
			
				@Override
				public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {}
			};
		}

    // Overriding a method which register the socket
    // handlers into a Registry
    @Override
    public void registerWebSocketHandlers(
        WebSocketHandlerRegistry webSocketHandlerRegistry)
    {


        // For adding a Handler we give the Handler class we
        // created before with End point Also we are managing
        // the CORS policy for the handlers so that other
        // domains can also access the socket
        webSocketHandlerRegistry
            .addHandler(new SocketConnectionHandler(router),"/connect", "/room/*")
						.addHandler(new BigScreenConnectionHandler(router), "/big/*")
						.addInterceptors(getInter())
            .setAllowedOrigins("*");
    }

		
}