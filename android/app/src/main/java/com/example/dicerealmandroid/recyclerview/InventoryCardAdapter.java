package com.example.dicerealmandroid.recyclerview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.dicerealm.core.command.combat.CombatTurnActionCommand;
import com.dicerealm.core.item.Item;
import com.dicerealm.core.item.Potion;
import com.dicerealm.core.skills.Skill;
import com.example.dicerealmandroid.R;
import com.example.dicerealmandroid.game.combat.CombatStateHolder;

import java.util.List;

public class InventoryCardAdapter extends CardAdapter<Item>{
    public InventoryCardAdapter(Context context, List<Item> item, SelectListener listener, String type , CombatStateHolder combatSh) {
        super(context, item, listener, type , combatSh);
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.skillcard, parent, false);
        return new CardAdapter.CardViewHolder(itemView, listener );
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        Item potionsandscrolls = item.get(position);
        holder.skillbutton.setText(potionsandscrolls.getDisplayName());
        holder.textViewName.setText("One time usage");
        holder.skillbutton.setOnClickListener(new View.OnClickListener() {
            int pos = holder.getAdapterPosition();
            @Override
            public void onClick(View v) {
                combatsh.performAction(item.get(pos), CombatTurnActionCommand.ActionType.SKILL);
            }
        });
    }
}
