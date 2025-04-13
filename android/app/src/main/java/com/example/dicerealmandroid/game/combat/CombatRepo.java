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
            List<Entity> monsters = new ArrayList<>();

            for(InitiativeResult initiativeResult : initiativeResults){
                Entity entity = initiativeResult.getEntity();
                if(entity.getAllegiance() == Entity.Allegiance.ENEMY){
                    monsters.add(entity);
                }
            }
            // Set Monster details
            combatDataSource.setMonsters(monsters);

            // Set the initiative results
            combatDataSource.setInitiativeResults(initiativeResults);
        }
    }

    // action represents the sub-classes of Item, Skill or Weapon
    public void performAction(Object action, CombatTurnActionCommand.ActionType actionType, Entity target){
        Player attacker = playerDataSource.getPlayer().getValue();
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


    public LiveData<List<Entity>> getMonsters() {
        return combatDataSource.getMonsters();
    }


//    public void takeDamage(UUID targetId, int damage) throws IllegalArgumentException{
//        UUID playerId = playerDataSource.getPlayerId();
//        Entity enemy = getMonster(targetId).getValue();
//        if(enemy == null){
//            throw new IllegalArgumentException("There doesn't exist a monster");
//        }
//        UUID enemyId = enemy.getId();
//
//        Entity targetEntity;
//
//        // If target is the player (you) or enemy
//        if (playerId.equals(targetId)){
//            targetEntity = playerDataSource.getPlayer().getValue();
//            if(targetEntity == null){
//                throw new IllegalArgumentException("Player cannot be null");
//            }
//            targetEntity.takeDamage(damage);
//            if (!targetEntity.isAlive()) removeCombatant(targetEntity.getId().toString());
//
//            playerDataSource.setPlayer((Player) targetEntity);
//        }
//        else if (enemyId.equals(targetId)){
//            targetEntity = getMonster(targetId);
//            targetEntity.takeDamage(damage);
//            combatDataSource.setMonster(targetEntity);
//        }
//    }

    public void takeDamage(UUID targetId, int damage) throws IllegalArgumentException {
        UUID playerId = playerDataSource.getPlayerId();

        // If target is the player
        if (playerId.equals(targetId)) {
            Player targetPlayer = playerDataSource.getPlayer().getValue();
            if (targetPlayer == null) {
                throw new IllegalArgumentException("Player cannot be null");
            }
            targetPlayer.takeDamage(damage);
            if (!targetPlayer.isAlive()) {
                removeCombatant(targetPlayer.getId().toString());
            }
            playerDataSource.setPlayer(targetPlayer);
        }

        // If target is a monster
        List<Entity> monsters = combatDataSource.getMonsters().getValue();
        if (monsters == null) {
            throw new IllegalArgumentException("Monster list cannot be null");
        }

        // Find the monster by ID
        for (int i = 0; i < monsters.size(); i++) {
            Entity monster = monsters.get(i);
            if (monster.getId().equals(targetId)) {
                // Apply damage
                monster.takeDamage(damage);

                // If monster died, remove from combat
                if (!monster.isAlive()) {
                    removeCombatant(monster.getId().toString());
                    monsters.remove(i);
                } else {
                    // Replace with updated monster
                    monsters.set(i, monster);
                }

                // Update monster list
                combatDataSource.setMonsters(new ArrayList<>(monsters));
            }
        }

        // If we get here, no monster was found
        throw new IllegalArgumentException("No monster found with ID: " + targetId);
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
