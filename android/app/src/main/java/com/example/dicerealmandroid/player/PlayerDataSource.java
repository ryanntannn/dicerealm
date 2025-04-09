package com.example.dicerealmandroid.player;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dicerealm.core.command.PlayerEquipItemResponse;
import com.dicerealm.core.entity.BodyPart;
import com.dicerealm.core.entity.ClassStats;
import com.dicerealm.core.entity.Entity;
import com.dicerealm.core.entity.EntityClass;
import com.dicerealm.core.entity.Race;
import com.dicerealm.core.entity.EntityClass;
import com.dicerealm.core.item.EquippableItem;
import com.dicerealm.core.inventory.InventoryOf;
import com.dicerealm.core.item.Item;
import com.dicerealm.core.player.Player;
import com.dicerealm.core.skills.Skill;

import java.util.UUID;


/*
 * Singleton pattern to ensure only 1 instance of PlayerDataSource exists so that it persists throughout the lifecycle of the app.
 * */
public class PlayerDataSource {
    private static PlayerDataSource instance;
    private final MutableLiveData<Player> player = new MutableLiveData<>(null);

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


    public UUID getPlayerId(){
        return player.getValue().getId();
    }

    public void equipItem(BodyPart bodyPart, EquippableItem item){
        Player equipPlayer = player.getValue();
        equipPlayer.equipItem(bodyPart, item);
        player.postValue(equipPlayer);
    }


    // Reset all cache data while leaving the singleton instance
    // Maintain observers connections :>
    public static void destroy(){
        if(instance != null){
            instance.setPlayer(null);
        }
    }
}
