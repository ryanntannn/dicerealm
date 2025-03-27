package com.dicerealm.core.dialogue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import com.dicerealm.core.command.ChangeLocationCommand;
import com.dicerealm.core.command.ShowPlayerActionsCommand;
import com.dicerealm.core.command.dialogue.EndTurnCommand;
import com.dicerealm.core.command.dialogue.StartTurnCommand;
import com.dicerealm.core.dm.DungeonMasterResponse;
import com.dicerealm.core.locations.Location;
import com.dicerealm.core.player.Player;
import com.dicerealm.core.room.RoomContext;
import com.dicerealm.core.room.RoomState;


public class DialogueManager {

	/**
	 * Start a new dialogue turn with given dungeonMasterText
	 * @param dungeonMasterText the text to display to the players
	 * @param context the RoomContext
	 * @return the new DialogueTurn
	 */
	public static DialogueTurn startNewDialogueTurn(String dungeonMasterText, RoomContext context) {
		DialogueTurn turn = context.getRoomState().addDialogueTurn(dungeonMasterText);
		context.getBroadcastStrategy().sendToAllPlayers(new StartTurnCommand(turn));
		return turn;
	}


	/**
	 * Handle a location change from a DungeonMasterResponse
	 * @param response
	 * @param context
	 * @throws IllegalArgumentException
	 */
	public static void broadcastLocationChange(DungeonMasterResponse response, RoomContext context) {
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
	public static void broadcastPlayerActions(DungeonMasterResponse.PlayerAction[] actionChoices, RoomContext context) {
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

	/**
	 * End the current dialogue turn
	 * @param context
	 */
	public static void endDialogueTurn(RoomContext context) {
		DialogueTurn currentTurn = context.getRoomState().getCurrentDialogueTurn();

		context.getRoomState().setState(RoomState.State.DIALOGUE_TURN);

		SkillCheck skillCheck = new SkillCheck(context.getRandomStrategy());

		StringBuilder dungeonMasterPrompt = new StringBuilder();

		for (Player player : context.getRoomState().getPlayers()) {
			DialogueTurnAction action = currentTurn.getDialogueTurnAction(player.getId());
			if (action == null) {
				context.getBroadcastStrategy().sendToPlayer(new EndTurnCommand(context.getRoomState().getCurrentDialogueTurnNumber(), new SkillCheck.ActionResultDetail()), player);
				continue;
			}

			SkillCheck.ActionResultDetail actionResult = skillCheck.check(action, player.getStats());

			dungeonMasterPrompt.append(player.getDisplayName() + " choses: " + actionResult.toString() + "\n");

			context.getBroadcastStrategy().sendToPlayer(new EndTurnCommand(context.getRoomState().getCurrentDialogueTurnNumber(), actionResult), player);
		}

		DungeonMasterResponse response = context.getDungeonMaster().handleDialogueTurn(dungeonMasterPrompt.toString());

		broadcastLocationChange(response, context);
		broadcastPlayerActions(response.actionChoices, context);

		startNewDialogueTurn(response.displayText, context);
	}
}
