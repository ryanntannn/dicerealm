package com.example.dicerealmandroid.game.dialog;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dicerealm.core.command.ShowPlayerActionsCommand;
import com.dicerealm.core.dialogue.SkillCheck;

import java.util.LinkedList;
import java.util.List;

/*
 * Singleton pattern to ensure only 1 instance of DialogDataSource exists so that it persists throughout the lifecycle of the app.
 * Separating dialog and combat data
 * */
public class DialogDataSource {
    private static DialogDataSource instance;

    // Turns
    private List<Dialog> turnHistory = new LinkedList<>();
    private final MutableLiveData<Dialog> currentTurn = new MutableLiveData<>();


    // Player actions
    private MutableLiveData<ShowPlayerActionsCommand> playerActions = new MutableLiveData<>();

    private final MutableLiveData<SkillCheck.ActionResultDetail> currentActionResult = new MutableLiveData<>();

    private DialogDataSource(){}

    public static DialogDataSource getInstance(){
        if (instance == null){
            instance = new DialogDataSource();
        }
        return instance;
    }

    public LiveData<Dialog> subscribeLatestTurn(){
        return currentTurn;
    }

    public void updateTurnHistory(Dialog currentTurn){
        //turnHistory.add(currentTurn);
        this.currentTurn.postValue(currentTurn);
    }

    public List<Dialog> getTurnHistory(){
        return turnHistory;
    }

    public LiveData<SkillCheck.ActionResultDetail> subscribeLatestActionResult(){
        return currentActionResult;
    }

    public void setLatestActionResult(SkillCheck.ActionResultDetail actionResult){
        currentActionResult.postValue(actionResult);
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



    // Reset all cache data while leaving the singleton instance
    // Maintain observers connections :>
    public static void destroy(){
        if(instance != null){
            instance.turnHistory = null;
            instance.currentTurn.postValue(null);
            instance.setPlayerActions(null);
            instance.setLatestActionResult(null);
        }
    }
}
