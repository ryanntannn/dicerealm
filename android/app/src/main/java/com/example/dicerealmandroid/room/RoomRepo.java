package com.example.dicerealmandroid.room;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.dicerealmandroid.DicerealmClient;
import com.dicerealm.core.player.Player;
import com.dicerealm.core.room.RoomState;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;



public class RoomRepo {
    private final RoomDataSource roomDataSource;


    public RoomRepo(){
        roomDataSource = RoomDataSource.getInstance();
    };


    public RoomRepo createRoom(String roomCode){
        try {
            roomDataSource.createRoom(roomCode);
        } catch (Exception e){
            Log.e("error", "Could not connect to room", e);
            return null;
        }
        return this;
    }

    public String getRoomCode(){
        return roomDataSource.getRoomCode();
    }

    public RoomState getRoomState(){
        return roomDataSource.getRoomState().getValue();
    }

    // LiveData: Ensure that the onChanged method is triggered only when the roomState changes from what it was before.
    public LiveData<RoomState> subscribeToRoomState(){
        return roomDataSource.getRoomState();
    }

    public void setRoomState(RoomState roomState){
        if (Objects.equals(this.getRoomState(), roomState)){
            Log.d("RoomRepo", "RoomState is the same, ignoring update.");
            return;
        }
        roomDataSource.setRoomState(roomState);
    }

    // This is to keep track on the number of players currently in the room (client-side)
    public void addRoomStatePlayer(Player player){
        RoomState updatedRoomState = this.getRoomState();
        updatedRoomState.addPlayer(player);
        roomDataSource.setRoomState(updatedRoomState);
    }

    // This is to keep track on the number of players currently in the room (client-side)
    public void removeRoomStatePlayer(String playerId){
        RoomState updatedRoomState = this.getRoomState();
        updatedRoomState.removePlayer(UUID.fromString(playerId));
        roomDataSource.setRoomState(updatedRoomState);
    }

    public void leaveRoom(){
        if(roomDataSource.getDiceRealmClient() == null){
            return;
        }
        roomDataSource.leaveRoom();
    }

    // Room Code input validation: Returns list with boolean and error message
    public ArrayList<String> validateRoomCode(String input){
        ArrayList<String> result = new ArrayList<String>();

        boolean isSingleLine = !input.contains("\n");
        boolean isNotEmpty = !input.isEmpty();

        boolean isValid = isSingleLine && isNotEmpty;
        String errorMessage = null;

        // Display error message if room code is invalid
        if (!isValid){
            if (!isSingleLine) {
                errorMessage = "Room code must be a single line";
            } else if (!isNotEmpty) {
                errorMessage = "Room code cannot be empty";
            }
        }
        result.add(String.valueOf(isValid));
        result.add(errorMessage);
        return result;
    }


    // Server status
    public LiveData<Boolean> isServerActive(){
        return roomDataSource.getServerState();
    }

    public void serverFree(){
        roomDataSource.serverFree();
    }
    public void serverNotFree(){
        roomDataSource.serverNotFree();
    }
}