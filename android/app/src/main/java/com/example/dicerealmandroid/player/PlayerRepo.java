package com.example.dicerealmandroid.player;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dicerealm.core.command.UpdatePlayerDetailsRequestCommand;
import com.dicerealm.core.entity.ClassStats;
import com.dicerealm.core.entity.EntityClass;
import com.dicerealm.core.entity.Race;
import com.dicerealm.core.player.Player;
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
}
