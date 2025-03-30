package com.example.dicerealmandroid.room;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.dicerealm.core.player.Player;
import com.dicerealm.core.room.RoomState;

import java.util.ArrayList;

public class RoomStateHolder extends ViewModel {
    private RoomRepo roomRepo;

    public RoomStateHolder(){
        roomRepo = new RoomRepo();
    }

    public String getRoomCode() {
        return roomRepo.getRoomCode();
    }

    public RoomState.State getRoomState(){
        if (roomRepo.getRoomState() == null){
            return null;
        }
        return roomRepo.getRoomState().getState();
    }

    public Player[] getAllPlayers(){
        return roomRepo.getRoomState().getPlayers();
    }

    public LiveData<Player[]> trackAllPlayers(){
        // Observe changes in roomState and return the players.
        // If roomState changes but players don't, the observer will not be triggered.
        return Transformations.map(roomRepo.subscribeToRoomState(), RoomState::getPlayers);
    }

    public RoomStateHolder createRoom(String roomCode){
        roomRepo.createRoom(roomCode);
        return this;
    }

    public void leaveRoom(){
        roomRepo.leaveRoom();
    }

    public ArrayList<String> validateRoomCode(String roomcode){
        return roomRepo.validateRoomCode(roomcode);
    }

    public LiveData<Boolean> isServerActive(){
        return roomRepo.isServerActive();
    }

}