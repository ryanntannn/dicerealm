package com.dicerealm.server.strategy;

import java.lang.reflect.Type;
import java.util.List;

import org.springframework.ai.converter.BeanOutputConverter;

import com.dicerealm.core.command.combat.CombatTurnActionCommand;
import com.dicerealm.core.entity.Entity;
import com.dicerealm.core.inventory.Identifiable;
import com.dicerealm.core.inventory.InventoryOf;
import com.dicerealm.core.item.EquippableItem;
import com.dicerealm.core.item.Item;
import com.dicerealm.core.item.Potion;
import com.dicerealm.core.item.Weapon;
import com.dicerealm.core.item.Scroll;
import com.dicerealm.core.skills.Skill;
import com.dicerealm.core.strategy.JsonSerializationStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class GsonSerializer implements JsonSerializationStrategy {

	static class ItemDeserializer implements JsonDeserializer<Item> {

        @Override
        public Item deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String itemType = json.getAsJsonObject().get("type").getAsString();
            switch (itemType) {
                case "ITEM":
                    return context.deserialize(json, Item.class);
                case "EQUIPPABLE_ITEM":
                    return context.deserialize(json, EquippableItem.class);
                case "SCROLL":
                    return context.deserialize(json, Scroll.class);
								case "POTION":
										return context.deserialize(json, Potion.class);
                default:
                    throw new JsonParseException(itemType + " not handled");
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
			return new GsonBuilder()
							.registerTypeAdapter(InventoryOf.class, new InventoryOfItemDeserializer<>())
							.registerTypeAdapter(Item.class, new ItemDeserializer())
							.registerTypeAdapter(CombatTurnActionCommand.class, new CombatTurnActionCommandDeserializer())
							.create();
	}

	private Gson gson = makeDicerealmGsonInstance();

	@Override
	public String serialize(Object object) {
		return gson.toJson(object);
	}

	@Override
	public <T> T deserialize(String json, Class<T> object) {
		return gson.fromJson(json, object);
	}

	@Override
	public String makeJsonSchema(Class<?> schema) {
		// We use the BeanOutputConverter to generate the JSON schema instead of gson because gson does not support JSON schema generation
		BeanOutputConverter<?> outputConverter = new BeanOutputConverter<>(schema);
		return outputConverter.getJsonSchema();
	}
}
