package com.dicerealm.core.room;

import java.util.UUID;

import com.dicerealm.core.combat.managers.CombatManager;
import com.dicerealm.core.combat.managers.LevelManager;
import com.dicerealm.core.combat.managers.MonsterAI;
import com.dicerealm.core.command.Command;
import com.dicerealm.core.command.FullRoomStateCommand;
import com.dicerealm.core.command.PlayerJoinCommand;
import com.dicerealm.core.command.PlayerLeaveCommand;
import com.dicerealm.core.dm.DungeonMaster;
import com.dicerealm.core.handler.CombatTurnActionHandler;
import com.dicerealm.core.handler.CommandRouter;
import com.dicerealm.core.handler.DialogueTurnActionHandler;
import com.dicerealm.core.handler.LevelUpHandler;
import com.dicerealm.core.handler.PlayerEquipItemHandler;
import com.dicerealm.core.handler.StartGameHandler;
import com.dicerealm.core.handler.UpdatePlayerDetailsHandler;
import com.dicerealm.core.player.Player;
import com.dicerealm.core.strategy.BroadcastStrategy;
import com.dicerealm.core.strategy.JsonSerializationStrategy;
import com.dicerealm.core.strategy.LLMStrategy;
import com.dicerealm.core.strategy.RandomStrategy;

/**
 * Represents a room in the game, containing the state of the room and the logic for handling player actions and messages
 * 
 * @see Command - base class for sending/receiving information between players and the room
 * @see Player - represents a player in the room
 * @see RoomBuilder - for creating a Room
 * @see RoomState - contains the entire state of the room
 * @see BroadcastStrategy - broadcasts Commands to players
 * @see LLMStrategy - for generating prompt responses using an LLM
 * @see DungeonMaster - uses an LLMStrategy to handle player messages
 * @see CommandDeserializer - for deserializing incoming player commands
 */
public class Room {
	private RoomState roomState = new RoomState();

	private BroadcastStrategy broadcastStrategy;
	private DungeonMaster dungeonMaster;
	private CommandRouter commandRouter;
	private RandomStrategy randomStrategy;
	private CombatManager combatManager;
	private MonsterAI monsterAI;
	private LevelManager levelManager;

	/**
	 * Create a new RoomBuilder for creating a Room
	 * @return RoomBuilder
	 */
	public static RoomBuilder builder() {
		return new RoomBuilder();
	}
	
	/**
	 * Create a new Room, you may want to use the RoomBuilder instead
	 * @param broadcastStrategy
	 * @param llmStrategy
	 * @param jsonSerializationStrategy
	 * @param randomStrategy
	 * 
	 * @see RoomBuilder
	 */
	public Room(BroadcastStrategy broadcastStrategy, LLMStrategy llmStrategy, JsonSerializationStrategy jsonSerializationStrategy, RandomStrategy randomStrategy, CommandRouter commandRouter) {
		this.broadcastStrategy = broadcastStrategy;
		this.dungeonMaster = new DungeonMaster(llmStrategy, jsonSerializationStrategy, roomState);
		this.commandRouter = commandRouter;
		this.randomStrategy = randomStrategy;
		this.combatManager = new CombatManager();
		this.monsterAI = new MonsterAI();
		this.levelManager = new LevelManager();

		commandRouter.registerHandler(new StartGameHandler());
		commandRouter.registerHandler(new PlayerEquipItemHandler());
		commandRouter.registerHandler(new UpdatePlayerDetailsHandler());
		commandRouter.registerHandler(new DialogueTurnActionHandler());
		commandRouter.registerHandler(new CombatTurnActionHandler());
		commandRouter.registerHandler(new LevelUpHandler());
	}

	/**
	 * Add a player to the room
	 * @param player
	 */
	public void addPlayer(Player player) {
		roomState.getPlayerMap().put(player.getId(), player);
		broadcastStrategy.sendToAllPlayersExcept(new PlayerJoinCommand(player), player);
		broadcastStrategy.sendToPlayer(new FullRoomStateCommand(roomState, player.getId().toString()), player);
	}

	/**
	 * Get a player by their ID
	 * @param id
	 * @return
	 */
	public Player getPlayerById(UUID id) {
		return roomState.getPlayerMap().get(id);
	}

	/**
	 * Remove a player from the room by their ID
	 * @param id
	 * @throws NullPointerException
	 */
	public void removePlayerById(UUID id) throws NullPointerException {
		Player player = roomState.getPlayerMap().get(id);
		roomState.getPlayerMap().remove(id);
		broadcastStrategy.sendToAllPlayers(new PlayerLeaveCommand(player));
	}

	/**
	 * Check if the room has no players
	 * @return
	 */
	public boolean isEmpty() {
		return roomState.getPlayers().length == 0;
	}

	/**
	 * Handle an incoming command from a player
	 * @param playerId
	 * @param json
	 */
	public void handlePlayerCommand(UUID playerId, String json) {
		commandRouter.handlePlayerCommand(playerId, json, new RoomContext(roomState, dungeonMaster, broadcastStrategy, randomStrategy, combatManager, monsterAI, levelManager));
	}
}
