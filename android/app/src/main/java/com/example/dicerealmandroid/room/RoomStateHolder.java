package com.example.dicerealmandroid.room;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.dicerealmandroid.core.Player;
import com.example.dicerealmandroid.core.RoomState;

public class RoomStateHolder extends ViewModel {
    private RoomRepo roomRepo;

    public RoomStateHolder(){
        roomRepo = RoomRepo.getInstance();
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

    public MutableLiveData<Player[]> trackAllPlayers(){
        // Downcast from LiveData to MutableLiveData
        return (MutableLiveData<Player[]>) Transformations.map(roomRepo.subscribeToRoomState(), RoomState::getPlayers);
    }

    public RoomStateHolder createRoom(String roomCode){
        roomRepo.createRoom(roomCode);
        return this;
    }
}