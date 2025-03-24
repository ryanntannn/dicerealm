package com.example.dicerealmandroid.command;

import com.example.dicerealmandroid.core.entity.Entity;
import com.example.dicerealmandroid.core.item.Item;

public class PlayerEquipItemResponse extends Command {
    private String playerId;
    private Item item;
    private Entity.BodyPart bodyPart;
    private Entity.StatsMap updatedPlayerStats;

    public PlayerEquipItemResponse(String playerId, Item item, Entity.BodyPart bodyPart, Entity.StatsMap updatedPlayerStats) {
        super("PLAYER_EQUIP_ITEM_RESPONSE");
        this.playerId = playerId;
        this.item = item;
        this.bodyPart = bodyPart;
        this.updatedPlayerStats = updatedPlayerStats;
    }

    public String getPlayerId() {
        return playerId;
    }

    public Item getItem() {
        return item;
    }

    public Entity.BodyPart getBodyPart() {
        return bodyPart;
    }

    public Entity.StatsMap getUpdatedPlayerStats() {
        return updatedPlayerStats;
    }
}
