package com.example.dicerealmandroid.game.dialog;

import androidx.lifecycle.LiveData;

import com.dicerealm.core.command.ShowPlayerActionsCommand;
import com.dicerealm.core.command.dialogue.DialogueTurnActionCommand;
import com.dicerealm.core.dm.DungeonMasterResponse;
import com.dicerealm.core.entity.Entity;
import com.dicerealm.core.entity.StatsMap;
import com.example.dicerealmandroid.player.PlayerDataSource;
import com.example.dicerealmandroid.room.RoomDataSource;
import com.google.gson.Gson;

import java.util.List;
import java.util.UUID;

public class DialogRepo {
    private final DialogDataSource dialogDataSource;
    private final RoomDataSource roomDataSource;
    private final PlayerDataSource playerDataSource;
    private final Gson gson = new Gson();

    public DialogRepo(){
        this.dialogDataSource = DialogDataSource.getInstance();
        this.roomDataSource = RoomDataSource.getInstance();
        this.playerDataSource = PlayerDataSource.getInstance();
    }


    public LiveData<Dialog> subscribeLatestTurn(){
        return dialogDataSource.subscribeLatestTurn();
    }

    public List<Dialog> getTurnHistory(){
        return dialogDataSource.getTurnHistory();
    }

    public int getLatestTurn(){
        return this.getTurnHistory().get(this.getTurnHistory().size() - 1).getTurnNumber();
    }


    public void updateTurnHistory(Dialog currentTurn){
        dialogDataSource.updateTurnHistory(currentTurn);
    }

    // Player related methods
    public LiveData<ShowPlayerActionsCommand> subscribePlayerActions(){
        return dialogDataSource.subscribePlayerActions();
    }
    public void setPlayerActions(ShowPlayerActionsCommand actions){
        dialogDataSource.setPlayerActions(actions);
    }

    public void sendPlayerAction(DungeonMasterResponse.PlayerAction action){
        StatsMap playerStats = playerDataSource.getPlayer().getValue().getStats();
        int turn = dialogDataSource.subscribeLatestTurn().getValue().getTurnNumber();

        DialogueTurnActionCommand dialogAction = new DialogueTurnActionCommand(turn, UUID.fromString(action.playerId), action.action, playerStats);
        String message = gson.toJson(dialogAction);
        roomDataSource.sendMessageToServer(message);
    }


}
