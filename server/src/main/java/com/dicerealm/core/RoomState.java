package com.dicerealm.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.dicerealm.core.locations.LocationGraph;
import com.dicerealm.mock.MockLocationGraph;

/**
 * Represents the entire state of a room
 * 
 * @see Room - manages a RoomState
 * @see Player - represents a player in the room
 * @see Message - represents a text message in the room
 * @see LocationGraph - represents the locations in the room
 */
public class RoomState {
		private Map<UUID, Player> playerMap = new HashMap<UUID, Player>();
		private List<Message> messages = Collections.synchronizedList(new ArrayList<Message>());
		private LocationGraph locationGraph = MockLocationGraph.makeLocationGraph();

		public Player[] getPlayers() {
			return playerMap.values().toArray(new Player[playerMap.size()]);
		}

		public Map<UUID, Player> getPlayerMap() {
			return playerMap;
		}

		public List<Message> getMessages() {
			return messages;
		}

		public LocationGraph getLocationGraph() {
			return locationGraph;
		}
}
