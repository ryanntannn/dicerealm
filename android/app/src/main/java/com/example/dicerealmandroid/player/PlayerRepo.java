package com.example.dicerealmandroid.player;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.dicerealmandroid.command.Command;
import com.example.dicerealmandroid.command.PlayerEquipItemRequest;
import com.example.dicerealmandroid.command.UpdatePlayerDetailsCommand;
import com.example.dicerealmandroid.command.UpdatePlayerDetailsRequestCommand;
import com.example.dicerealmandroid.core.entity.Entity;
import com.example.dicerealmandroid.core.item.InventoryOf;
import com.example.dicerealmandroid.core.item.Item;
import com.example.dicerealmandroid.core.player.Player;
import com.example.dicerealmandroid.core.skill.Skill;
import com.example.dicerealmandroid.room.RoomDataSource;
import com.example.dicerealmandroid.room.RoomRepo;
import com.google.gson.Gson;

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
        playerDataSource.setPlayer(player);
    }


    public UUID getPlayerId(){
        if (this.getPlayer().getValue() == null){
            return null;
        }
        return playerDataSource.getPlayerId();
    }

    public void updatePlayerRequest(Player player){
        UpdatePlayerDetailsRequestCommand command = new UpdatePlayerDetailsRequestCommand(player.getDisplayName(), player.getRace(), player.getEntityClass(), player.getStats());
        String message = gson.toJson(command);
        roomDataSource.sendMessageToServer(message);
    }

    public void equipItem(UUID itemId, Entity.BodyPart bodypart){
        PlayerEquipItemRequest command = new PlayerEquipItemRequest(itemId.toString(), bodypart);
        String message = gson.toJson(command);
        roomDataSource.sendMessageToServer(message);
    }
}
