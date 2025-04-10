package com.dicerealm.core.dm;

import java.util.Map;

import com.dicerealm.core.entity.Stat;
import com.dicerealm.core.entity.StatsMap;
import com.dicerealm.core.monster.Monster;

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

			public StatsMap toStatsMap() {
				return new StatsMap(Map.of(
					Stat.STRENGTH, STRENGTH,
					Stat.DEXTERITY, DEXTERITY,
					Stat.CONSTITUTION, CONSTITUTION,
					Stat.INTELLIGENCE, INTELLIGENCE,
					Stat.WISDOM, WISDOM,
					Stat.CHARISMA, CHARISMA
				));
			}
		}
		
		public class Stats {
			public int maxHealth;
			public int armourClass;
			public int strength;
			public int dexterity;
			public int constitution;
			public int intelligence;
			public int wisdom;
			public int charisma;
		}
}