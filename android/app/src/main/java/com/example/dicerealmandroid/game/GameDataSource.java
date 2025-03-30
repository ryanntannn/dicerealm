package com.example.dicerealmandroid.game;

/*
 * Singleton pattern to ensure only 1 instance of DialogDataSource exists so that it persists throughout the lifecycle of the app.
 * Separating dialog and combat data
 * */

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.dicerealmandroid.game.dialog.DialogDataSource;

public class GameDataSource {
    private static GameDataSource instance;
//    private MutableLiveData<Boolean> isGameReady = new MutableLiveData<>(null);
    private GameDataSource(){}


    public static GameDataSource getInstance(){
        if (instance == null){
            instance = new GameDataSource();
        }
        return instance;
    }

//    public LiveData<Boolean> isGameReady(){
//        return isGameReady;
//    }
//
//    public void gameStarted(){
//        isGameReady.postValue(true);
//    }

}
