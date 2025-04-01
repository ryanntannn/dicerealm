package com.dicerealm.core.command.combat;

import java.util.UUID;

import com.dicerealm.core.command.Command;

/**
 * Sent by the server to the client to indicate the start of combat
 * RoomState should be set to BATTLE
 */
public class CombatStartCommand extends Command {
		private String displayText;
		private UUID[] turnOrderIds;

		public CombatStartCommand(String displayText, UUID[] turnOrderIds) {
			super.type = "COMBAT_START";
			this.displayText = displayText;
			this.turnOrderIds = turnOrderIds;
		}

		public String getDisplayText() {
			return displayText;
		}

		public UUID[] getTurnOrderIds() {
			return turnOrderIds;
		}
}
