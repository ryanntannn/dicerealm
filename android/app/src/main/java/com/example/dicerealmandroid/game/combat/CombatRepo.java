package com.example.dicerealmandroid.game.combat;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.dicerealm.core.combat.systems.InitiativeResult;
import com.dicerealm.core.command.combat.CombatTurnActionCommand;
import com.dicerealm.core.entity.Entity;
import com.dicerealm.core.monster.Monster;
import com.dicerealm.core.player.Player;
import com.dicerealm.core.room.RoomState;
import com.dicerealm.core.skills.Skill;
import com.example.dicerealmandroid.player.PlayerDataSource;
import com.example.dicerealmandroid.room.RoomDataSource;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public LiveData<CombatTurnModal> subscribeLatestTurn(){
        return combatDataSource.subscribeLatestTurn();
    }

    public void setLatestTurn(CombatTurnModal currentTurn){
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

    // Logic to update all the combatant details
    public void updateCombatantsDetails(Entity target, Entity attacker) throws NoSuchElementException{
        Entity enemy = getMonster().getValue();
        UUID targetId = target.getId();
        UUID attackerId = attacker.getId();
        UUID playerId = playerDataSource.getPlayerId();
        RoomState roomState = roomDataSource.getRoomState().getValue();
        Entity currTarget;

        if(roomState == null) return;
        if(enemy == null) return;

        // If the target is player, the attacker is a monster
        // If the target is a monster, the attacker is a player
        if(target.getAllegiance() != Entity.Allegiance.ENEMY){
            // Target is player, attacker is monster

            currTarget = roomState.getPlayerMap().get(targetId);
            if(currTarget == null) throw new NoSuchElementException("Player not found");

            // If target is you
            if(targetId.equals(playerId)) playerDataSource.setPlayer((Player) target);

            // Check if players are alive
            if(!target.isAlive()){
                removeCombatant(targetId.toString());
                roomState.removePlayer(targetId);
                return;
            }

            // Update the combatants details that's in the room state
            roomState.removePlayer(targetId);
            roomState.addPlayer((Player) target);

            // Update monster details
            combatDataSource.setMonster(attacker);

        }else{
            // Target is monster, attacker is player

            // I am assuming 1 monster here, if theres more than 1 monster than you should change this line
            currTarget = getMonster().getValue();
            if(currTarget == null) throw new NoSuchElementException("Monster not found");

            // if attacker is you
            if(attackerId.equals(playerId)){
                playerDataSource.setPlayer((Player) attacker);
            }
            else if (roomState.getPlayerMap().get(attackerId) != null)
            {

                // Attacker is other player
                roomState.removePlayer(attackerId);
                roomState.addPlayer((Player) attacker);
            }

            // Here i am just assuming there's only 1 monster, change this line if there are more than 1 monster
            combatDataSource.setMonster(target);
        }
    }



    // Remove combatant from the initiative list, maintaining the order
    public void removeCombatant(String targetId){
        UUID combatant = UUID.fromString(targetId);
        List<InitiativeResult> initiativeResults =this.getInitiativeResults().getValue();
        if(initiativeResults == null || initiativeResults.isEmpty()){
            return;
        }

        initiativeResults.removeIf(initiativeResult -> initiativeResult.getEntity().getId().equals(combatant));
        combatDataSource.setInitiativeResults(initiativeResults);
    }


    public void setNextRound(int round){

        // If its the same round
        if(combatDataSource.getPrevRound() != round){
            combatDataSource.setPrevRound(round);
            combatDataSource.setCurrentRound(round);
        }
    }

    public LiveData<Integer> getCurrentRound(){
        return combatDataSource.getCurrentRound();
    }

    public Boolean isNewRound(){
        Integer currRound = combatDataSource.getCurrentRound().getValue();
        Integer prevRound = combatDataSource.getPrevRound();
        if(currRound == null) return false;
        return !currRound.equals(prevRound);
    }

    public void resetRounds(){
        combatDataSource.setPrevRound(0);
        combatDataSource.setCurrentRound(1);
    }
}
