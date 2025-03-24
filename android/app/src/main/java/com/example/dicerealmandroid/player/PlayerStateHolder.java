package com.example.dicerealmandroid.player;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.dicerealmandroid.command.UpdatePlayerDetailsCommand;
import com.example.dicerealmandroid.core.entity.Entity;
import com.example.dicerealmandroid.core.player.Player;

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

    public void equipItemRequest(UUID itemId, Entity.BodyPart bodyPart){
        playerRepo.equipItemRequest(itemId, bodyPart);
    }

    public String remainingHealth() {
        return "Remaining Health: " + playerRepo.getPlayer().getValue().getHealth() + "/" + playerRepo.getPlayer().getValue().getStat(Entity.Stat.MAX_HEALTH);
    }
}
