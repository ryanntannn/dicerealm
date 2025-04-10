package com.example.dicerealmandroid.recyclerview;
import com.dicerealm.core.item.Item;
import com.dicerealm.core.skills.Skill;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dicerealmandroid.R;
import com.example.dicerealmandroid.game.combat.CombatStateHolder;
import com.google.android.material.button.MaterialButton;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

//idk where to add this , rmb to ask jj after
public abstract class CardAdapter<T> extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    Context context;
    LayoutInflater mInflater;

    List<T> item;

    SelectListener listener;
    CombatStateHolder combatsh;
    static String type;
    public CardAdapter(Context context, List<T> item, SelectListener listener, String type, CombatStateHolder combatsh){
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.item = item;
        this.listener = listener;
        this.type = type;
        this.combatsh = combatsh;
    }

    @NonNull
    @Override
    public abstract CardAdapter.CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType);

    @Override
    public abstract void onBindViewHolder(@NonNull CardAdapter.CardViewHolder holder, int position);

    @Override
    public int getItemCount() {
        return item.size();
    }
    static class CardViewHolder extends RecyclerView.ViewHolder{

        MaterialButton skillbutton;
        TextView textViewName;

        CardViewHolder(View view, SelectListener listener){ // view means the current activity that we are in. Hence to use findViewById
            // you have to use view.findviewbyid
            super(view);
            // initialize fields
            skillbutton = view.findViewById(R.id.skillbutton);
            textViewName = view.findViewById(R.id.cardtext);
            // set onclick listener
            skillbutton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    skillbutton.setClickable(false);
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position,type);
                        }
                    }
                }
            });
        }

    }

}
