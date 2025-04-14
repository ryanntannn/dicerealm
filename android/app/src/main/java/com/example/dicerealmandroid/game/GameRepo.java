package com.example.dicerealmandroid.game;

import androidx.lifecycle.LiveData;

import com.dicerealm.core.command.StartGameCommand;
import com.dicerealm.core.locations.Location;
import com.example.dicerealmandroid.Color_hashmap.Colorhashmap;
import com.example.dicerealmandroid.room.RoomDataSource;
import com.google.gson.Gson;

import java.util.UUID;

public class GameRepo {
    private final RoomDataSource roomDataSource;
    private final GameDataSource gameDataSource;
    private final Gson gson = new Gson();

    private final Colorhashmap colorhashmap;
    public GameRepo(){
        this.colorhashmap = new Colorhashmap();
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
    public void changeLocation(Location location){
        if(location.equals(gameDataSource.getCurrentLocation())){
            return;
        }
        gameDataSource.setCurrentLocation(location);
    }

    public LiveData<Location> subscribeCurrentLocation(){
        return gameDataSource.subscribeCurrentLocation();
    }

    public void setplayercolor(UUID uuid){
        colorhashmap.getColor(uuid);
    }

    public String getplayercolor(UUID uuid){return colorhashmap.getColor(uuid);}

    // Send user game input text to server
    public void sendTextInput(String text){
        if(text == null || text.isBlank()){
            return;
        }
        roomDataSource.sendMessageToServer(text);
    }

}
