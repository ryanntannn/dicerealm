package com.dicerealm.core.dm;

public class DungeonMasterResponse{

		public String displayText;
		public PlayerAction[] actionChoices;
		public RemovedItems[] removedItems;

		public class PlayerAction{
			public String action;
			public String playerId;
		}
		
		public class RemovedItems{
			public String itemId;
			public String playerId;
		}
}