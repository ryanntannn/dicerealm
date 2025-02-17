package com.dicerealm.core.command;


import com.dicerealm.core.entity.BodyPart;
import com.dicerealm.core.entity.StatsMap;
import com.dicerealm.core.item.Item;

public class PlayerEquipItemResponse extends Command {
	private String playerId;
	private Item item;
	private BodyPart bodyPart;
	private StatsMap updatedPlayerStats;

	public PlayerEquipItemResponse(String playerId, Item item, BodyPart bodyPart,  StatsMap updatedPlayerStats) {
		super.type = "PLAYER_EQUIP_ITEM_RESPONSE";
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

	public BodyPart getBodyPart() {
		return bodyPart;
	}

	public StatsMap getUpdatedPlayerStats() {
		return updatedPlayerStats;
	}
}
