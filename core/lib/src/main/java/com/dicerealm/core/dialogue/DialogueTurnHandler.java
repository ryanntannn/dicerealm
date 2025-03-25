package com.dicerealm.core.dialogue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import com.dicerealm.core.command.ChangeLocationCommand;
import com.dicerealm.core.command.ShowPlayerActionsCommand;
import com.dicerealm.core.command.dialogue.EndTurnCommand;
import com.dicerealm.core.command.dialogue.StartTurnCommand;
import com.dicerealm.core.dice.D20;
import com.dicerealm.core.dm.DungeonMasterResponse;
import com.dicerealm.core.entity.Stat;
import com.dicerealm.core.entity.StatsMap;
import com.dicerealm.core.locations.Location;
import com.dicerealm.core.player.Player;
import com.dicerealm.core.room.RoomContext;
import com.dicerealm.core.room.RoomState;


public class DialogueTurnHandler {

	public static DialogueTurn startNewDialogueTurn(String dungeonMasterText, RoomContext context) {
		DialogueTurn turn = context.getRoomState().addDialogueTurn(dungeonMasterText);
		context.getBroadcastStrategy().sendToAllPlayers(new StartTurnCommand(turn));
		return turn;
	}


	/**
	 * Handle a location change from a DungeonMasterResponse
	 * @param response
	 */
	public static void handleLocationChange(DungeonMasterResponse response, RoomContext context) {
		if (response.locationId == null) {
			return;
		}
		UUID newLocationUuid = UUID.fromString(response.locationId);
		// check if location change is needed
		if (newLocationUuid.equals(context.getRoomState().getLocationGraph().getCurrentLocation().getId())) {
			return;
		}
		Location newLocation = context.getRoomState().getLocationGraph().getN(newLocationUuid);
		if (newLocation == null) {
			throw new IllegalArgumentException("Invalid location id");
		}
		context.getRoomState().getLocationGraph().setCurrentLocation(newLocation);
		context.getBroadcastStrategy().sendToAllPlayers(new ChangeLocationCommand(newLocation));
	}

	/**
	 * Handle the actionChoices from a DungeonMasterResponse, sending them to the appropriate players
	 * @param actionChoices
	 */
	public static void handlePlayerActions(DungeonMasterResponse.PlayerAction[] actionChoices, RoomContext context) {
		HashMap<UUID, ArrayList<DungeonMasterResponse.PlayerAction>> playerActions = new HashMap<>();
		for (DungeonMasterResponse.PlayerAction action : actionChoices) {
			UUID id = UUID.fromString(action.playerId);
			if (!playerActions.containsKey(id)) {
				playerActions.put(id, new ArrayList<>());
			}
			playerActions.get(id).add(action);
		}
		for (UUID id : playerActions.keySet()) {
			Player player = context.getRoomState().getPlayerMap().get(id);
			ArrayList<DungeonMasterResponse.PlayerAction> actions = playerActions.getOrDefault(id, new ArrayList<>());
			DungeonMasterResponse.PlayerAction[] playerActionsArray = actions.toArray(new DungeonMasterResponse.PlayerAction[actions.size()]);
			context.getBroadcastStrategy().sendToPlayer(new ShowPlayerActionsCommand(playerActionsArray), player);
		}
	}

	public static void endDialogueTurn(RoomContext context) {
		DialogueTurn currentTurn = context.getRoomState().getCurrentDialogueTurn();
		context.getRoomState().setState(RoomState.State.DIALOGUE_TURN);
		context.getBroadcastStrategy().sendToAllPlayers(new EndTurnCommand(context.getRoomState().getCurrentDialogueTurnNumber()));


		D20 d20 = new D20();
		d20.setRandomStrategy(context.getRandomStrategy());

		StringBuilder dungeonMasterPrompt = new StringBuilder();

		for (Player player : context.getRoomState().getPlayers()) {
			DialogueTurnAction action = currentTurn.getDialogueTurnAction(player.getId());
			if (action == null) {
				continue;
			}

			// Check if a skill check is required
			StatsMap skillCheck = action.getSkillCheck();
			StatsMap filteredSkillCheck = new StatsMap();
			for (Stat key : skillCheck.keySet()) {
				if (skillCheck.get(key) != 0) {
					filteredSkillCheck.put(key, skillCheck.get(key));
				}
			}

			if (filteredSkillCheck.isEmpty()) {
				dungeonMasterPrompt.append(player.getDisplayName() + " wants to do: " + action.getAction() + "\n");
				continue;
			}

			StatsMap playerStatsMap = player.getStats();
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

			dungeonMasterPrompt.append(player.getDisplayName() + " wants to do: " + action.getAction() + " with skill check: " + skillCheckString + "\n");
		}

		DungeonMasterResponse response = context.getDungeonMaster().handleDialogueTurn(dungeonMasterPrompt.toString());

		handleLocationChange(response, context);
		handlePlayerActions(response.actionChoices, context);

		startNewDialogueTurn(response.displayText, context);
	}
}
