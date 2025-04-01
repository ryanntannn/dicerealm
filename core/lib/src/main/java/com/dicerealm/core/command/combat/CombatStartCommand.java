package com.dicerealm.core.command.combat;

import java.util.List;

import com.dicerealm.core.combat.systems.InitiativeResult;
import com.dicerealm.core.command.Command;

/**
 * Sent by the server to the client to indicate the start of combat
 * RoomState should be set to BATTLE
 */
public class CombatStartCommand extends Command {
		private String displayText;
		private List<InitiativeResult> initiativeResults;

		public CombatStartCommand(String displayText, List<InitiativeResult> initiativeResults) {
				this.type = "COMBAT_START";
				this.displayText = displayText;
				this.initiativeResults = initiativeResults;
		}

		public String getDisplayText() {
				return displayText;
		}

		public List<InitiativeResult> getInitiativeResults() {
				return initiativeResults;
		}
}
