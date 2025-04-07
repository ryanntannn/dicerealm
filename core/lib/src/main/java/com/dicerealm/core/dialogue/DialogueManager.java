package com.dicerealm.core.dialogue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.dicerealm.core.combat.managers.MonsterGenerator;
import com.dicerealm.core.combat.systems.InitiativeResult;
import com.dicerealm.core.combat.systems.RoomStrengthCalculator;
import com.dicerealm.core.command.ChangeLocationCommand;
import com.dicerealm.core.command.ShowPlayerActionsCommand;
import com.dicerealm.core.command.combat.CombatStartCommand;
import com.dicerealm.core.command.dialogue.EndTurnCommand;
import com.dicerealm.core.command.dialogue.StartTurnCommand;
import com.dicerealm.core.dm.DungeonMasterResponse;
import com.dicerealm.core.entity.Entity;
import com.dicerealm.core.entity.EntityClass;
import com.dicerealm.core.entity.Race;
import com.dicerealm.core.entity.Stat;
import com.dicerealm.core.entity.StatsMap;
import com.dicerealm.core.handler.CombatTurnActionHandler;
import com.dicerealm.core.locations.Location;
import com.dicerealm.core.monster.Monster;
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
		context.getRoomState().setState(RoomState.State.DIALOGUE_TURN);
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
	 * Transition RoomState from DIALOGUE_PROCESSING to BATTLE
	 * @param displayText
	 */
	public static void handleSwitchToCombat(String displayText, RoomContext context) {
		context.getRoomState().setState(RoomState.State.BATTLE);
		List<Entity> combatParticipants = new ArrayList<>();
		
		// add all players and entities in the location to the combat manager
		for (Player player : context.getRoomState().getPlayers()) {
			combatParticipants.add(player);
		}
		for (Entity entity : context.getRoomState().getLocationGraph().getCurrentLocation().getEntities()) {
			combatParticipants.add(entity);
		}

		// Update the combat manager with the new participants
		context.getCombatManager().newCombat(combatParticipants);
		context.getMonsterAI().setCombatManager(context.getCombatManager());

		// Initialize the turn order
		context.getCombatManager().startCombat();

		// Get the turn order of combat participants
		List<InitiativeResult> turnOrderIds = context.getCombatManager().getInitiativeResults();

		// Send the turn order to all players
		context.getBroadcastStrategy().sendToAllPlayers(new CombatStartCommand(displayText, turnOrderIds));

		// Start the first turn
		CombatTurnActionHandler.handleNextTurn(context);
	}

	/**
	 * End the current dialogue turn
	 * This transitions the RoomState from DIALOGUE_TURN to DIALOGUE_PROCESSING
	 * @param context
	 */
	public static void endDialogueTurn(RoomContext context) {
		DialogueTurn currentTurn = context.getRoomState().getCurrentDialogueTurn();

		context.getRoomState().setState(RoomState.State.DIALOGUE_PROCESSING);

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

		handleDungeonMasterResponse(response, context);
	}

	public static void handleDungeonMasterResponse(DungeonMasterResponse response, RoomContext context) {
		
		broadcastLocationChange(response, context);
		RoomState roomState = context.getRoomState();
		if (response.switchToCombatThisTurn) {	
			boolean isRoomBalanced = RoomStrengthCalculator.isRoomBalanced(roomState);
			while (!isRoomBalanced) {
				// Add a monster to the room if it is not balanced
				addMonster(response.enemy, context.getRoomState());
				isRoomBalanced = RoomStrengthCalculator.isRoomBalanced(roomState);
			}
			List<Entity> monster = context.getRoomState().getLocationGraph().getCurrentLocation().getEntities();

			handleSwitchToCombat(response.displayText, context);
		} else {
			broadcastPlayerActions(response.actionChoices, context);
			startNewDialogueTurn(response.displayText, context);
		}
	}

	public static void addMonster(DungeonMasterResponse.Enemy enemy, RoomState roomState) {
		try {
			Monster monster = MonsterGenerator.generateMonster(enemy.name, 
			EntityClass.valueOf(enemy.entityClass.toUpperCase()), 
			Race.valueOf(enemy.race.toUpperCase()), 
			roomState.getRoomLevel()
			);
			roomState.getLocationGraph().getCurrentLocation().getEntities().add(monster);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Error creating monster: Invalid enum value for race or entity class. " + e.getMessage());
		} catch (NullPointerException e) {
			throw new NullPointerException("Error creating monster: Missing required fields in enemy stats. " + e.getMessage());
		}
	}
}
