package com.example.dicerealmandroid.player;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.dicerealmandroid.core.Entity;
import com.example.dicerealmandroid.core.Player;

import java.util.UUID;

/*
* Singleton pattern to ensure only 1 instance of PlayerRepo exists
* */
public class PlayerRepo {
    private static PlayerRepo instance;
    private final MutableLiveData<Player> player = new MutableLiveData<Player>(new Player("Default",
                                                                                            Entity.Race.HUMAN,
                                                                                            Entity.EntityClass.WARRIOR,
                                                                                            Entity.ClassStats.getStatsForClass(Entity.EntityClass.WARRIOR)));

    private PlayerRepo(){};

    public static PlayerRepo getInstance(){
        if (instance == null){
            instance = new PlayerRepo();
        }
        return instance;
    }


    public LiveData<Player> getPlayer(){
        return player;
    }

    public void setPlayer(Player player) throws IllegalArgumentException{
        if (player == null){
            throw new IllegalArgumentException("Player cannot be null");
        }
        this.player.postValue(player);
    }

    public UUID getPlayerId(){
        if (player.getValue() == null){
            return null;
        }
        return player.getValue().getId();
    }

}
