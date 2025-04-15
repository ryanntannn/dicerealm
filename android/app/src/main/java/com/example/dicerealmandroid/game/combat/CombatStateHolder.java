package com.example.dicerealmandroid.game.combat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
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
    private MutableLiveData<Entity> selectedTarget = new MutableLiveData<>();
    private int currentTargetIndex = 0;


    public CombatStateHolder() {
        combatRepo = new CombatRepo();
        playerRepo = new PlayerRepo();
    }

    public LiveData<CombatTurnModal> subscribeCombatLatestTurn() {
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

    public void setInitialTarget() {
        List<Entity> monsters = getMonsters().getValue();
        if (monsters != null && !monsters.isEmpty() && selectedTarget.getValue() == null) {
            selectedTarget.setValue(monsters.get(0));
            currentTargetIndex = 0;
        }
    }

    public void selectNextTarget() {
        List<Entity> monsters = getMonsters().getValue();
        if (monsters != null && !monsters.isEmpty()) {
            currentTargetIndex = (currentTargetIndex + 1) % monsters.size();
            selectedTarget.setValue(monsters.get(currentTargetIndex));
        }
    }

    public LiveData<Entity> getSelectedTarget() {
        return selectedTarget;
    }

    public void performAction(Object action, CombatTurnActionCommand.ActionType actionType, Entity selectedTarget) {
        combatRepo.performAction(action, actionType, selectedTarget);
    }

    public LiveData<List<Entity>> getMonsters(){
        return combatRepo.getMonsters();
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

    public LiveData<Integer> getCurrentRound(){
        return combatRepo.getCurrentRound();
    }

    public String getinitmessage(){return combatRepo.getinitmessage();}

    public LiveData<UUID> getplayerturn(){return combatRepo.getplayerturn();}
}
