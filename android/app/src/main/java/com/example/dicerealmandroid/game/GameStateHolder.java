package com.example.dicerealmandroid.game;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.dicerealm.core.dialogue.SkillCheck;
import com.dicerealm.core.locations.Location;
import com.example.dicerealmandroid.game.dialog.Dialog;
import com.dicerealm.core.command.ShowPlayerActionsCommand;
import com.dicerealm.core.dm.DungeonMasterResponse;
import com.example.dicerealmandroid.game.dialog.DialogRepo;
import com.example.dicerealmandroid.room.RoomRepo;

import java.util.List;

public class GameStateHolder extends ViewModel {

    private GameRepo gameRepo;
    private DialogRepo dialogRepo;
    private RoomRepo roomRepo;

    public GameStateHolder(){
        gameRepo = new GameRepo();
        dialogRepo = new DialogRepo();
        roomRepo = new RoomRepo();
    }

    public void startGame(){
        gameRepo.startGame();
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

    public LiveData<SkillCheck.ActionResultDetail> subscribeDialogLatestActionResult(){
        return dialogRepo.subscribeLatestActionResult();
    }

    // Location
    public LiveData<Location> subscribeCurrentLocation(){
        return gameRepo.subscribeCurrentLocation();
    }

    // Send text input to server
    public void sendTextInput(String text){
        gameRepo.sendTextInput(text);
    }

}
