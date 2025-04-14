package com.example.dicerealmandroid.recyclerview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.dicerealm.core.command.combat.CombatTurnActionCommand;
import com.dicerealm.core.entity.Entity;
import com.dicerealm.core.item.Item;
import com.example.dicerealmandroid.R;
import com.example.dicerealmandroid.activity.CombatScreen;
import com.example.dicerealmandroid.game.combat.CombatStateHolder;

import java.util.List;
import java.util.Objects;

public class InventoryCardAdapter extends CardAdapter<Item>{
    CombatScreen Combat;
    public InventoryCardAdapter(Context context, List<Item> item, String type , CombatStateHolder combatSh , CombatScreen Combat) {
        super(context, item,  type , combatSh);
        this.Combat = Combat;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.skillcard, parent, false);
        return new CardAdapter.CardViewHolder(itemView);
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
                Entity target = combatsh.getSelectedTarget().getValue();
                if(target != null) {
                    if (Objects.equals(item.get(pos).getType(), "SCROLL")) {
                        combatsh.performAction(item.get(pos), CombatTurnActionCommand.ActionType.SCROLL, target);
                    } else if (Objects.equals(item.get(pos).getType(), "POTION")) {
                        combatsh.performAction(item.get(pos), CombatTurnActionCommand.ActionType.POTION, target);
                    }
                    Combat.close();
                }
            }
        });
    }
}
