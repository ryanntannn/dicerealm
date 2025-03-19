package com.example.dicerealmandroid.game.dialog;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.dicerealmandroid.command.dialogue.StartTurnCommand;

import java.util.LinkedList;
import java.util.Queue;

/*
 * Singleton pattern to ensure only 1 instance of DialogDataSource exists so that it persists throughout the lifecycle of the app.
 * Seperate dialog and combat data
 * */
public class DialogDataSource {
    private static DialogDataSource instance;

    private MutableLiveData<Queue<StartTurnCommand>> turnHistory = new MutableLiveData<>(new LinkedList<>());
    private boolean turnEnd = false;
    private DialogDataSource(){}

    public static DialogDataSource getInstance(){
        if (instance == null){
            instance = new DialogDataSource();
        }
        return instance;
    }

    public LiveData<Queue<StartTurnCommand>> subscribeTurnHistory(){
        return turnHistory;
    }

    public void addNewTurn(StartTurnCommand currentTurn){
        Queue<StartTurnCommand> currentHistory = this.turnHistory.getValue();
        currentHistory.add(currentTurn);
        this.turnHistory.postValue(currentHistory);
    }
}
