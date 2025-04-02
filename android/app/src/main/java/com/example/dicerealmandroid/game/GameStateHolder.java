package com.example.dicerealmandroid.game;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.dicerealm.core.combat.systems.InitiativeResult;
import com.dicerealm.core.dialogue.SkillCheck;
import com.dicerealm.core.locations.Location;
import com.example.dicerealmandroid.game.combat.CombatRepo;
import com.example.dicerealmandroid.game.combat.CombatSequence;
import com.example.dicerealmandroid.game.dialog.Dialog;
import com.dicerealm.core.command.ShowPlayerActionsCommand;
import com.dicerealm.core.dm.DungeonMasterResponse;
import com.example.dicerealmandroid.game.dialog.DialogRepo;
import com.example.dicerealmandroid.player.PlayerRepo;
import com.example.dicerealmandroid.room.RoomRepo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class GameStateHolder extends ViewModel {

    // TODO: Move combat and dialog related methods to their own state holders (if got time)

    private GameRepo gameRepo;
    private DialogRepo dialogRepo;
    private RoomRepo roomRepo;
    private CombatRepo combatRepo;
    private PlayerRepo playerRepo;

    public GameStateHolder() {
        gameRepo = new GameRepo();
        dialogRepo = new DialogRepo();
        roomRepo = new RoomRepo();
        combatRepo = new CombatRepo();
        playerRepo = new PlayerRepo();
    }

    public void startGame() {
        gameRepo.startGame();
    }

    // Combat related methods
    public LiveData<String> subscribeCombatLatestTurn() {
        return combatRepo.subscribeLatestTurn();
    }


    public LiveData<List<CombatSequence>> getCombatSequence(){
        // Only used for UI display only, so placed here instead of in the repo
        // The CombatSequence class is a filtered version of InitiativeResult
        return Transformations.map(combatRepo.getInitiativeResults(), initiativeResults -> {
            List<CombatSequence> combatSequence = new ArrayList<>();
            for(InitiativeResult initiativeResult : initiativeResults){
                String name = initiativeResult.getEntity().getDisplayName();
                int totalInitiative = initiativeResult.getTotalInitiative();
                if(initiativeResult.getEntity().getId().equals(playerRepo.getPlayerId())){
                    name = "You";
                }
                combatSequence.add(new CombatSequence(name, totalInitiative));
            }
            return combatSequence;
        });
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

    public int[] getStatsIds() {
        return gameRepo.getStatsIds();
    }

    public LiveData<SkillCheck.ActionResultDetail> subscribeDialogLatestActionResult() {
        return dialogRepo.subscribeLatestActionResult();
    }

    // Location
    public LiveData<Location> subscribeCurrentLocation() {
        return gameRepo.subscribeCurrentLocation();
    }

    // Send text input to server
    public void sendTextInput(String text) {
        gameRepo.sendTextInput(text);
    }
}
