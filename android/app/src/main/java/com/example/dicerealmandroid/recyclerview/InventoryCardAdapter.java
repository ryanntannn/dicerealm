package com.example.dicerealmandroid.recyclerview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.dicerealm.core.item.Item;
import com.dicerealm.core.item.Potion;
import com.dicerealm.core.skills.Skill;
import com.example.dicerealmandroid.R;

import java.util.List;

public class InventoryCardAdapter extends CardAdapter<Potion>{
    public InventoryCardAdapter(Context context, List<Potion> item, SelectListener listener, String type) {
        super(context, item, listener, type);
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.skillcard, parent, false);
        return new CardAdapter.CardViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        Item potionsandscrolls = item.get(position);
        holder.skillbutton.setText(potionsandscrolls.getDisplayName());
    }
}
