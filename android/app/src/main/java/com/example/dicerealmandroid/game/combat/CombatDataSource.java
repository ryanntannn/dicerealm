package com.example.dicerealmandroid.game.combat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dicerealm.core.combat.systems.InitiativeResult;
import com.dicerealm.core.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/*
 * Singleton pattern to ensure only 1 instance of CombatDataSource exists so that it persists throughout the lifecycle of the app.
 * Separating dialog and combat data
 * */
public class CombatDataSource {
    private static CombatDataSource instance;
    private final MutableLiveData<CombatTurnModal> currentTurn = new MutableLiveData<>();
    private final MutableLiveData<List<InitiativeResult>> initiativeResults = new MutableLiveData<>();
    private final MutableLiveData<List<Entity>> monsters = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Integer> currentRound = new MutableLiveData<>(1);
    private Integer prevRound = 0;

    private CombatDataSource(){}

    public static CombatDataSource getInstance(){
        if (instance == null){
            instance = new CombatDataSource();
        }
        return instance;
    }

    public LiveData<CombatTurnModal> subscribeLatestTurn(){
        return currentTurn;
    }

    public void updateTurnHistory(CombatTurnModal currentTurn){
        this.currentTurn.postValue(currentTurn);
    }


    public LiveData<List<InitiativeResult>> getInitiativeResults(){
        return initiativeResults;
    }

    public void setInitiativeResults(List<InitiativeResult> initiativeResult){
        this.initiativeResults.postValue(initiativeResult);
    }

    public void setMonsters(List<Entity> monsterList) {
        this.monsters.postValue(monsterList);
    }
    public void setMonster(Entity updatedMonster) {
        List<Entity> currentMonsters = monsters.getValue();
        if (currentMonsters != null) {
            for (int i = 0; i < currentMonsters.size(); i++) {
                if (currentMonsters.get(i).getId().equals(updatedMonster.getId())) {
                    currentMonsters.set(i, updatedMonster); // Replace the existing monster
                    break;
                }
            }
            monsters.postValue(currentMonsters); // Update LiveData
        } else {
            List<Entity> newMonsters = new ArrayList<>();
            newMonsters.add(updatedMonster);
            monsters.postValue(newMonsters);
        }
    }

    public Entity getMonster(UUID monsterId) {
        List<Entity> currentMonsters = monsters.getValue();
        if (currentMonsters != null) {
            for (Entity monster : currentMonsters) {
                if (monster.getId().equals(monsterId)) {
                    return monster; // Return the matching monster
                }
            }
        }
        return null;
    }

    public LiveData<List<Entity>> getMonsters() {
        return monsters;
    }

    public LiveData<Integer> getCurrentRound(){
        return currentRound;
    }

    public void setCurrentRound(int round){
        this.currentRound.postValue(round);
    }

    public int getPrevRound(){
        return prevRound;
    }
    public void setPrevRound(int round){
        this.prevRound = round;
    }


    // Reset all cache data while leaving the singleton instance
    // Maintain observers connections :>
    public static void destroy(){
        if(instance != null){
            instance.setMonsters(null);
            instance.setInitiativeResults(null);
            instance.updateTurnHistory(null);
            instance.setCurrentRound(1);
            instance.setPrevRound(0);
        }
    }
}
