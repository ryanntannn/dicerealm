package com.dicerealm.core.handler;

import java.util.UUID;

import com.dicerealm.core.command.PlayerActionCommand;
import com.dicerealm.core.dice.D20;
import com.dicerealm.core.entity.Stat;
import com.dicerealm.core.entity.StatsMap;
import com.dicerealm.core.player.Player;
import com.dicerealm.core.room.RoomContext;

public class PlayerActionHandler extends CommandHandler<PlayerActionCommand> {

	private PlayerMessageHandler playerMessageHandler = new PlayerMessageHandler();

	public PlayerActionHandler() {
		super("PLAYER_ACTION");
	}

	@Override
	public void handle(UUID playerId, PlayerActionCommand command, RoomContext context) {
		D20 d20 = new D20();
		d20.setRandomStrategy(context.getRandomStrategy());
		Player player = context.getRoomState().getPlayerMap().get(playerId);
		StatsMap playerStatsMap = player.getStats();
		StatsMap skillCheck = command.skillCheck;

		// filter skillCheck to remove zero values
		StatsMap filteredSkillCheck = new StatsMap();
		for (Stat key : skillCheck.keySet()) {
			if (skillCheck.get(key) != 0) {
				filteredSkillCheck.put(key, skillCheck.get(key));
			}
		}

		// Check if a skill check is required
		if (filteredSkillCheck.isEmpty()) {
			playerMessageHandler.handleNormalMessage(playerId, "ACTION: " + command.action, context);
			return;
		}

		String skillCheckString = new String();

		boolean success = true;
		// roll a d20 for each stat in the skill check
		StatsMap rollResults = new StatsMap();
		for (Stat key : filteredSkillCheck.keySet()) {
			skillCheckString += ("\n" + key + ": ");
			int roll = d20.roll();
			rollResults.put(key, roll);
			skillCheckString += (roll + "(1d20) + " + playerStatsMap.get(key) + "(modifier) = " + (roll + playerStatsMap.get(key)));
			if (roll >= 20) {
				success = true;
				break;
			}
			if (roll <= 1) {
				success = false;
				break;
			}
			if (roll + playerStatsMap.get(key) < filteredSkillCheck.get(key)) {
				success = false;
				skillCheckString += (" < " + filteredSkillCheck.get(key) + "(Fail)");
			} else {
				skillCheckString += (" >= " + filteredSkillCheck.get(key) + "(Success)");
			}
		}

		if (success) {
			skillCheckString += ("\nit was successful!");
		} else {
			skillCheckString += ("\nit was a fail!");
		}
		String dmMessage = "I attempted action " + command.action + " with skill check: " + skillCheckString;
		playerMessageHandler.handleNormalMessage(playerId, dmMessage, context);
	}
	
}
