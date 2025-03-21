package com.example.dicerealmandroid.core;

import java.util.HashMap;
import java.util.UUID;

public class DialogueTurn {
    // This is an incrementing number that represents the turn number
    private int turnNumber;
    private HashMap<UUID, DialogueTurnAction> actions = new HashMap<UUID, DialogueTurnAction>();
    private String dungeonMasterText;

    public DialogueTurn(int turnNumber, String dungeonMasterText) {
        this.dungeonMasterText = dungeonMasterText;
        this.turnNumber = turnNumber;
    }

    public DialogueTurn(int turnNumber, String dungeonMasterText, long deltaTime) {
        this.dungeonMasterText = dungeonMasterText;
        this.turnNumber = turnNumber;
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

    public String getDungeonMasterText() {
        return dungeonMasterText;
    }

    public int getNumberOfActions() {
        return actions.size();
    }
}

