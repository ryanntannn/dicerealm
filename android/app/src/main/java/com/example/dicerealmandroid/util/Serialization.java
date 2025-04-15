package com.example.dicerealmandroid.util;

import com.dicerealm.core.command.combat.CombatTurnActionCommand;
import com.dicerealm.core.entity.BodyPart;
import com.dicerealm.core.entity.Entity;
import com.dicerealm.core.inventory.Identifiable;
import com.dicerealm.core.inventory.InventoryOf;
import com.dicerealm.core.item.EquippableItem;
import com.dicerealm.core.item.Item;
import com.dicerealm.core.item.Potion;
import com.dicerealm.core.item.Scroll;
import com.dicerealm.core.item.UseableItem;
import com.dicerealm.core.item.Weapon;
import com.dicerealm.core.locations.Path;
import com.dicerealm.core.skills.Skill;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.dicerealm.core.command.combat.CombatTurnActionCommand;
import com.dicerealm.core.item.Scroll;
import com.dicerealm.core.item.Potion;
import com.dicerealm.core.item.Weapon;
import com.dicerealm.core.entity.Entity;
import com.google.gson.reflect.TypeToken;
import com.dicerealm.core.monster.Monster;
import com.dicerealm.core.player.Player;
import com.dicerealm.core.locations.LocationGraph;


import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Serialization {

	static class ItemDeserializer implements JsonDeserializer<Item> {

		@Override
		public Item deserialize(JsonElement json, Type typeOfT1, JsonDeserializationContext context)
				throws JsonParseException {
			JsonObject jsonObject = json.getAsJsonObject();

			String itemType = json.getAsJsonObject().get("type").getAsString();
			switch (itemType) {
				case "ITEM":
					return context.deserialize(json, Item.class);
				case "EQUIPPABLE_ITEM":
					return context.deserialize(json, EquippableItem.class);
				case "WEAPON":
					return context.deserialize(json, Weapon.class);
				case "SCROLL":
					return context.deserialize(json, Scroll.class);
				case "POTION":
					return context.deserialize(json, Potion.class);
				case "SKILL":
					return context.deserialize(json, Skill.class);
				default:
					throw new JsonParseException(itemType + " not handled");
			}
		}

		static class EquippableItemDeserializer implements JsonDeserializer<EquippableItem> {

			Gson baseGson = new Gson();

			@Override
			public EquippableItem deserialize(JsonElement json, Type typeOfT1,
					JsonDeserializationContext context) throws JsonParseException {

				String itemType = json.getAsJsonObject().get("type").getAsString();
				switch (itemType) {
					case "EQUIPPABLE_ITEM":
						return baseGson.fromJson(json, EquippableItem.class);
					case "WEAPON":
						return context.deserialize(json, Weapon.class);
					default:
						throw new JsonParseException(itemType + " not handled");
				}
			}
		}
	}

	static class InventoryOfItemDeserializer<T extends Identifiable>
			implements JsonDeserializer<InventoryOf<T>> {

		public InventoryOf<T> deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			JsonObject object = json.getAsJsonObject();
			String type = object.get("type").getAsString();
			int size = object.get("inventorySize").getAsInt();
			List<JsonElement> items = object.get("items").getAsJsonArray().asList();
			// handle based on inventory type
			switch (type) {
				case "ITEM":
					InventoryOf<T> itemInventory = new InventoryOf<>("ITEM", size);
					for (JsonElement item : items) {
						itemInventory.addItem(context.deserialize(item, Item.class));
					}
					return itemInventory;
				case "SKILL":
					InventoryOf<T> skillInventory = new InventoryOf<>("SKILL", size);
					for (JsonElement item : items) {
						skillInventory.addItem(context.deserialize(item, Skill.class));
					}
					return skillInventory;
				default:
					throw new JsonParseException(type + " not handled");
			}
		}
	}


	static class CombatTurnActionCommandDeserializer
			implements JsonDeserializer<CombatTurnActionCommand> {
		@Override
		public CombatTurnActionCommand deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			JsonObject object = json.getAsJsonObject();
			String type = object.get("actionType").getAsString();
			JsonElement actionJsonElement = object.get("action");
			Object action;
			switch (type) {
				case "SKILL":
					action = context.deserialize(actionJsonElement, Skill.class);
					break;
				case "WEAPON":
					action = context.deserialize(actionJsonElement, Weapon.class);
					break;
				case "SCROLL":
					action = context.deserialize(actionJsonElement, Scroll.class);
					break;
				case "POTION":
					action = context.deserialize(actionJsonElement, Potion.class);
					break;
				default:
					throw new JsonParseException(type + " not handled");
			}

			CombatTurnActionCommand command =
					new CombatTurnActionCommand(context.deserialize(object.get("attacker"), Entity.class),
							context.deserialize(object.get("target"), Entity.class), action,
							CombatTurnActionCommand.ActionType.valueOf(type));

			return command;
		}
	}

	static class EntityDeserializer implements JsonDeserializer<Entity> {
		Gson baseGson = new Gson();

		@Override
		public Entity deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			JsonObject object = json.getAsJsonObject();
			String type = object.get("allegiance").getAsString();
			switch (type) {
				case "PLAYER":
					return context.deserialize(json, Player.class);
				case "ENEMY":
					return context.deserialize(json, Monster.class);
				default:
					return baseGson.fromJson(json, Entity.class);
			}
		}
	}

	static class LocationGraphDeserializer implements JsonDeserializer<LocationGraph> {
		Gson baseGson = new Gson();

		@Override
		public LocationGraph deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			LocationGraph graph = baseGson.fromJson(json, LocationGraph.class);

			for (Path path : graph.getEdges()) {
				path.setSource(graph.getN(path.getSource().getId()));
				path.setTarget(graph.getN(path.getTarget().getId()));
			}

			// ensure that the current location points to the same memory addr as the list of nodes
			graph.setCurrentLocation(graph.getN(graph.getCurrentLocation().getId()));

			return graph;
		}
	}

	public static Gson makeDicerealmGsonInstance() {
		// Create custom equippedItemsType for the map of BodyPart to EquippableItem
		Type equippedItemsType = new TypeToken<Map<BodyPart, EquippableItem>>() {}.getType();
		return new GsonBuilder()
				.registerTypeAdapter(InventoryOf.class, new InventoryOfItemDeserializer<>())
				.registerTypeAdapter(Item.class, new ItemDeserializer())
				.registerTypeAdapter(EquippableItem.class,
						new ItemDeserializer.EquippableItemDeserializer())
				.registerTypeAdapter(CombatTurnActionCommand.class,
						new CombatTurnActionCommandDeserializer())
				.registerTypeAdapter(Entity.class, new EntityDeserializer())
				.registerTypeAdapter(LocationGraph.class, new LocationGraphDeserializer()).create();
	}
}
