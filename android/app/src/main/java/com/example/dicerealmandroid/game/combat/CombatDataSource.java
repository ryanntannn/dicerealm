package com.example.dicerealmandroid.game.combat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dicerealm.core.combat.systems.InitiativeResult;
import com.dicerealm.core.entity.Entity;
import com.dicerealm.core.monster.Monster;

import java.util.List;

/*
 * Singleton pattern to ensure only 1 instance of CombatDataSource exists so that it persists throughout the lifecycle of the app.
 * Separating dialog and combat data
 * */
public class CombatDataSource {
    private static CombatDataSource instance;
    private final MutableLiveData<String> currentTurn = new MutableLiveData<>();
    private final MutableLiveData<List<InitiativeResult>> initiativeResults = new MutableLiveData<>();
    private final MutableLiveData<Entity> monster = new MutableLiveData<>();

    private CombatDataSource(){}

    public static CombatDataSource getInstance(){
        if (instance == null){
            instance = new CombatDataSource();
        }
        return instance;
    }

    public LiveData<String> subscribeLatestTurn(){
        return currentTurn;
    }

    public void updateTurnHistory(String currentTurn){
        this.currentTurn.postValue(currentTurn);
    }


    public LiveData<List<InitiativeResult>> getInitiativeResults(){
        return initiativeResults;
    }

    public void setInitiativeResults(List<InitiativeResult> initiativeResult){
        this.initiativeResults.postValue(initiativeResult);
    }

    public void setMonster(Entity monster){
        this.monster.postValue(monster);
    }

    public LiveData<Entity> getMonster(){
        return monster;
    }
}
