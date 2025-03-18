package com.dicerealm.core.room;

import com.dicerealm.core.combat.CombatManager;
import com.dicerealm.core.dm.DungeonMaster;
import com.dicerealm.core.strategy.BroadcastStrategy;
import com.dicerealm.core.strategy.RandomStrategy;

public class RoomContext {
	private RoomState roomState;
	private DungeonMaster dungeonMaster;
	private BroadcastStrategy broadcastStrategy;
	private RandomStrategy randomStrategy;
	private CombatManager combatManager;

	public RoomContext(RoomState roomState, DungeonMaster dungeonMaster, BroadcastStrategy broadcastStrategy, RandomStrategy randomStrategy, CombatManager combatManager) {
		this.roomState = roomState;
		this.dungeonMaster = dungeonMaster;
		this.broadcastStrategy = broadcastStrategy;
		this.randomStrategy = randomStrategy;
		this.combatManager = combatManager;
	}

	public RoomState getRoomState() {
		return roomState;
	}

	public DungeonMaster getDungeonMaster() {
		return dungeonMaster;
	}

	public BroadcastStrategy getBroadcastStrategy() {
		return broadcastStrategy;
	}

	public RandomStrategy getRandomStrategy() {
		return randomStrategy;
	}

	public CombatManager getCombatManager() {
		return combatManager;
	}
}
