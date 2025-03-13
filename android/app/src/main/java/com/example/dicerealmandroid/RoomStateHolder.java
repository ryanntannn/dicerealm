package com.example.dicerealmandroid;

import androidx.lifecycle.ViewModel;

import com.example.dicerealmandroid.core.DicerealmClientSingleton;
import com.example.dicerealmandroid.core.Player;
import com.example.dicerealmandroid.core.RoomState;

public class RoomStateHolder extends ViewModel {
    private String roomCode;


    public String getRoomCode() {
        return DicerealmClientSingleton.getInstance().getRoomCode();
    }

    public RoomState.State getRoomState(){
        return DicerealmClientSingleton.getInstance().getRoomState().getState();
    }

    public Player[] getAllPlayers(){
        return DicerealmClientSingleton.getInstance().getRoomState().getPlayers();
    }

    public DicerealmClient createRoom(String roomCode){
        this.roomCode = roomCode;
        return DicerealmClientSingleton.createInstance(roomCode);
    }
}
