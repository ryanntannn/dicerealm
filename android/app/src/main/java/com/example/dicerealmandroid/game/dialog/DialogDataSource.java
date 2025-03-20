package com.example.dicerealmandroid.game.dialog;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.dicerealmandroid.command.dialogue.StartTurnCommand;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/*
 * Singleton pattern to ensure only 1 instance of DialogDataSource exists so that it persists throughout the lifecycle of the app.
 * Seperate dialog and combat data
 * */
public class DialogDataSource {
    private static DialogDataSource instance;

    private List<StartTurnCommand> turnHistory = new LinkedList<>();
    private MutableLiveData<StartTurnCommand> currentTurn = new MutableLiveData<>();
    private boolean turnEnd = false;
    private DialogDataSource(){}

    public static DialogDataSource getInstance(){
        if (instance == null){
            instance = new DialogDataSource();
        }
        return instance;
    }

    public LiveData<StartTurnCommand> subscribeLatestTurn(){
        return currentTurn;
    }

    public void updateTurnHistory(StartTurnCommand currentTurn){
        turnHistory.add(currentTurn);
        this.currentTurn.postValue(currentTurn);
    }

    public List<StartTurnCommand> getTurnHistory(){
        return turnHistory;
    }
}
