package com.example.dicerealmandroid.game.combat;

import androidx.lifecycle.LiveData;

import com.dicerealm.core.combat.systems.InitiativeResult;

import java.util.List;
import java.util.Objects;

public class CombatRepo {
    private final CombatDataSource combatDataSource;

    public CombatRepo(){
        combatDataSource = CombatDataSource.getInstance();
    }

    public LiveData<String> subscribeLatestTurn(){
        return combatDataSource.subscribeLatestTurn();
    }

    public void setLatestTurn(String currentTurn){
        combatDataSource.updateTurnHistory(currentTurn);
    }


    public LiveData<List<InitiativeResult>> getInitiativeResults(){
        return combatDataSource.getInitiativeResults();
    }

    public void setInitiativeResults(List<InitiativeResult> initiativeResults) throws IllegalArgumentException{
        if (initiativeResults == null || initiativeResults.isEmpty()){
            throw new IllegalArgumentException("InitiativeResults cannot be null");
        }
        else if (!Objects.equals(initiativeResults, combatDataSource.getInitiativeResults())){
            combatDataSource.setInitiativeResults(initiativeResults);
        }
    }

    // Rotate the combat sequence
    public void rotateCombatSequence(){
        List<InitiativeResult> initiativeResults = this.getInitiativeResults().getValue();
        if(initiativeResults != null){
            InitiativeResult first = initiativeResults.remove(0);
            initiativeResults.add(first);
        }
        combatDataSource.setInitiativeResults(initiativeResults);
    }
}
