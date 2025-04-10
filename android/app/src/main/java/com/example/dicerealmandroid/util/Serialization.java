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


import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Serialization {

	static class ItemDeserializer implements JsonDeserializer<Item> {

		@Override
		public Item deserialize(JsonElement json, Type typeOfT1, JsonDeserializationContext context) throws JsonParseException {
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
						default:
								throw new JsonParseException(itemType + " not handled");
				}
		}

		static class EquippableItemDeserializer implements JsonDeserializer<EquippableItem> {

			Gson baseGson = new Gson();
			@Override
			public EquippableItem deserialize(JsonElement json, Type typeOfT1, JsonDeserializationContext context) throws JsonParseException {

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

			static class InventoryOfItemDeserializer<T extends Identifiable> implements JsonDeserializer<InventoryOf<T>> {

				public InventoryOf<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
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

//			static class EquippedItemsDeserializer implements JsonDeserializer<Map<BodyPart, EquippableItem>>{
//				@Override
//				public Map<BodyPart, EquippableItem> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException{
//					Map<BodyPart, EquippableItem> equippedItems = new HashMap<>();
//					JsonObject object = json.getAsJsonObject();
//
//					// Loops through every equipped item
//					for (Map.Entry<String, JsonElement> entry : object.entrySet()){
//						String key = entry.getKey();
//						BodyPart bodyPart = BodyPart.valueOf(key.toUpperCase());
//
//						// Each equipped item
//						JsonObject equippedObj = entry.getValue().getAsJsonObject();
//						EquippableItem item;
//
//						// Check if weapon
//						JsonElement actionType = equippedObj.get("actionType");
//						if(actionType != null && actionType.getAsString().equals("MELEE")){
//							item = context.deserialize(entry.getValue(), Weapon.class);
//						}else{
//							item = context.deserialize(entry.getValue(), EquippableItem.class);
//						}
//						equippedItems.put(bodyPart, item);
//					}
//
//					return equippedItems;
//				}
//			}

			static class CombatTurnActionCommandDeserializer implements JsonDeserializer<CombatTurnActionCommand> {
			@Override
			public CombatTurnActionCommand deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
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
						action =  context.deserialize(actionJsonElement, Scroll.class);
						break;
					case "POTION":
						action =  context.deserialize(actionJsonElement, Potion.class);
						break;
					default:
						throw new JsonParseException(type + " not handled");
				}

				CombatTurnActionCommand command = new CombatTurnActionCommand(
						context.deserialize(object.get("attacker"), Entity.class),
						context.deserialize(object.get("target"), Entity.class),
						action,
						CombatTurnActionCommand.ActionType.valueOf(type)
				);

				return command;
			}
			}

			public static Gson makeDicerealmGsonInstance() {
				// Create custom equippedItemsType for the map of BodyPart to EquippableItem
				Type equippedItemsType = new TypeToken<Map<BodyPart, EquippableItem>>() {}.getType();
				return new GsonBuilder()
								.registerTypeAdapter(InventoryOf.class, new InventoryOfItemDeserializer<>())
								.registerTypeAdapter(Item.class, new ItemDeserializer())
								.registerTypeAdapter(EquippableItem.class, new ItemDeserializer.EquippableItemDeserializer())
								.registerTypeAdapter(CombatTurnActionCommand.class, new CombatTurnActionCommandDeserializer())
//								.registerTypeAdapter(equippedItemsType, new EquippedItemsDeserializer())
								.create();
			}


}
