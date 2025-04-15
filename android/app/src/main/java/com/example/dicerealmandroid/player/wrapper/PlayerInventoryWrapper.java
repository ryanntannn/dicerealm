package com.example.dicerealmandroid.player.wrapper;

import androidx.lifecycle.LiveData;

import com.dicerealm.core.entity.BodyPart;
import com.dicerealm.core.item.EquippableItem;
import com.dicerealm.core.item.Potion;
import com.dicerealm.core.item.Scroll;

import java.util.List;
import java.util.Map;

// For easier access to all the player inventory(all Items) for fragment argmuent
public class PlayerInventoryWrapper {
    public LiveData<List<EquippableItem>> equippableItems;
    public LiveData<Map<BodyPart, EquippableItem>> equippedItems;
    public LiveData<List<Potion>> potions;
    public LiveData<List<Scroll>> scrolls;

    public PlayerInventoryWrapper(
            LiveData<List<EquippableItem>> equippableItems,
            LiveData<Map<BodyPart, EquippableItem>> equippedItems,
            LiveData<List<Potion>> potions,
            LiveData<List<Scroll>> scrolls
    ) {
        this.equippableItems = equippableItems;
        this.equippedItems = equippedItems;
        this.potions = potions;
        this.scrolls = scrolls;
        // Skills not implemented due to type difference (Identifiable not Item)
    }

}
