package com.example.dicerealmandroid.game.combat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.dicerealm.core.combat.systems.InitiativeResult;
import com.example.dicerealmandroid.player.PlayerRepo;

import java.util.ArrayList;
import java.util.List;

public class CombatStateHolder extends ViewModel {
    private CombatRepo combatRepo;
    private PlayerRepo playerRepo;

    public CombatStateHolder() {
        // Constructor logic if needed
        combatRepo = new CombatRepo();
        playerRepo = new PlayerRepo();
    }

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

    public void performAction(Object action){
        combatRepo.performAction(action);
    }

}
