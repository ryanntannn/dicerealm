package com.example.dicerealmandroid.room;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.dicerealmandroid.DicerealmClient;
import com.example.dicerealmandroid.core.player.Player;
import com.example.dicerealmandroid.core.RoomState;

import java.util.Objects;
import java.util.UUID;


/*
 * Singleton pattern to ensure only 1 instance of RoomRepo exists
 * */
public class RoomRepo {
    private static RoomRepo instance;
    private DicerealmClient dicerealmClient;
    private final MutableLiveData<RoomState> roomState = new MutableLiveData<>();
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
        return roomState.getValue();
    }

    // LiveData: Ensure that the onChanged method is triggered only when the roomState changes from what it was before.
    public LiveData<RoomState> subscribeToRoomState(){
        return roomState;
    }

    public void setRoomState(RoomState roomState){
        if (Objects.equals(this.roomState.getValue(), roomState)){
            Log.d("RoomRepo", "RoomState is the same, ignoring update.");
            return;
        }
        this.roomState.postValue(roomState);
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
        if(dicerealmClient == null || instance == null){
            return;
        }
        dicerealmClient.close(1000, 1000, "Leaving room");
        dicerealmClient = null;
    }

    public DicerealmClient getDicerealmClient() {
        return dicerealmClient;
    }
}