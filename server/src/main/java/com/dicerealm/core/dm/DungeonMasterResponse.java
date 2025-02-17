package com.dicerealm.core.dm;

/**
 * Response from the Dungeon Master should be serialized to this class
 * This response should contain the displayText shown to the players in the room, and the action choices that each player can make
 * 
 * @see DungeonMaster
 */
public class DungeonMasterResponse{

		public String displayText;
		public PlayerAction[] actionChoices;

		public class PlayerAction{
			public String action;
			public String playerId;
		}
}