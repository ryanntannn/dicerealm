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
		public String locationId;
		public String contextSummary;
		public boolean switchToCombatThisTurn;

		public class PlayerAction{
			public String action;
			public String playerId;
			public SkillCheck skillCheck;
		}

		public class SkillCheck {
			public int STRENGTH;
			public int DEXTERITY;
			public int CONSTITUTION;
			public int INTELLIGENCE;
			public int WISDOM;
			public int CHARISMA;
		}
}