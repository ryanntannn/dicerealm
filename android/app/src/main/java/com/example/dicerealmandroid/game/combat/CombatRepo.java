package com.example.dicerealmandroid.game.combat;

import androidx.lifecycle.LiveData;

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
}
