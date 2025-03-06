package com.dicerealm.core.handler;

import java.util.UUID;

import com.dicerealm.core.command.PlayerEquipItemRequest;
import com.dicerealm.core.command.PlayerEquipItemResponse;
import com.dicerealm.core.item.EquippableItem;
import com.dicerealm.core.item.Item;
import com.dicerealm.core.player.Player;
import com.dicerealm.core.room.RoomContext;

public class PlayerEquipItemHandler extends CommandHandler<PlayerEquipItemRequest> {
	public PlayerEquipItemHandler() {
		super("PLAYER_EQUIP_ITEM_REQUEST");
	}
	@Override
	public void handle(UUID playerId, PlayerEquipItemRequest command, RoomContext context) {
		Player player = context.getRoomState().getPlayerMap().get(playerId);

		String itemIdStr = command.getItemId();

		UUID itemId = UUID.fromString(itemIdStr);

		Item item = player.getInventory().getItem(itemId);
		
		if (item == null) {
			throw new IllegalArgumentException("Item not found in inventory");
		}
		if (!(item instanceof EquippableItem)) {
			throw new IllegalArgumentException("Item is not wearable");
		}
		boolean equipSuccessful = player.equipItem(command.getBodyPart(), (EquippableItem) item);
		if (!equipSuccessful) {
			throw new IllegalArgumentException("Item could not be equipped");
		}
		context.getBroadcastStrategy().sendToAllPlayers(new PlayerEquipItemResponse(playerId.toString(), item, command.getBodyPart(), player.getStats()));
	}
}
