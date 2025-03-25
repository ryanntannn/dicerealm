package com.example.dicerealmandroid;

import com.dicerealm.core.inventory.Identifiable;
import com.dicerealm.core.inventory.InventoryOf;
import com.dicerealm.core.item.EquippableItem;
import com.dicerealm.core.item.Item;
import com.dicerealm.core.item.UseableItem;
import com.dicerealm.core.skills.Skill;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.List;

public class Serialization {


    static class InventoryOfItemDeserializer<T extends Identifiable> implements JsonDeserializer<InventoryOf<T>> {
        Gson baseGson = new Gson();
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
                        String itemType = item.getAsJsonObject().get("type").getAsString();
                        switch (itemType){
                            case "ITEM":
                                itemInventory.addItem((T) baseGson.fromJson(item, Item.class));
                                break;
                            case "EQUIPPABLE_ITEM":
                                itemInventory.addItem((T) baseGson.fromJson(item, EquippableItem.class));
                                break;
                            case "USABLE_ITEM":
                                itemInventory.addItem((T) baseGson.fromJson(item, UseableItem.class));
                                break;
                            default:
                                throw new JsonParseException(type + " not handled");
                        }
                    }
                    return itemInventory;
                case "SKILL":
                    InventoryOf<T> skillInventory = new InventoryOf<>("SKILL", size);
                    for (JsonElement item : items) {
                        skillInventory.addItem((T) baseGson.fromJson(item, Skill.class));
                    }
                    return skillInventory;
                default:
                    throw new JsonParseException(type + " not handled");
            }
        }
    }
    public static Gson makeDicerealmGsonInstance() {
        return new GsonBuilder().registerTypeAdapter(InventoryOf.class, new InventoryOfItemDeserializer<>()).create();
    }
}
