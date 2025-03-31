package com.example.dicerealmandroid.game;

/*
 * Singleton pattern to ensure only 1 instance of DialogDataSource exists so that it persists throughout the lifecycle of the app.
 * Separating dialog and combat data
 * */

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dicerealm.core.locations.Location;
import com.example.dicerealmandroid.game.dialog.DialogDataSource;

public class GameDataSource {
    private static GameDataSource instance;
    private final MutableLiveData<Location> currentLocation = new MutableLiveData<>();

    private GameDataSource(){}


    public static GameDataSource getInstance(){
        if (instance == null){
            instance = new GameDataSource();
        }
        return instance;
    }

    public LiveData<Location> subscribeCurrentLocation(){
        return currentLocation;
    }

    public Location getCurrentLocation(){
        return currentLocation.getValue();
    }

    public void setCurrentLocation(Location location){
        currentLocation.postValue(location);
    }
}
