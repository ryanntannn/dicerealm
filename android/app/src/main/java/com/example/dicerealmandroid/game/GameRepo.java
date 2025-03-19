package com.example.dicerealmandroid.game;

import com.example.dicerealmandroid.command.StartGameCommand;
import com.example.dicerealmandroid.room.RoomDataSource;
import com.google.gson.Gson;

public class GameRepo {
    private final RoomDataSource roomDataSource;
    private final Gson gson = new Gson();

    public GameRepo(){
        this.roomDataSource = RoomDataSource.getInstance();
    }

    public void startGame(){
        // Start the game
        StartGameCommand command = new StartGameCommand();
        String message = gson.toJson(command);
        roomDataSource.sendMessageToServer(message);
    }
}
