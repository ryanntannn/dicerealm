package com.example.dicerealmandroid.game.dialog;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.dicerealm.core.command.ShowPlayerActionsCommand;
import com.dicerealm.core.dialogue.SkillCheck;
import com.dicerealm.core.dm.DungeonMasterResponse;

import java.util.List;

public class DialogStateHolder extends ViewModel {
    private final DialogRepo dialogRepo;


    public DialogStateHolder() {
        // Constructor
        dialogRepo = new DialogRepo();
    }


    // Dialog related methods
    public List<Dialog> getDialogTurnHistory() {
        return dialogRepo.getTurnHistory();
    }

    public LiveData<Dialog> subscribeDialogLatestTurn() {
        return dialogRepo.subscribeLatestTurn();
    }

    public LiveData<ShowPlayerActionsCommand> subscribeDialogPlayerActions() {
        return dialogRepo.subscribePlayerActions();
    }

    public void sendPlayerDialogAction(DungeonMasterResponse.PlayerAction action) {
        dialogRepo.sendPlayerAction(action);
    }

    public int getDialogLatestTurn() {
        return dialogRepo.getLatestTurn();
    }

    public LiveData<SkillCheck.ActionResultDetail> subscribeDialogLatestActionResult() {
        return dialogRepo.subscribeLatestActionResult();
    }
}
