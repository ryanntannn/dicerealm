package com.example.dicerealmandroid.room;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.dicerealmandroid.DicerealmClient;
import com.example.dicerealmandroid.core.Player;
import com.example.dicerealmandroid.core.RoomState;

import java.util.Objects;
import java.util.UUID;


/*
 * Singleton pattern to ensure only 1 instance of RoomRepo exists
 * */
public class RoomRepo {
    private static RoomRepo instance;
    private DicerealmClient dicerealmClient;
    private MutableLiveData<RoomState> roomState;
    private RoomRepo(){};

    public static RoomRepo getInstance(){
        if (instance == null){
            instance = new RoomRepo();
        }
        return instance;
    }

    public RoomRepo createRoom(String roomCode){
        if (dicerealmClient == null){
            try {
                dicerealmClient = new DicerealmClient(roomCode);
            } catch (Exception e){
                Log.e("error", "Could not connect to room", e);
                return null;
            }
        }
        return instance;
    }

    public String getRoomCode(){
        return dicerealmClient.getRoomCode();
    }

    public RoomState getRoomState(){
        if(roomState == null){
            return null;
        }
        return roomState.getValue();
    }

    public MutableLiveData<RoomState> subscribeToRoomState(){
        // Only Initializing it when needed, otherwise null
        if(roomState == null){
            roomState = new MutableLiveData<RoomState>(new RoomState());
        }
        return roomState;
    }

    public void setRoomState(RoomState roomState){
        if(this.roomState == null){
            this.roomState = new MutableLiveData<RoomState>(roomState);
        }else{
            if (Objects.equals(this.roomState.getValue(), roomState)){
                Log.d("RoomRepo", "RoomState is the same, ignoring update.");
                return;
            }
            this.roomState.postValue(roomState);
        }
    }

    // This is to keep track on the number of players currently in the room (client-side)
    public void addRoomStatePlayer(Player player){
        RoomState updatedRoomState = this.roomState.getValue();
        updatedRoomState.addPlayer(player);
        this.roomState.postValue(updatedRoomState); // Trigger observers
    }

    // This is to keep track on the number of players currently in the room (client-side)
    public void removeRoomStatePlayer(String playerId){
        RoomState updatedRoomState = this.roomState.getValue();
        updatedRoomState.removePlayer(UUID.fromString(playerId));
        this.roomState.postValue(updatedRoomState); // Trigger observers
    }


    public void leaveRoom(){
        instance = null;
    }

}