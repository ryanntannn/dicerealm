package com.example.dicerealmandroid.game.dialog;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.dicerealmandroid.command.ShowPlayerActionsCommand;

import java.util.LinkedList;
import java.util.List;

/*
 * Singleton pattern to ensure only 1 instance of DialogDataSource exists so that it persists throughout the lifecycle of the app.
 * Separating dialog and combat data
 * */
public class DialogDataSource {
    private static DialogDataSource instance;

    // Turns
    private List<DialogueClass> turnHistory = new LinkedList<>();
    private final MutableLiveData<DialogueClass> currentTurn = new MutableLiveData<>();


    // Player actions
    private MutableLiveData<ShowPlayerActionsCommand> playerActions = new MutableLiveData<>();


    private DialogDataSource(){}

    public static DialogDataSource getInstance(){
        if (instance == null){
            instance = new DialogDataSource();
        }
        return instance;
    }

    public LiveData<DialogueClass> subscribeLatestTurn(){
        return currentTurn;
    }

    public void updateTurnHistory(DialogueClass currentTurn){
        turnHistory.add(currentTurn);
        this.currentTurn.postValue(currentTurn);
    }

    public List<DialogueClass> getTurnHistory(){
        return turnHistory;
    }


    // Player related methods
    public LiveData<ShowPlayerActionsCommand> subscribePlayerActions(){
        return playerActions;
    }
    public ShowPlayerActionsCommand getPlayerActions(){
        return playerActions.getValue();
    }
    public void setPlayerActions(ShowPlayerActionsCommand actions){
        playerActions.postValue(actions);
    }


}
