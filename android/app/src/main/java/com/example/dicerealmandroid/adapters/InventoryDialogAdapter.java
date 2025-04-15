package com.example.dicerealmandroid.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dicerealm.core.entity.BodyPart;
import com.dicerealm.core.item.EquippableItem;
import com.example.dicerealmandroid.R;
import com.dicerealm.core.item.Item;
import com.example.dicerealmandroid.player.wrapper.PlayerInventoryWrapper;
import com.example.dicerealmandroid.player.PlayerStateHolder;

import java.util.ArrayList;
import java.util.List;

public class InventoryDialogAdapter extends RecyclerView.Adapter<InventoryDialogAdapter.InventoryViewHolder> {

    private final PlayerInventoryWrapper playerInventoryWrapper;
    private final List<Item> equippableItems = new ArrayList<>();
    private final List<Item> equippedItems = new ArrayList<>();
    private final List<Item> potions = new ArrayList<>();
    private final List<Item> scrolls = new ArrayList<>();
    private final PlayerStateHolder playerSh;

    public InventoryDialogAdapter(PlayerInventoryWrapper playerInventoryWrapper, PlayerStateHolder player) {
        this.playerInventoryWrapper = playerInventoryWrapper;
        this.playerSh = player;
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inventory, parent, false);
        return new InventoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {

        Item item = getAllItems().get(position);
        holder.itemIcon.setImageResource(getIconResId(item));
        holder.itemName.setText(item.getDisplayName());
        holder.itemDescription.setText(item.getDescription());

        // Show equip button if the item is equippable
        if (item instanceof EquippableItem) {
            EquippableItem equippableItem = (EquippableItem) item;

            // Btn properties
            for (BodyPart bodyPart : equippableItem.getSuitableBodyParts()) {
                holder.itemEquipButton.setVisibility(View.VISIBLE);
                holder.itemEquipButton.setText("Equip to " + bodyPart);

                // Enable if item not equipped
                if (equippableItem != null && equippedItems != null && !equippedItems.contains(equippableItem)) {
                    holder.itemEquipButton.setEnabled(true);
                    holder.itemEquipButton.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.button_bg_selector));
                    holder.itemEquipButton.setOnClickListener(v -> {
//                        Log.d("info", "Equip button clicked");
                        try {
                            playerSh.equipItemRequest(equippableItem.getId(), bodyPart);

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("InventoryAdapter", "Error equipping item: " + e.getMessage());
                        }
                    });
                } else {
                    holder.itemEquipButton.setEnabled(false);
                    holder.itemEquipButton.setText("Equipped");
                }
            }
        } else {
            holder.itemEquipButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return getAllItems().size();
    }

    private List<Item> getAllItems() {
        List<Item> all = new ArrayList<>();
        all.addAll(equippedItems);
        all.addAll(equippableItems);
        all.addAll(potions);
        all.addAll(scrolls);
//        Log.d("InventoryAdapter", "All items: " + all);
        return all;
    }

    public void updateEquippableItems(List<Item> items) {
        equippableItems.clear();
        equippableItems.addAll(items);
        notifyDataSetChanged();
    }

    public void updateEquippedItems(List<Item> items) {
        equippedItems.clear();
        equippedItems.addAll(items);
        notifyDataSetChanged();
    }

    public void updatePotions(List<Item> items) {
        potions.clear();
        potions.addAll(items);
        notifyDataSetChanged();
    }

    public void updateScrolls(List<Item> items) {
        scrolls.clear();
        scrolls.addAll(items);
        notifyDataSetChanged();
    }

    private int getIconResId(Item item) {
        switch (item.getType()) {
            case "WEAPON":
                return R.drawable.weapon;
            case "POTION":
                return R.drawable.potion;
            case "SCROLL":
                return R.drawable.scroll;
            case "EQUIPPABLE_ITEM":
                return R.drawable.weaponshield;
            default:
                Log.d("InventoryAdapter", "Other item type: " + item.getType());
                return R.drawable.weaponshield;
        }
    }

    static class InventoryViewHolder extends RecyclerView.ViewHolder {
        ImageView itemIcon;
        TextView itemName, itemDescription;
        Button itemEquipButton;

        public InventoryViewHolder(@NonNull View itemView) {
            super(itemView);
            itemIcon = itemView.findViewById(R.id.itemIcon);
            itemName = itemView.findViewById(R.id.itemName);
            itemDescription = itemView.findViewById(R.id.itemDescription);
            itemEquipButton = itemView.findViewById(R.id.itemEquipButton);
        }
    }

}