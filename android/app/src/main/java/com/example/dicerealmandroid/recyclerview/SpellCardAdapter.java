package com.example.dicerealmandroid.recyclerview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dicerealm.core.skills.Skill;
import com.example.dicerealmandroid.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class SpellCardAdapter extends CardAdapter<Skill> {
    public SpellCardAdapter(Context context, List<Skill> item, SelectListener listener, String type) {
        super(context, item, listener,type);
    }

    @NonNull
    @Override
    public CardAdapter.CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.skillcard, parent, false);
        return new CardAdapter.CardViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull CardAdapter.CardViewHolder holder, int position) {
        Skill skill = item.get(position);
        holder.skillbutton.setText(skill.getDisplayName());
        if (skill.isUsable()){
            holder.textViewName.setText("Usable, Cooldown:" + (skill.getCooldown()) + " turns");
        }
        holder.textViewName.setText("Remaining Cooldown:" + skill.getRemainingCooldown());
    }
}

