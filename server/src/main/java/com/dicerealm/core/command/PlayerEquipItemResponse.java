package com.dicerealm.core.command;

import com.dicerealm.core.entity.BodyPart;
import com.dicerealm.core.item.Item;

public class PlayerEquipItemResponse extends Command {
	private String playerId;
	private Item item;
	private BodyPart bodyPart;

	public PlayerEquipItemResponse(String playerId, Item item, BodyPart bodyPart) {
		super.type = "PLAYER_EQUIP_ITEM_RESPONSE";
		this.playerId = playerId;
		this.item = item;
		this.bodyPart = bodyPart;
	}

	public String getPlayerId() {
		return playerId;
	}

	public Item getItem() {
		return item;
	}

	public BodyPart getBodyPart() {
		return bodyPart;
	}
}
