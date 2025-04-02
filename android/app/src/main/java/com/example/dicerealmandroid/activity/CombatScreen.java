package com.example.dicerealmandroid.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;

import com.dicerealm.core.entity.BodyPart;
import com.dicerealm.core.inventory.InventoryOf;
import com.dicerealm.core.item.EquippableItem;
import com.dicerealm.core.player.Player;
import com.dicerealm.core.skills.Skill;
import com.example.dicerealmandroid.R;
import com.example.dicerealmandroid.game.GameStateHolder;
import com.example.dicerealmandroid.game.combat.CombatSequence;
import com.example.dicerealmandroid.game.combat.CombatStateHolder;
import com.example.dicerealmandroid.game.dialog.Dialog;
import com.example.dicerealmandroid.player.PlayerStateHolder;
import com.example.dicerealmandroid.room.RoomStateHolder;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CombatScreen extends AppCompatActivity {
    private RoomStateHolder roomSh = new RoomStateHolder();
    private PlayerStateHolder playerSh = new PlayerStateHolder();
    private CombatStateHolder combatSh = new CombatStateHolder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_combat_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        this.combatSequence();
        this.trackCurrentTurn();
        this.attackLeft();
        this.attackRight();
        this.openSpells();
        this.displayPlayerInfo();
    }

    private void displayPlayerInfo(){
        TextView playerInfo = findViewById(R.id.PlayerInfo);
        playerSh.getPlayer().observe(this, new Observer<Player>() {
            @Override
            public void onChanged(Player player) {
                playerInfo.setText("");
                playerInfo.setText(player.getStats().toString());
            }
        });
    }

    public void openSpells(){
        playerSh.getSkills().observe(this, new Observer<InventoryOf<Skill>>() {
            @Override
            public void onChanged(InventoryOf<Skill> skills) {
                if (skills != null) {
                    List<Skill> skillList = new ArrayList<>(skills.getItems());
                    for (Skill skill : skillList) {
                        Log.d("skill", "Skill: " + skill.getDisplayName());
                    }
                }
            }
        });
    }

    private void attackRight(){
        MaterialButton attackBtnRight = findViewById(R.id.attackButtonRight);

        playerSh.getEquippedItem(BodyPart.RIGHT_HAND).observe(this, new Observer<EquippableItem>() {
            @Override
            public void onChanged(EquippableItem equippableItem) {
                if (equippableItem != null) {
                    attackBtnRight.setText("Right Hand Attack with " + equippableItem.getDisplayName());
                } else {
                    attackBtnRight.setText("Attack with right hand");
                }
            }
        });

        attackBtnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // gameSh.performAction();
                Log.d("action right", "Attack with right hand");
            }
        });
    }

    private void attackLeft(){
        MaterialButton attackBtnLeft = findViewById(R.id.attackButtonLeft);

        playerSh.getEquippedItem(BodyPart.LEFT_HAND).observe(this, new Observer<EquippableItem>() {
            @Override
            public void onChanged(EquippableItem equippableItem) {
                if (equippableItem != null) {
                    attackBtnLeft.setText("Left Hand Attack with " + equippableItem.getDisplayName());
                } else {
                    attackBtnLeft.setText("Attack with left hand");
                }
            }
        });

        attackBtnLeft.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v) {
//                gameSh.performAction();
                Log.d("action left", "Attack with left hand");
            }
        });

    }

    private void combatSequence(){
        TextView turnText = findViewById(R.id.CombatSequence);
        turnText.setText("");
        combatSh.getCombatSequence().observe(this, new Observer<List<CombatSequence>>() {
            @Override
            public void onChanged(List<CombatSequence> combatSequences){
                for(int i = 0; i < combatSequences.size(); i++){
                    CombatSequence sequence = combatSequences.get(i);
                    if(i == 0){
                        // Mark first element as the current turn
                        turnText.append(">>> " + sequence.getName() + " - " + sequence.getInitiative() + " <<<" + "\n");
                    } else {
                        turnText.append(sequence.getName() + " - " + sequence.getInitiative() + "\n");
                    }
                }
            }
        });
    }

    private void trackCurrentTurn() {
        LinearLayout messageView = findViewById(R.id.CombatMessageLayout);
        CardView currentTurnCard = new CardView(CombatScreen.this);
        TextView currentTurnText = new TextView(CombatScreen.this);

        currentTurnCard.setCardBackgroundColor(Color.parseColor("#D9D9D9"));
        currentTurnCard.setCardElevation(10);
        currentTurnCard.setRadius(20);

        currentTurnText.setPadding(10, 10, 10, 10);

        messageView.setPadding(10, 10, 10, 10);
        messageView.setVerticalScrollBarEnabled(true);

        currentTurnCard.addView(currentTurnText);
        messageView.addView(currentTurnCard);

        combatSh.subscribeCombatLatestTurn().observe(this, new Observer<String>() {
           @Override
           public void onChanged(String currTurn){

               currentTurnText.setText(""); // Reset the text view before displaying the new message
               displayMessageStream(currTurn, currentTurnText);
           }
        });
    }

    private void displayMessageStream(String message, TextView currentTurnView){
        ScrollView messagesScroll = findViewById(R.id.messages);
        // Run this on another thread/logical core to achieve true parallelism unlike python which thread is limited by GIL
        Thread backgroundThread = new Thread(() ->{
            for(int i = 0; i < message.length(); i++){
                char currChar = message.charAt(i);
                try{
                    Thread.sleep(5);
                }catch (InterruptedException e){
                    e.printStackTrace();
                    return;
                }
                // Important to run on UI thread
                runOnUiThread(() ->{
                    currentTurnView.append(String.valueOf(currChar));
                    messagesScroll.fullScroll(ScrollView.FOCUS_DOWN);
                });
            }
        });
        backgroundThread.start();
    }
}