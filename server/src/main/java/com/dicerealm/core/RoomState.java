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
		public enum State {
			LOBBY, DIALOGUE, BATTLE
		}

		private State state = State.LOBBY;
		private Map<UUID, Player> playerMap = new HashMap<UUID, Player>();
		private List<Message> messages = Collections.synchronizedList(new ArrayList<Message>());
		private LocationGraph locationGraph = MockLocationGraph.makeLocationGraph();

		public RoomState() {
			// set some placeholder messages

			messages.add(new Message("Welcome to the room!", "Server"));
			messages.add(new Message("This is a text-based multiplayer game", "Server"));
			messages.add(new Message("Type /help for a list of commands", "Server"));
		}

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

		public State getState() {
			return state;
		}

		public void setState(State state) {
			this.state = state;
		}
}
