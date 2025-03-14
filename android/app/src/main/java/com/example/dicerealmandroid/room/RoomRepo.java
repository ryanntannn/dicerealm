package com.example.dicerealmandroid.room;

import android.util.Log;

import com.example.dicerealmandroid.DicerealmClient;
import com.example.dicerealmandroid.core.RoomState;


/*
 * Singleton pattern to ensure only 1 instance of RoomRepo exists
 * */
public class RoomRepo {
    private static RoomRepo instance;
    private DicerealmClient dicerealmClient;
    private RoomState roomState;
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
                roomState = dicerealmClient.getRoomState();
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
        return roomState;
    }

    public void leaveRoom(){
        instance = null;
    }

}
