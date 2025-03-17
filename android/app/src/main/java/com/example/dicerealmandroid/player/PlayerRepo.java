package com.example.dicerealmandroid.player;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.dicerealmandroid.core.Entity;
import com.example.dicerealmandroid.core.Player;

import java.util.UUID;

public class PlayerRepo {

    private final PlayerDataSource playerDataSource;


    public PlayerRepo(){
        playerDataSource = PlayerDataSource.getInstance();
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

}
