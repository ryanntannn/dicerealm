package com.example.dicerealmandroid.command;

import com.example.dicerealmandroid.core.entity.Entity;
import com.example.dicerealmandroid.core.item.EquippableItem;
import com.example.dicerealmandroid.core.item.Item;

public class PlayerEquipItemResponse extends Command {
    private String playerId;
    private EquippableItem item;
    private Entity.BodyPart bodyPart;
    private Entity.StatsMap updatedPlayerStats;

    public PlayerEquipItemResponse() {
        super("PLAYER_EQUIP_ITEM_RESPONSE");
    }

    public String getPlayerId() {
        return playerId;
    }

    public EquippableItem getItem() {
        return item;
    }

    public Entity.BodyPart getBodyPart() {
        return bodyPart;
    }

    public Entity.StatsMap getUpdatedPlayerStats() {
        return updatedPlayerStats;
    }
}
