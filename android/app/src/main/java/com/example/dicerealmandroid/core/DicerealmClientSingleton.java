package com.example.dicerealmandroid.core;


import android.util.Log;

import com.example.dicerealmandroid.DicerealmClient;

import java.net.URISyntaxException;

/*
* Follows a singleton pattern
* Stores a server instance, providing a global access point to it
* */
public class DicerealmClientSingleton {
    private static DicerealmClient dicerealmClient;

    private DicerealmClientSingleton(){}

    public static DicerealmClient createInstance(String roomCode){
        if (dicerealmClient != null){
            Log.e("error", "Server already initialized");
            return dicerealmClient;
        }

        try{
            dicerealmClient = new DicerealmClient(roomCode);
            return dicerealmClient;
        } catch (URISyntaxException e){
            Log.e("error", "Could not connect to room", e);
            return null;
        }
    }

    public static DicerealmClient getInstance(){
        if (dicerealmClient == null){
            Log.e("Server", "Server not initialized");
            return null;
        }
        return dicerealmClient;
    }


    public static void clearInstance(){
        dicerealmClient = null;
    }
}
