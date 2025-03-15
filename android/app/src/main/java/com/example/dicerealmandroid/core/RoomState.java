package com.example.dicerealmandroid.core;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RoomState {
    public enum State {
        LOBBY, DIALOGUE_TURN, DIALOGUE_PROCESSING, BATTLE
    }
    private State state = State.LOBBY;
    private Map<UUID, Player> playerMap = new HashMap<>();

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Player[] getPlayers() {
        return playerMap.values().toArray(new Player[playerMap.size()]);
    }

    public Map<UUID, Player> getPlayerMap() {
        return playerMap;
    }

    public void addPlayer(Player player) {
        playerMap.put(player.getId(), player);
    }

    public void removePlayer(UUID playerId) {
        playerMap.remove(playerId);
    }
}
