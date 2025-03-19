package com.example.dicerealmandroid.game;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.dicerealmandroid.command.dialogue.StartTurnCommand;
import com.example.dicerealmandroid.game.dialog.DialogRepo;
import com.example.dicerealmandroid.room.RoomRepo;

import java.util.Queue;

public class GameStateHolder extends ViewModel {

    private GameRepo gameRepo;
    private DialogRepo dialogRepo;

    public GameStateHolder(){
        gameRepo = new GameRepo();
        dialogRepo = new DialogRepo();
    }

    public void startGame(){
        gameRepo.startGame();
    }

    public LiveData<Queue<StartTurnCommand>> subscribeDialogueTurnHistory(){
        return dialogRepo.subscribeTurnHistory();
    }
}
