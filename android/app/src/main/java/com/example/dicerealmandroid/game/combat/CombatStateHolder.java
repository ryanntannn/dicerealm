package com.example.dicerealmandroid.game.combat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.dicerealm.core.combat.systems.InitiativeResult;
import com.dicerealm.core.command.combat.CombatTurnActionCommand;
import com.dicerealm.core.entity.Entity;
import com.dicerealm.core.monster.Monster;
import com.example.dicerealmandroid.player.PlayerRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CombatStateHolder extends ViewModel {
    private CombatRepo combatRepo;
    private PlayerRepo playerRepo;

    public CombatStateHolder() {
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
            if(initiativeResults == null) return combatSequence;

            for(InitiativeResult initiativeResult : initiativeResults){
                String name = initiativeResult.getEntity().getDisplayName();
                int totalInitiative = initiativeResult.getTotalInitiative();
                int health = initiativeResult.getEntity().getHealth();
                UUID entity_uuid = initiativeResult.getEntity().getId();
                combatSequence.add(new CombatSequence(name, totalInitiative, health, entity_uuid));
            }
            return combatSequence;
        });
    }

    public void performAction(Object action, CombatTurnActionCommand.ActionType actionType){
        combatRepo.performAction(action, actionType);
    }

    public LiveData<Entity> getMonster(){
        return combatRepo.getMonster();
    }

    public List<InitiativeResult> initiativeResults(){
        return combatRepo.getInitiativeResults().getValue();
    }

    public Boolean isMyTurn(){
        List<InitiativeResult> initiativeResults = combatRepo.getInitiativeResults().getValue();
        if(initiativeResults != null || !initiativeResults.isEmpty()){
            UUID currPlayer = initiativeResults.get(0).getEntity().getId();
            if(currPlayer.equals(playerRepo.getPlayerId())){
                return true;
            }
        }
        return false;
    }
}
