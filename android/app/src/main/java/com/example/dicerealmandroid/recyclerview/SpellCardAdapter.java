package com.example.dicerealmandroid.recyclerview;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dicerealm.core.command.combat.CombatTurnActionCommand;
import com.dicerealm.core.entity.Entity;
import com.dicerealm.core.skills.Skill;
import com.example.dicerealmandroid.R;
import com.example.dicerealmandroid.activity.CombatScreen;
import com.example.dicerealmandroid.game.combat.CombatStateHolder;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class SpellCardAdapter extends CardAdapter<Skill> {
    CombatScreen Combat;
    Entity target;
    public SpellCardAdapter(Context context, List<Skill> item,  String type, CombatStateHolder combatSh , CombatScreen combat) {
        super(context, item,type,combatSh);
        this.Combat = combat;
        this.target = target;
    }

    @NonNull
    @Override
    public CardAdapter.CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.skillcard, parent, false);
        return new CardAdapter.CardViewHolder(itemView );
    }

    @Override
    public void onBindViewHolder(@NonNull CardAdapter.CardViewHolder holder, int position) {
        
        Skill skill = item.get(position);
        holder.skillbutton.setText(skill.getDisplayName());
        // rmb to disable button
        if (skill.isUsable()){
            holder.textViewName.setText("Usable, Cooldown:" + (skill.getCooldown()) + " turns");
            holder.skillbutton.setBackgroundColor(Color.BLACK);
        }else {
            holder.textViewName.setText("Remaining Cooldown:" + skill.getRemainingCooldown());
            holder.skillbutton.setBackgroundColor(Color.GRAY);
        }
        holder.skillbutton.setOnClickListener(new View.OnClickListener() {
        int pos = holder.getAdapterPosition();
            @Override
            public void onClick(View v) {
                Entity target = combatsh.getSelectedTarget().getValue();
                if (skill.isUsable()) {
                    if (target != null) {
                        combatsh.performAction(item.get(pos), CombatTurnActionCommand.ActionType.SKILL, target);
                        Combat.close();
                    }
                }
            }
        });
    }
}

