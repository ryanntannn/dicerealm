package com.example.dicerealmandroid.activity;

import static com.dicerealm.core.entity.Entity.Allegiance.ENEMY;

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
import androidx.core.view.WindowInsetsControllerCompat;
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
import com.example.dicerealmandroid.Color_hashmap.Colorhashmap;
import com.example.dicerealmandroid.R;
import com.example.dicerealmandroid.fragments.InitCombatFragment;
import com.example.dicerealmandroid.game.GameStateHolder;
import com.example.dicerealmandroid.game.combat.CombatSequence;
import com.example.dicerealmandroid.game.combat.CombatStateHolder;
import com.example.dicerealmandroid.game.combat.CombatTurnModal;
import com.example.dicerealmandroid.player.PlayerStateHolder;
import com.example.dicerealmandroid.recyclerview.CardAdapter;
import com.example.dicerealmandroid.recyclerview.InventoryCardAdapter;
import com.example.dicerealmandroid.recyclerview.SpellCardAdapter;
import com.example.dicerealmandroid.room.RoomStateHolder;
import com.example.dicerealmandroid.util.Initcombat;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import android.widget.LinearLayout.LayoutParams;

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

	private Initcombat initcombatscreen;

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
			copy.add(result.clone()); // or use a custom copy constructor
		}

		// Hide android bottom nav bar
		View decorView = getWindow().getDecorView();
		WindowInsetsControllerCompat controller = ViewCompat.getWindowInsetsController(decorView);

		if (controller != null) {
			controller.hide(WindowInsetsCompat.Type.systemBars());
			controller.setSystemBarsBehavior(
					WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
		}



        roomSh.trackState().observe(this, new Observer<RoomState.State>() {
            @Override
            public void onChanged(RoomState.State roomState) {
                if (roomState == null) {
                    Log.d("CombatScreen", "Navigating back to home screen");
                    Intent intent = new Intent(CombatScreen.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else if (roomState == RoomState.State.DIALOGUE_PROCESSING) {
                    Log.d("CombatScreen", "Navigating back to dialog screen");
                    CombatScreen.this.finish();
                }
            }
        });
        Log.d("InitMessage:" , "CombatScreen " + combatSh.getinitmessage());
        initcombatscreen = new Initcombat(this, combatSh.getinitmessage());
        initcombatscreen.show();
        this.combatSequence(copy);
        this.trackCurrentTurn();
        this.attackLeft();
        this.attackRight();
        this.openSpells();
        this.displayPlayerInfo();
        this.selectMonster();
        this.closespell();
        this.useitems();
        this.trackCurrentRound();
    }

    private void selectMonster() {
        // Observe the monsters list and set the initial target
        combatSh.getMonsters().observe(this, monsters -> {
            if (monsters != null && !monsters.isEmpty()) {
                // Set the initial target if needed
                combatSh.setInitialTarget();

                // Create monster selection button
                MaterialButton selectTargetButton = findViewById(R.id.selectTargetButton);
                selectTargetButton.setOnClickListener(v -> {
                    combatSh.selectNextTarget();
                });

                // Update the UI dynamically when the monsters list changes
                Entity currentTarget = combatSh.getSelectedTarget().getValue();
                if (currentTarget != null) {
                    for (Entity monster : monsters) {
                        if (monster.getId().equals(currentTarget.getId())) {
                            updateEnemyInfo(monster);
                            break;
                        }
                    }
                }
            }
        });

        // Observe selected target changes
        combatSh.getSelectedTarget().observe(this, this::updateEnemyInfo);
    }


    private void updateEnemyInfo(Entity target) {
        TextView enemyName = findViewById(R.id.enemyName);
        TextView enemyHealth = findViewById(R.id.enemyHealth);

        if (target != null) {
            enemyName.setText(target.getDisplayName());
            enemyHealth.setText(target.getHealth() + "/" + target.getStat(Stat.MAX_HEALTH));
        } else {
            enemyName.setText("No target");
            enemyHealth.setText("0/0");
        }
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

    public void openSpells() {
        playerSh.getSkills().observe(this, new Observer<InventoryOf<Skill>>() {
            @Override
            public void onChanged(InventoryOf<Skill> skills) {
                if (skills != null) {
                    List<Skill> skillList = new ArrayList<>(skills.getItems());


                    // Hardcode the button to use the first skill
                    MaterialButton skillButtons = findViewById(R.id.spellButton);
                    skillButtons.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            //Make the actions layout invisible
                            ConstraintLayout actions = (ConstraintLayout) findViewById(R.id.ActionsAvailable);
                            actions.setVisibility(View.GONE);
                            //Show the spells layout
                            ConstraintLayout spellaction = (ConstraintLayout) findViewById(R.id.SpellActions);
                            spellaction.setVisibility(View.VISIBLE);
                            for (Skill skills : skillList) {
                                Log.d("skill", "Skill: " + skills.getDisplayName());
                            }
                            //Call recycleview

                            CardAdapter cardAdapter = new SpellCardAdapter(CombatScreen.this,skillList,"Spell",combatSh , CombatScreen.this);

                            RecyclerView recyclerView = findViewById(R.id.cardRecycleView);
                            recyclerView.setAdapter(cardAdapter);
                            recyclerView.setLayoutManager(new GridLayoutManager(CombatScreen.this, 2));

                        }
                    });
                }
            }
        });
    }


    public void closespell() {

        // Hardcode the button to use the first skill

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

	public void useitems() {
		playerSh.getScrolls_Potions().observe(this, new Observer<InventoryOf<Item>>() {
			@Override
			public void onChanged(InventoryOf<Item> Potions_Scroll) {
				if (Potions_Scroll != null) {
					List<Item> Potions_Scrolllist = new ArrayList<>(Potions_Scroll.getItems());
					List<Item> remove_item = new ArrayList<>();
					for (int i = 0; i < Potions_Scrolllist.size(); i++) {
						Log.d("itemtype", Potions_Scrolllist.get(i).getType());
						if (!Objects.equals(Potions_Scrolllist.get(i).getType(), "POTION")
								&& !Objects.equals(Potions_Scrolllist.get(i).getType(), "SCROLL")) {
							remove_item.add(Potions_Scrolllist.get(i));
						}
					}
					for (Item removeitem : remove_item) {
						Potions_Scrolllist.remove(removeitem);
					}



                    // Hardcode the button to use the first skill
                    MaterialButton Itembutton = findViewById(R.id.itemButton);
                    Itembutton.setOnClickListener(new View.OnClickListener() {
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
                            recyclerView.setLayoutManager(new GridLayoutManager(CombatScreen.this, 2));

                        }
                    });
                }
            }
        });
    }

    private void attackRight() {
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
                        Entity target = combatSh.getSelectedTarget().getValue();
                        Log.d("action right", "Attack with right hand");
                        if (target != null) {
                            combatSh.performAction(equippableItem, CombatTurnActionCommand.ActionType.WEAPON, target);
                        } else {
                            Log.d("Combat", "No target selected");
                        }
                    }
                });
            }
        });


    }

    private void attackLeft() {
        MaterialButton attackBtnLeft = findViewById(R.id.attackButtonLeft);

        playerSh.getEquippedItem(BodyPart.LEFT_HAND).observe(this, new Observer<EquippableItem>() {
            @Override
            public void onChanged(EquippableItem equippableItem) {
                if (equippableItem != null) {
                    attackBtnLeft.setText("Left-H Attack: " + equippableItem.getDisplayName());
                } else {
                    attackBtnLeft.setText("Left-H Attack: Fist");
                }

                attackBtnLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Entity target = combatSh.getSelectedTarget().getValue();
                        Log.d("action left", "Attack with left hand");
                        if (target != null) {
                            combatSh.performAction(equippableItem, CombatTurnActionCommand.ActionType.WEAPON, target);
                        } else {
                            Log.d("Combat", "No target selected");
                        }
                    }
                });
            }
        });

    }


    private void combatSequence(List<InitiativeResult> initiativeResults){
        // So that we can disable the buttons
        MaterialButton attackBtnLeft = findViewById(R.id.attackButtonLeft);
        MaterialButton attackBtnRight = findViewById(R.id.attackButtonRight);
        MaterialButton spellBtn = findViewById(R.id.spellButton);
        MaterialButton itemBtn = findViewById(R.id.itemButton);
        int enabledColor = getResources().getColor(R.color.purple);
        int disabledColor = Color.GRAY;

        TableLayout turntable = findViewById(R.id.turnCombatSquence);

        combatSh.getCombatSequence().observe(this, new Observer<List<CombatSequence>>() {
            @Override
            public void onChanged(List<CombatSequence> combatSequences) {
                if(combatSh.isMyTurn()){
                    // Set enabled state and background colors
                    attackBtnLeft.setEnabled(true);
                    attackBtnLeft.setBackgroundColor(enabledColor);
                    attackBtnRight.setEnabled(true);
                    attackBtnRight.setBackgroundColor(enabledColor);
                    spellBtn.setEnabled(true);
                    spellBtn.setBackgroundColor(enabledColor);
                    itemBtn.setEnabled(true);
                    itemBtn.setBackgroundColor(enabledColor);
                }
                else{
                    // Set disabled state and background colors
                    attackBtnLeft.setEnabled(false);
                    attackBtnLeft.setBackgroundColor(disabledColor);
                    attackBtnRight.setEnabled(false);
                    attackBtnRight.setBackgroundColor(disabledColor);
                    spellBtn.setEnabled(false);
                    spellBtn.setBackgroundColor(disabledColor);
                    itemBtn.setEnabled(false);
                    itemBtn.setBackgroundColor(disabledColor);
                }

                Log.d("combat", "Initiative Results:" + initiativeResults.toString());
                turntable.removeAllViews();
                List<InitiativeResult> removeplayer = new ArrayList<>();

				for (InitiativeResult player_enemy : initiativeResults) {
					if (combatSequences.stream()
							.anyMatch(r -> r.getuuid().equals(player_enemy.getEntity().getId()))) {
						TableRow newtablerow = new TableRow(CombatScreen.this);
						TextView nameView = new TextView(CombatScreen.this);
						int padding = 16;
						nameView.setPadding(padding, padding, padding, padding);
						nameView.setMaxWidth(400);
						Log.d("nameofevery", player_enemy.getEntity().getDisplayName() + "    "
								+ combatSequences.get(0).getName());
						String viewforname = "";

						if (player_enemy.getEntity().getId().equals(combatSequences.get(0).getuuid())) {
							// Mark first element as the current turn
							if (!player_enemy.getEntity().getAllegiance().equals(ENEMY)) {
								nameView.setBackgroundResource(R.drawable.bold_cell_border_green);
								viewforname = gameSh.getplayercolor(player_enemy.getEntity().getId());
							} else {
								nameView.setBackgroundResource(R.drawable.bold_cell_border_red);
							}
						} else {
							if (!player_enemy.getEntity().getAllegiance().equals(ENEMY)) {
								viewforname = gameSh.getplayercolor(player_enemy.getEntity().getId());
								nameView.setBackgroundResource(R.drawable.cell_border_green);
							} else {
								nameView.setBackgroundResource(R.drawable.cell_border_red);
							}
						}

						viewforname += player_enemy.getEntity().getDisplayName() + " - "
								+ player_enemy.getInitiativeRoll();
						if (player_enemy.getEntity().getId().equals(playerSh.getPlayerId())) {
							viewforname += " (You)";
						}
						nameView.setText(viewforname);
						newtablerow.addView(nameView);
						turntable.addView(newtablerow);

                        }

                    }



            }
        });
    }

    private void trackCurrentTurn() {
        LinearLayout messageLayout = findViewById(R.id.CombatMessageLayout);

		combatSh.subscribeCombatLatestTurn().observe(this, new Observer<CombatTurnModal>() {
			@Override
			public void onChanged(CombatTurnModal currTurn) {
				if(currTurn == null) return;

				// Card design
				CardView messageCard = new CardView(CombatScreen.this);
				LayoutParams cardParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				cardParams.setMargins(8, 8, 8, 8);
				messageCard.setLayoutParams(cardParams);
				messageCard.setCardElevation(4);
				messageCard.setRadius(12);

				// Create content layout inside card
				LinearLayout cardContent = new LinearLayout(CombatScreen.this);
				cardContent.setOrientation(LinearLayout.VERTICAL);
				cardContent.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				cardContent.setPadding(16, 16, 16, 16);

				// Turn number header
				TextView turnHeader = new TextView(CombatScreen.this);
				turnHeader.setText("Turn " + currTurn.getTurn());
				turnHeader.setTypeface(null, Typeface.BOLD);
				turnHeader.setTextSize(16);
				cardContent.addView(turnHeader);

				// Message content
				String messageContent = "";
				if (currTurn.getHitLog() != null) {
					messageContent = currTurn.getHitLog();
				}
				if (currTurn.getMessage() != null) {
					if (!messageContent.isEmpty()) {
						messageContent += "  ";
					}
					messageContent += currTurn.getMessage();
				}

				// Message text view
				TextView messageText = new TextView(CombatScreen.this);
				messageText.setPadding(0, 8, 0, 0);
				messageText.setTextSize(14);
				cardContent.addView(messageText);

				messageCard.addView(cardContent);
				messageLayout.addView(messageCard);

				messageText.setText("");

				// Display msg with animation
				displayMessageStream(messageContent, messageText);
			}
		});
	}

    private void trackCurrentRound(){
        LinearLayout messageLayout = findViewById(R.id.CombatMessageLayout);
        TextView turncombattext = findViewById(R.id.turnCombat);

		combatSh.getCurrentRound().observe(this, new Observer<Integer>() {
			@Override
			public void onChanged(Integer round) {
				CardView roundCard = new CardView(CombatScreen.this);
				LayoutParams cardParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				cardParams.setMargins(8, 20, 8, 4);
				roundCard.setLayoutParams(cardParams);
				roundCard.setCardElevation(6); // Higher elevation than message cards
				roundCard.setRadius(12);
				roundCard.setCardBackgroundColor(Color.TRANSPARENT);

				// Round header text
				TextView roundText = new TextView(CombatScreen.this);
				roundText.setText("Round: " + round);
                turncombattext.setText("Combat Round: " + round);
				roundText.setTextSize(18);
				roundText.setTypeface(null, Typeface.BOLD);
				roundText.setTextColor(Color.WHITE);
				roundText.setPadding(16, 12, 16, 12);
				roundText.setGravity(android.view.Gravity.CENTER);
				roundText.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

				// Add text to card
				roundCard.addView(roundText);

				// Add round header to layout
				messageLayout.addView(roundCard);
				}
		});
	}

    private void displayMessageStream(String message, TextView currentTurnView) {
        ScrollView messagesScroll = findViewById(R.id.messages);

		// Run this on another thread/logical core to achieve true parallelism unlike python which
		// thread is limited by GIL
		Thread backgroundThread = new Thread(() -> {
			if (message == null || message.isEmpty())
				return;

			for (int i = 0; i < message.length(); i++) {
				char currChar = message.charAt(i);
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
				// Important to run on UI thread
				runOnUiThread(() -> {
					currentTurnView.append(String.valueOf(currChar));
					messagesScroll.fullScroll(ScrollView.FOCUS_DOWN);
				});
			}
		});
		backgroundThread.start();
	}

}