package com.dicerealm.core.dialogue;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class DialogueTurn {
	// This is an incrementing number that represents the turn number
	private int turnNumber;
	private HashMap<UUID, DialogueTurnAction> actions = new HashMap<UUID, DialogueTurnAction>();
	private long startTime;
	private long endTime;
	private String dungeonMasterText;

	public DialogueTurn(int turnNumber, String dungeonMasterText) {
		this.dungeonMasterText = dungeonMasterText;
		this.turnNumber = turnNumber;
		startTime = new Date().getTime();
		endTime = startTime + 30000;
	}

	public DialogueTurn(int turnNumber, String dungeonMasterText, long deltaTime) {
		this.dungeonMasterText = dungeonMasterText;
		this.turnNumber = turnNumber;
		startTime = new Date().getTime();
		endTime = startTime + deltaTime;
	}

	public void setDialogueTurnAction(UUID playerId, DialogueTurnAction dialogueTurnAction) {
		actions.put(playerId, dialogueTurnAction);
	}

	public DialogueTurnAction getDialogueTurnAction(UUID playerId) {
		return actions.get(playerId);
	}

	public int getTurnNumber() {
		return turnNumber;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public String getDungeonMasterText() {
		return dungeonMasterText;
	}

	public int getNumberOfActions() {
		return actions.size();
	}
}
