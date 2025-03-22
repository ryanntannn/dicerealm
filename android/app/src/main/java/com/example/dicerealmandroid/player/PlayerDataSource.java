package com.example.dicerealmandroid.player;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.dicerealmandroid.core.entity.Entity;
import com.example.dicerealmandroid.core.item.InventoryOf;
import com.example.dicerealmandroid.core.item.Item;
import com.example.dicerealmandroid.core.player.Player;
import com.example.dicerealmandroid.core.skill.Skill;

import java.util.UUID;


/*
 * Singleton pattern to ensure only 1 instance of PlayerDataSource exists so that it persists throughout the lifecycle of the app.
 * */
public class PlayerDataSource {
    private static PlayerDataSource instance;
    private final MutableLiveData<Player> player = new MutableLiveData<Player>(new Player("Default",
            Entity.Race.HUMAN,
            Entity.EntityClass.WARRIOR,
            Entity.ClassStats.getStatsForClass(Entity.EntityClass.WARRIOR)));

    private PlayerDataSource(){}


    public static PlayerDataSource getInstance(){
        if (instance == null){
            instance = new PlayerDataSource();
        }
        return instance;
    }

    public LiveData<Player> getPlayer(){
        return player;
    }

    public void setPlayer(Player player) throws IllegalArgumentException{
        this.player.postValue(player);
    }

    public void updatePlayer(Entity.Stats stats, InventoryOf<Item> inventory, InventoryOf<Skill> skillsInventory){
        Player currentPlayer = player.getValue();
        currentPlayer.updateInventory(inventory);
        currentPlayer.updateSkillsInventory(skillsInventory);
        currentPlayer.updateEntityStats(stats);
        player.postValue(currentPlayer);
    }

    public UUID getPlayerId(){
        return player.getValue().getId();
    }

}
