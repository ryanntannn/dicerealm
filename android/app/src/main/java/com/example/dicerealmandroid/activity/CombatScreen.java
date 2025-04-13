package com.example.dicerealmandroid.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dicerealm.core.combat.systems.InitiativeResult;
import com.dicerealm.core.command.combat.CombatTurnActionCommand;
import com.dicerealm.core.entity.BodyPart;
import com.dicerealm.core.entity.Entity;
import com.dicerealm.core.entity.Stat;
import com.dicerealm.core.entity.StatsMap;
import com.dicerealm.core.inventory.InventoryOf;
import com.dicerealm.core.item.EquippableItem;
import com.dicerealm.core.item.Item;
import com.dicerealm.core.player.Player;
import com.dicerealm.core.room.RoomState;
import com.dicerealm.core.skills.Skill;
import com.example.dicerealmandroid.R;
import com.example.dicerealmandroid.game.GameStateHolder;
import com.example.dicerealmandroid.game.combat.CombatSequence;
import com.example.dicerealmandroid.game.combat.CombatStateHolder;
import com.example.dicerealmandroid.player.PlayerStateHolder;
import com.example.dicerealmandroid.recyclerview.CardAdapter;
import com.example.dicerealmandroid.recyclerview.InventoryCardAdapter;
import com.example.dicerealmandroid.recyclerview.SpellCardAdapter;
import com.example.dicerealmandroid.room.RoomStateHolder;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


// TODO: Implement Weapon attack functionality (DONE)
// TODO: Implement spell functionality
// TODO: Implement item functionality (Show potions and scrolls)


// TODO: Refactor the classes to move the attributes in datasource to repo (datasouces shouldnt be holding data only talking to the server, make repo the singleton)
//          - Make sure sub-repos is not a cycle
//          - Maybe can make the DicerealmClient a singleton and can remove the roomDataSource
// TODO: Refactor code again to implement dependencies injection if got time

public class CombatScreen extends AppCompatActivity {
    private GameStateHolder gameSh = new GameStateHolder();
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
        List<InitiativeResult> initiativeRes = combatSh.initiativeResults();
        List<InitiativeResult> copy = new ArrayList<>();
        for (InitiativeResult result : initiativeRes) {
            copy.add(result.clone());  // or use a custom copy constructor
        }

        roomSh.trackState().observe(this, new Observer<RoomState.State>() {
            @Override
            public void onChanged(RoomState.State roomState) {
                if (roomState == null){
                    Log.d("CombatScreen", "Navigating back to home screen");
                    Intent intent = new Intent(CombatScreen.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else if(roomState == RoomState.State.DIALOGUE_PROCESSING){
                    Log.d("CombatScreen", "Navigating back to dialog screen");
                    CombatScreen.this.finish();
                }
            }
        });
        this.combatSequence(copy);
        this.trackCurrentTurn();
        this.attackLeft();
        this.attackRight();
        this.openSpells();
        this.displayPlayerInfo();
        this.displayEnemyInfo();
        this.closespell();
        this.useitems();
    }

    private void displayEnemyInfo(){
        TextView enemyName = findViewById(R.id.enemyName);
        TextView enemyHealth = findViewById(R.id.enemyHealth);
        combatSh.getMonster().observe(this, new Observer<Entity>(){
            @Override
            public void onChanged(Entity monster){
                enemyName.setText(monster.getDisplayName());
                enemyHealth.setText(monster.getHealth() + "/" + monster.getStat(Stat.MAX_HEALTH));
            }
        });
    }

    private void displayPlayerInfo(){
        TextView playerName = findViewById(R.id.playerName);
        TextView yourHealth = findViewById(R.id.yourHealth);
        int[] statsIds = gameSh.getStatsIds();
        playerSh.getPlayer().observe(this, new Observer<Player>() {
            @Override
            public void onChanged(Player player) {
                playerName.setText(player.getDisplayName() + "(you)");
                yourHealth.setText(player.getHealth() + "/" + player.getStat(Stat.MAX_HEALTH));

                try {
                    List<Stat> sortedStats = new ArrayList<>(player.getStats().keySet());
                    Collections.sort(sortedStats, Comparator.comparing(Enum::name));
                    Log.d("DisplayStats", "displayPlayerDetails: "+sortedStats);
                    int currentStatId = 0;
                    for (Stat stat : sortedStats) {
                        // we render max health separately
                        if (stat == Stat.MAX_HEALTH) {
                            continue;
                        }
                        int id = statsIds[currentStatId++];
                        TextView currentStat = findViewById(id);
                        if (StatsMap.getStatText(stat) == "Armour Class"){
                            currentStat.setText(StatsMap.getStatText(stat) + ": " + player.getStat(stat));
                        }
                        else{
                            currentStat.setText(StatsMap.getStatText(stat).substring(0,3) + ": " + player.getStat(stat));
                        }

                    }
                }
                catch (NullPointerException e){
                    e.printStackTrace();
                }
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

                    // Hardcode the button to use the first skill
                    MaterialButton skillButtons = findViewById(R.id.spellButton);
                    skillButtons.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {

                            //Make the actions layout invisible
                            ConstraintLayout actions = (ConstraintLayout) findViewById(R.id.ActionsAvailable);
                            actions.setVisibility(View.GONE);
                            //Show the spells layout
                            ConstraintLayout spellaction = (ConstraintLayout) findViewById(R.id.SpellActions);
                            spellaction.setVisibility(View.VISIBLE);
                            for (Skill skills : skillList){
                                Log.d("skill", "Skill: " + skills.getDisplayName());
                            }
                            //Call recycleview
                            CardAdapter cardAdapter = new SpellCardAdapter(CombatScreen.this,skillList,"Spell",combatSh , CombatScreen.this);
                            RecyclerView recyclerView = findViewById(R.id.cardRecycleView);
                            recyclerView.setAdapter(cardAdapter);
                            recyclerView.setLayoutManager(new GridLayoutManager(CombatScreen.this,2));

                        }
                    });
                }
            }
        });
    }

    public void closespell(){
        MaterialButton skillButtons = findViewById(R.id.BackButton);
        skillButtons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Make the actions layout invisible
                close();
            }
        });
    }
    public void close(){
        ConstraintLayout actions = (ConstraintLayout) findViewById(R.id.ActionsAvailable);
        actions.setVisibility(View.VISIBLE);
        //Show the spells layout
        ConstraintLayout spellaction = (ConstraintLayout) findViewById(R.id.SpellActions);
        spellaction.setVisibility(View.GONE);
        //Scrollable view
        RecyclerView cardRecycleView = (RecyclerView) findViewById(R.id.cardRecycleView);
        cardRecycleView.removeAllViews();
    }

    public void useitems(){
        playerSh.getScrolls_Potions().observe(this, new Observer<InventoryOf<Item>>() {
            @Override
            public void onChanged(InventoryOf<Item> Potions_Scroll) {
                if (Potions_Scroll != null) {
                    List<Item> Potions_Scrolllist = new ArrayList<>(Potions_Scroll.getItems());

                    for (int i = 0 ; i < Potions_Scrolllist.size(); i++) {
                        if (Potions_Scrolllist.get(i).getType() != "POTION" || Potions_Scrolllist.get(i).getType() != "SCROLL"){
                            Potions_Scrolllist.remove(i);
                        }
                    }


                    // Hardcode the button to use the first skill
                    MaterialButton Itembutton = findViewById(R.id.itemButton);
                    Itembutton.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            Log.d("Open Inventory", "Inventory Opened");
                            //Make the actions layout invisible
                            ConstraintLayout actions = (ConstraintLayout) findViewById(R.id.ActionsAvailable);
                            actions.setVisibility(View.GONE);
                            //Show the spells layout
                            ConstraintLayout spellaction = (ConstraintLayout) findViewById(R.id.SpellActions);
                            spellaction.setVisibility(View.VISIBLE);

                            //Call recycleview
                            CardAdapter cardAdapter = new InventoryCardAdapter(CombatScreen.this,Potions_Scrolllist ,"Item",combatSh, CombatScreen.this);
                            RecyclerView recyclerView = findViewById(R.id.cardRecycleView);
                            recyclerView.setAdapter(cardAdapter);
                            recyclerView.setLayoutManager(new GridLayoutManager(CombatScreen.this,2));

                        }
                    });
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
                    attackBtnRight.setText("Right-H Attack: " + equippableItem.getDisplayName());
                } else {
                    attackBtnRight.setText("Right-H Attack: Fist");
                }

                attackBtnRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("action right", "Attack with right hand");
                        combatSh.performAction(equippableItem, CombatTurnActionCommand.ActionType.WEAPON);
                    }
                });
            }
        });


    }

    private void attackLeft(){
        MaterialButton attackBtnLeft = findViewById(R.id.attackButtonLeft);

        playerSh.getEquippedItem(BodyPart.LEFT_HAND).observe(this, new Observer<EquippableItem>() {
            @Override
            public void onChanged(EquippableItem equippableItem) {
                if (equippableItem != null) {
                    attackBtnLeft.setText("Left-H Attack: " + equippableItem.getDisplayName());
                } else {
                    attackBtnLeft.setText("Left-H Attack: Fist");
                }

                attackBtnLeft.setOnClickListener(new View.OnClickListener()  {
                    @Override
                    public void onClick(View v) {
                        Log.d("action left", "Attack with left hand");
                        combatSh.performAction(equippableItem, CombatTurnActionCommand.ActionType.WEAPON);
                    }
                });
            }
        });

    }

    private void combatSequence(List<InitiativeResult> initiativeResults){
        TableLayout turntable = findViewById(R.id.turnCombatSquence);

        combatSh.getCombatSequence().observe(this, new Observer<List<CombatSequence>>() {
            @Override
            public void onChanged(List<CombatSequence> combatSequences){
                Log.d("combat", "Initiative Results:" + initiativeResults.toString());
                turntable.removeAllViews();
//                for(int i = 0; i < combatSequences.size(); i++){
//                    TableRow newtablerow = new TableRow(CombatScreen.this);
//                    TextView nameView = new TextView(CombatScreen.this);
//                    int padding = 16;
//                    nameView.setPadding(padding, padding, padding, padding);
//                    nameView.setMaxWidth(400);
//                    nameView.setBackgroundResource(R.drawable.cell_border);
//                    CombatSequence sequence = combatSequences.get(i);
//                    if(combatSh.isMyTurn()){
//                        Log.d("turn", combatSh.isMyTurn().toString());
//                        // Mark first element as the current turn
//                        nameView.setTypeface(null, Typeface.BOLD);
//                        nameView.setText(sequence.getName() + " - " + sequence.getInitiative());
//                        nameView.setBackgroundResource(R.drawable.bold_cell_border);
//                    } else {
//                        nameView.setText(sequence.getName() + " - " + sequence.getInitiative());
//                        nameView.setBackgroundResource(R.drawable.cell_border);
//                    }
//                    newtablerow.addView(nameView);
//                    turntable.addView(newtablerow);
//                }
                List<InitiativeResult> removeplayer = new ArrayList<>();
                for (InitiativeResult player_init : initiativeResults) {
                    boolean player_present = false;
                    for(CombatSequence player_combat: combatSequences){
                        if(player_init.getEntity().getDisplayName().equals(player_combat.getName())){
                            player_present = true;
                        }
                    }
                    if(!player_present){
                        removeplayer.add(player_init);
                    }
                }
                for (InitiativeResult remove : removeplayer) {
                    initiativeResults.remove(remove);
                }

                for(int i = 0; i < initiativeResults.size(); i++){
                    TableRow newtablerow = new TableRow(CombatScreen.this);
                    TextView nameView = new TextView(CombatScreen.this);
                    int padding = 16;
                    nameView.setPadding(padding, padding, padding, padding);
                    nameView.setMaxWidth(400);
                    nameView.setBackgroundResource(R.drawable.cell_border);
                    InitiativeResult sequence = initiativeResults.get(i);
                    Log.d("nameofevery",sequence.getEntity().getDisplayName() +"    " +combatSequences.get(0).getName());
                    if(sequence.getEntity().getDisplayName().equals(combatSequences.get(0).getName())){
                        // Mark first element as the current turn
                        nameView.setTypeface(null, Typeface.BOLD);
                        nameView.setText(sequence.getEntity().getDisplayName() + " - " + sequence.getInitiativeRoll());
                        nameView.setBackgroundResource(R.drawable.bold_cell_border);
                    } else {
                        nameView.setText(sequence.getEntity().getDisplayName() + " - " + sequence.getInitiativeRoll());
                        nameView.setBackgroundResource(R.drawable.cell_border);
                    }
                    newtablerow.addView(nameView);
                    turntable.addView(newtablerow);
                }

            }
        });
    }

    private void trackCurrentTurn() {
        LinearLayout messageView = findViewById(R.id.CombatMessageLayout);


        combatSh.subscribeCombatLatestTurn().observe(this, new Observer<String>() {
           @Override
           public void onChanged(String currTurn){
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