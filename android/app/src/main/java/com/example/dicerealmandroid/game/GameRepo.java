package com.example.dicerealmandroid.game;

import com.dicerealm.core.command.StartGameCommand;
import com.example.dicerealmandroid.room.RoomDataSource;
import com.google.gson.Gson;

public class GameRepo {
    private final RoomDataSource roomDataSource;
    private final GameDataSource gameDataSource;
    private final Gson gson = new Gson();

    public GameRepo(){
        this.roomDataSource = RoomDataSource.getInstance();
        this.gameDataSource = GameDataSource.getInstance();
    }

    public void startGame(){
        // Start the game
        StartGameCommand command = new StartGameCommand();
        String message = gson.toJson(command);
        roomDataSource.sendMessageToServer(message);
    }

    public int[] getStatsIds(){
        if (this.gameDataSource.statsIdArray == null){
            throw new IllegalStateException("StatsIdArray is null");
        }
        return this.gameDataSource.statsIdArray;
    }

}
