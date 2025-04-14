package com.example.dicerealmandroid.game;

import androidx.lifecycle.LiveData;

import com.dicerealm.core.command.StartGameCommand;
import com.dicerealm.core.locations.Location;
import com.dicerealm.core.locations.LocationGraph;
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
//    public void changeLocation(Location location){
//        if(location.equals(gameDataSource.getCurrentLocation())){
//            return;
//        }
//        gameDataSource.setCurrentLocation(location);
//    }
//
//    public LiveData<Location> subscribeCurrentLocation(){
//        return gameDataSource.subscribeCurrentLocation();
//    }


    // Send user game input text to server
    public void sendTextInput(String text){
        if(text == null || text.isBlank()){
            return;
        }
        roomDataSource.sendMessageToServer(text);
    }

    public void updateCurrentLocation(Location location){
        LocationGraph locationGraph = gameDataSource.getCurrentLocationGraph().getValue();
        if(locationGraph == null) throw new IllegalStateException("LocationGraph is null");
        locationGraph.setCurrentLocation(location);
        updateLocationGraph(locationGraph);
    }

    public void updateLocationGraph(LocationGraph locationGraph){
        gameDataSource.setCurrentLocationGraph(locationGraph);
    }

    public LiveData<LocationGraph> getLocationGraph(){
        return gameDataSource.getCurrentLocationGraph();
    }

}
