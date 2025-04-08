package com.example.dicerealmandroid.game.combat;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.dicerealm.core.combat.systems.InitiativeResult;
import com.dicerealm.core.command.combat.CombatTurnActionCommand;
import com.dicerealm.core.entity.Entity;
import com.dicerealm.core.monster.Monster;
import com.dicerealm.core.player.Player;
import com.dicerealm.core.skills.Skill;
import com.example.dicerealmandroid.player.PlayerDataSource;
import com.example.dicerealmandroid.room.RoomDataSource;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CombatRepo {
    private final CombatDataSource combatDataSource;
    private final PlayerDataSource playerDataSource;
    private final RoomDataSource roomDataSource;
    private final Gson gson = new Gson();

    public CombatRepo(){
        combatDataSource = CombatDataSource.getInstance();
        playerDataSource = PlayerDataSource.getInstance();
        roomDataSource = RoomDataSource.getInstance();
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
            // get monster details
            for(InitiativeResult initiativeResult : initiativeResults){
                if(initiativeResult.getEntity().getAllegiance() == Entity.Allegiance.ENEMY){
                    combatDataSource.setMonster(initiativeResult.getEntity());
                }
            }

            // Set the initiative results
            combatDataSource.setInitiativeResults(initiativeResults);
        }
    }

    // action represents the sub-classes of Item, Skill or Weapon
    public void performAction(Object action, CombatTurnActionCommand.ActionType actionType){
        Player attacker = playerDataSource.getPlayer().getValue();
        Entity target = combatDataSource.getMonster().getValue();
        if (attacker == null || target == null) {
            throw new IllegalArgumentException("Player or Monster cannot be null");
        }

        CombatTurnActionCommand command = new CombatTurnActionCommand(attacker, target, action, actionType);
        String message = gson.toJson(command);
        roomDataSource.sendMessageToServer(message);
    }



    // Rotate the combat sequence, showing the next player in line
    public void rotateCombatSequence(){
        List<InitiativeResult> initiativeResults =this.getInitiativeResults().getValue();
        if(initiativeResults != null && !initiativeResults.isEmpty()){
            InitiativeResult first = initiativeResults.remove(0);
            initiativeResults.add(first);
        }
        combatDataSource.setInitiativeResults(initiativeResults);
    }


    public LiveData<Entity> getMonster(){
        return combatDataSource.getMonster();
    }


    public void takeDamage(UUID targetId, int damage){
        UUID playerId = playerDataSource.getPlayerId();
        UUID enemyId = getMonster().getValue().getId();

        Entity targetEntity;

        // If target is the player (you) or enemy
        if (playerId.equals(targetId)){
            targetEntity = playerDataSource.getPlayer().getValue();
            targetEntity.takeDamage(damage);
            playerDataSource.setPlayer((Player) targetEntity);
        }
        else if (enemyId.equals(targetId)){
            targetEntity = getMonster().getValue();
            targetEntity.takeDamage(damage);
            combatDataSource.setMonster(targetEntity);
        }
    }

}
