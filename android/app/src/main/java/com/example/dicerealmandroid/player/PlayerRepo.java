package com.example.dicerealmandroid.player;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dicerealm.core.command.Command;
import com.dicerealm.core.command.PlayerEquipItemRequest;
import com.dicerealm.core.command.PlayerEquipItemResponse;
import com.dicerealm.core.command.UpdatePlayerDetailsCommand;
import com.dicerealm.core.command.UpdatePlayerDetailsRequestCommand;
import com.dicerealm.core.entity.Entity;
import com.dicerealm.core.inventory.InventoryOf;
import com.dicerealm.core.item.EquippableItem;
import com.dicerealm.core.item.Item;
import com.dicerealm.core.player.Player;
import com.dicerealm.core.room.RoomState;
import com.dicerealm.core.skills.Skill;
import com.example.dicerealmandroid.room.RoomDataSource;
import com.example.dicerealmandroid.room.RoomRepo;
import com.google.gson.Gson;
import com.dicerealm.core.entity.BodyPart;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerRepo {

    private final PlayerDataSource playerDataSource;
    private final RoomDataSource roomDataSource;
    private final Gson gson = new Gson();

    public PlayerRepo(){
        playerDataSource = PlayerDataSource.getInstance();
        roomDataSource = RoomDataSource.getInstance();
    };

    public LiveData<Player> getPlayer(){
        return playerDataSource.getPlayer();
    }

    public void setPlayer(Player player) throws IllegalArgumentException{
        if (player == null){
            throw new IllegalArgumentException("Player cannot be null");
        }

        // Only set if the player ID matches the current player ID
        if (this.getPlayerId() == null || player.getId().equals(this.getPlayerId())){
            playerDataSource.setPlayer(player);
        }
    }


    public UUID getPlayerId(){
        if (this.getPlayer().getValue() == null){
            return null;
        }
        return playerDataSource.getPlayerId();
    }

    public void updatePlayerRequest(Player player){
        UpdatePlayerDetailsRequestCommand command = new UpdatePlayerDetailsRequestCommand(player.getDisplayName(), player.getRace(), player.getEntityClass(), player.getStats());
        command.race = player.getRace();
        String message = gson.toJson(command);
        roomDataSource.sendMessageToServer(message);
    }

    public void equipItemRequest(UUID itemId, BodyPart bodypart){
        PlayerEquipItemRequest command = new PlayerEquipItemRequest(itemId.toString(), bodypart);
        String message = gson.toJson(command);
        roomDataSource.sendMessageToServer(message);
    }

    public Boolean equipItem(PlayerEquipItemResponse response){
        BodyPart bodyPart = response.getBodyPart();
        EquippableItem item = (EquippableItem) playerDataSource.getPlayer().getValue().getInventory().getItem(response.getItem().getId());

        // Check if the response is for the current player
        if(getPlayerId().equals(UUID.fromString(response.getPlayerId()))){
            playerDataSource.equipItem(bodyPart, item);
            return true;
        }
        return false;
    }

}
