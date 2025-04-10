package com.example.dicerealmandroid.room;


import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.dicerealmandroid.DicerealmClient;
import com.dicerealm.core.room.RoomState;

import java.net.URISyntaxException;
import java.util.Objects;
import java.util.UUID;

/*
 * Singleton pattern to ensure only 1 instance of RoomDataSource exists so that data persistence is maintained throughout lifecycle of the app.
 * */
public class RoomDataSource {

    private static RoomDataSource instance;
    private final MutableLiveData<RoomState> roomState = new MutableLiveData<>();
    private DicerealmClient dicerealmClient;

    private RoomDataSource(){}

    public static RoomDataSource getInstance(){
        if (instance == null){
            instance = new RoomDataSource();
        }
        return instance;
    }

    public LiveData<RoomState> getRoomState(){
        return roomState;
    }

    public String getRoomCode(){
        return dicerealmClient.getRoomCode();
    }

    public void setRoomState(RoomState roomState){
        this.roomState.postValue(roomState); // Trigger observers
    }

    public void changeState(RoomState.State state){
        RoomState currentRoomState = this.roomState.getValue();
        if (currentRoomState != null) {
            currentRoomState.setState(state);
            this.roomState.postValue(currentRoomState);
        }
    }


    public void createRoom(String roomCode) throws URISyntaxException {
        dicerealmClient = new DicerealmClient(roomCode);
    }

    public void leaveRoom(){
        dicerealmClient.close(1000, 1000, "Leaving room");
    }

    public DicerealmClient getDiceRealmClient(){
        return dicerealmClient;
    }

    public void removeServerInstance(){
        this.dicerealmClient = null;
    }

    public void sendMessageToServer(String message){
        dicerealmClient.send(message);
    }


    // Reset all cache data while leaving the singleton instance
    // Maintain observers connections :>
    public static void destroy(){
        if(instance != null){
            instance.setRoomState(null);
            instance.removeServerInstance();
        }
    }
}