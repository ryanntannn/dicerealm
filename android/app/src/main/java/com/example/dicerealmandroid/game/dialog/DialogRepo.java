package com.example.dicerealmandroid.game.dialog;

import androidx.lifecycle.LiveData;

import com.example.dicerealmandroid.command.dialogue.StartTurnCommand;

import java.util.Queue;

public class DialogRepo {
    private final DialogDataSource dialogDataSource;

    public DialogRepo(){
        this.dialogDataSource = DialogDataSource.getInstance();
    }

    public void addNewTurn(StartTurnCommand message){
        dialogDataSource.addNewTurn(message);
    }

    public LiveData<Queue<StartTurnCommand>> subscribeTurnHistory(){
        return dialogDataSource.subscribeTurnHistory();
    }
}
