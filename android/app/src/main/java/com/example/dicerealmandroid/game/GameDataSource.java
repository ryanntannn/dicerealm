package com.example.dicerealmandroid.game;

/*
 * Singleton pattern to ensure only 1 instance of DialogDataSource exists so that it persists throughout the lifecycle of the app.
 * Separating dialog and combat data
 * */

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dicerealm.core.locations.LocationGraph;
import com.example.dicerealmandroid.R;
import com.dicerealm.core.locations.Location;

public class GameDataSource {
    private static GameDataSource instance;
//    private final MutableLiveData<Location> currentLocation = new MutableLiveData<>();
    private final MutableLiveData<LocationGraph> currentLocationGraph = new MutableLiveData<>();

    private GameDataSource(){}


    public static GameDataSource getInstance(){
        if (instance == null){
            instance = new GameDataSource();
        }
        return instance;
    }

    public static final int[] statsIdArray = {
            R.id.stat_armourclass,
            R.id.stat_charisma,
            R.id.stat_constitution,
            R.id.stat_dexterity,
            R.id.stat_intelligence,
            R.id.stat_strength,
            R.id.stat_wisdom
    };

//    public LiveData<Location> subscribeCurrentLocation(){
//        return currentLocation;
//    }
//
//    public Location getCurrentLocation(){
//        return currentLocation.getValue();
//    }
//
//    public void setCurrentLocation(Location location){
//        currentLocation.postValue(location);
//    }

    public void setCurrentLocationGraph(LocationGraph locationGraph){
        currentLocationGraph.postValue(locationGraph);
    }

    public LiveData<LocationGraph> getCurrentLocationGraph(){
        return currentLocationGraph;
    }


    // Destroy the singleton instance
    public static void destroy(){
        if(instance != null){
            instance.setCurrentLocationGraph(null);
//            instance.setCurrentLocation(null);
        }
    }
}
