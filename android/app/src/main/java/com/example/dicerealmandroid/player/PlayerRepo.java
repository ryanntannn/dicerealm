package com.example.dicerealmandroid.player;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.dicerealmandroid.command.Command;
import com.example.dicerealmandroid.command.UpdatePlayerDetailsCommand;
import com.example.dicerealmandroid.command.UpdatePlayerDetailsRequestCommand;
import com.example.dicerealmandroid.core.entity.Entity;
import com.example.dicerealmandroid.core.player.Player;
import com.example.dicerealmandroid.room.RoomRepo;
import com.google.gson.Gson;

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

    private Gson gson = new Gson();

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

    public void updatePlayerRequest(Player player){
        UpdatePlayerDetailsRequestCommand command = new UpdatePlayerDetailsRequestCommand(player.getDisplayName(), player.getRace(), player.getEntityClass(), player.getStats());
        String message = gson.toJson(command);
        RoomRepo.getInstance().getDicerealmClient().send(message);
    }
}
