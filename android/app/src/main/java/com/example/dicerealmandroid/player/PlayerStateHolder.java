package com.example.dicerealmandroid.player;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.dicerealm.core.command.UpdatePlayerDetailsCommand;
import com.dicerealm.core.entity.BodyPart;
import com.dicerealm.core.entity.Entity;
import com.dicerealm.core.entity.Stat;
import com.dicerealm.core.entity.Stats;
import com.dicerealm.core.inventory.InventoryOf;
import com.dicerealm.core.item.EquippableItem;
import com.dicerealm.core.item.Item;
import com.dicerealm.core.player.Player;
import com.dicerealm.core.skills.Skill;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerStateHolder extends ViewModel{
    private PlayerRepo playerRepo;


    public PlayerStateHolder(){
        this.playerRepo = new PlayerRepo();
    }

    public LiveData<Player> getPlayer(){
        return playerRepo.getPlayer();
    }

    public void setPlayer(Player player){
        playerRepo.setPlayer(player);
    }
    public void updatePlayerRequest(Player player){
        playerRepo.updatePlayerRequest(player);
    }

    public UUID getPlayerId(){
        return playerRepo.getPlayerId();
    }

    public void equipItemRequest(UUID itemId, BodyPart bodyPart){
        playerRepo.equipItemRequest(itemId, bodyPart);
    }

    public String remainingHealth() {
        return "Remaining Health: " + playerRepo.getPlayer().getValue().getHealth() + "/" + playerRepo.getPlayer().getValue().getStat(Stat.MAX_HEALTH);
    }


    public LiveData<EquippableItem> getEquippedItem(BodyPart bodyPart){
        return Transformations.map(playerRepo.getPlayer(), player -> player.getEquippedItems().get(bodyPart));
    }


    public LiveData<InventoryOf<Skill>> getSkills(){
        return Transformations.map(playerRepo.getPlayer(), Entity::getSkillsInventory);
    }

    public LiveData<InventoryOf<Item>> getScrolls_Potions(){
       return Transformations.map(playerRepo.getPlayer(), Entity::getInventory);
    }
}
