package com.example.dicerealmandroid.game.dialog;

import androidx.lifecycle.LiveData;

import com.example.dicerealmandroid.command.dialogue.StartTurnCommand;

import java.util.List;
import java.util.Queue;

public class DialogRepo {
    private final DialogDataSource dialogDataSource;

    public DialogRepo(){
        this.dialogDataSource = DialogDataSource.getInstance();
    }


    public LiveData<StartTurnCommand> subscribeLatestTurn(){
        return dialogDataSource.subscribeLatestTurn();
    }

    public List<StartTurnCommand> getTurnHistory(){
        return dialogDataSource.getTurnHistory();
    }

    public void updateHistory(StartTurnCommand turn){
        dialogDataSource.updateTurnHistory(turn);
    }
}
