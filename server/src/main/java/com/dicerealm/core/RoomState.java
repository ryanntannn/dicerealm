package com.dicerealm.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RoomState {
		private Map<UUID, Player> playerMap = new HashMap<UUID, Player>();
		private List<Message> messages = Collections.synchronizedList(new ArrayList<Message>());

		public Player[] getPlayers() {
			return playerMap.values().toArray(new Player[playerMap.size()]);
		}

		public Map<UUID, Player> getPlayerMap() {
			return playerMap;
		}

		public List<Message> getMessages() {
			return messages;
		}
}
