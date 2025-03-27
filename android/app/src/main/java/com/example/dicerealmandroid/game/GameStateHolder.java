package com.example.dicerealmandroid.game;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.dicerealmandroid.game.dialog.Dialog;
import com.dicerealm.core.command.ShowPlayerActionsCommand;
import com.dicerealm.core.dm.DungeonMasterResponse;
import com.example.dicerealmandroid.game.dialog.DialogRepo;

import java.util.List;

public class GameStateHolder extends ViewModel {

    private GameRepo gameRepo;
    private DialogRepo dialogRepo;

    private long timeLeftMillis = 30000;
    private long interval = 1000;

    public GameStateHolder(){
        gameRepo = new GameRepo();
        dialogRepo = new DialogRepo();
    }

    public void startGame(){
        gameRepo.startGame();
    }

    public LiveData<Boolean> isGameRunning(){
        return gameRepo.isGameRunning();
    }




    // Dialog related methods
    public List<Dialog> getDialogTurnHistory(){
        return dialogRepo.getTurnHistory();
    }

    public LiveData<Dialog> subscribeDialogLatestTurn(){
        return dialogRepo.subscribeLatestTurn();
    }

    public LiveData<ShowPlayerActionsCommand> subscribeDialogPlayerActions(){
        return dialogRepo.subscribePlayerActions();
    }

    public void sendPlayerDialogAction(DungeonMasterResponse.PlayerAction action){
        dialogRepo.sendPlayerAction(action);
    }

    public int getDialogLatestTurn(){
        return dialogRepo.getLatestTurn();
    }



    // Timer related methods
    public long getTimeLeftInMillis(){
        // Reset the timer if it reaches 0
        if(timeLeftMillis <= 0){
            timeLeftMillis = 30000;
        }
        return timeLeftMillis;
    }

    public void setTimeLeftInMillis(long timeLeftMillis){
        this.timeLeftMillis = timeLeftMillis;
    }

    public long getIntervalInMillis(){
        return interval;
    }


    // Turn related methods
    public LiveData<Boolean> isServerBusy(){
        return gameRepo.isServerBusy();
    }
}
