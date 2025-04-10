package com.dicerealm.core.room;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.dicerealm.core.dialogue.DialogueTurn;
import com.dicerealm.core.locations.LocationGraph;
import com.dicerealm.core.message.Message;
import com.dicerealm.core.player.Player;
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
			LOBBY, DIALOGUE_TURN, DIALOGUE_PROCESSING, BATTLE
		}

		private State state = State.LOBBY;
		private Map<UUID, Player> playerMap = new HashMap<UUID, Player>();
		private List<Message> messages = Collections.synchronizedList(new ArrayList<Message>());
		private LocationGraph locationGraph = MockLocationGraph.makeLocationGraph();
		private List<DialogueTurn> dialogueTurns = new ArrayList<DialogueTurn>();
		private int roomLevel = 1;
		private int roomExperience = 0;


		public RoomState() {
			// set some placeholder messages
			messages.add(new Message("Welcome to the room!", "Server"));
			messages.add(new Message("This is a text-based multiplayer game", "Server"));
			messages.add(new Message("Type /help for a list of commands", "Server"));
		}

		public Player[] getPlayers() {
			return playerMap.values().toArray(new Player[playerMap.size()]);
		}

		public String getPlayerSummaries() {
			StringBuilder sb = new StringBuilder();
			for (Player player : playerMap.values()) {
				sb.append(player.getSummary()).append(", ");
			}
			if (sb.length() > 0) {
				sb.setLength(sb.length() - 2); // remove last comma and space
			}
			return sb.toString();
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

		public void setLocationGraph(LocationGraph locationGraph) {
			this.locationGraph = locationGraph;
		}

		public State getState() {
			return state;
		}

		public void setState(State state) {
			this.state = state;
		}

		public DialogueTurn getCurrentDialogueTurn() {
			if (dialogueTurns.size() == 0) {
				return null;
			}
			return dialogueTurns.get(dialogueTurns.size() - 1);
		}

		public int getCurrentDialogueTurnNumber() {
			return dialogueTurns.size() - 1;
		}

		public DialogueTurn addDialogueTurn(String dungeonMasterText) {
			DialogueTurn dialogueTurn = new DialogueTurn(dialogueTurns.size(), dungeonMasterText);
			dialogueTurns.add(dialogueTurn);
			return dialogueTurn;
		}

		public void addPlayer(Player player) {
			playerMap.put(player.getId(), player);
		}

		public void removePlayer(UUID playerId) {
			playerMap.remove(playerId);
		}

		public void setRoomLevel(int roomLevel) {
			this.roomLevel = roomLevel;
		}
		public int getRoomLevel() {
			return roomLevel;
		}
		public void addRoomExperience(int xp) {
			roomExperience += xp;
		}
		public int getRoomExperience() {
			return roomExperience;
		}
}
