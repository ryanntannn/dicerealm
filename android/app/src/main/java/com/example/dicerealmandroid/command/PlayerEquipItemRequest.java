package com.example.dicerealmandroid.command;

import com.example.dicerealmandroid.core.entity.Entity.BodyPart;

public class PlayerEquipItemRequest extends Command {
    private String itemId;
    private BodyPart bodyPart;

    public PlayerEquipItemRequest(String itemId, BodyPart bodyPart) {
        super("PLAYER_EQUIP_ITEM_REQUEST");
        this.itemId = itemId;
        this.bodyPart = bodyPart;
    }

    public String getItemId() {
        return itemId;
    }

    public BodyPart getBodyPart() {
        return bodyPart;
    }
}
