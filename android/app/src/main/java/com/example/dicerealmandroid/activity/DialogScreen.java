package com.example.dicerealmandroid.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dicerealm.core.dialogue.SkillCheck;
import com.dicerealm.core.entity.Stat;
import com.dicerealm.core.entity.StatsMap;
import com.dicerealm.core.item.EquippableItem;
import com.dicerealm.core.item.Potion;
import com.dicerealm.core.item.Scroll;
import com.dicerealm.core.locations.Location;
import com.dicerealm.core.player.Player;
import com.dicerealm.core.room.RoomState;
import com.example.dicerealmandroid.adapters.LocationAdapter;
import com.dicerealm.core.skills.Skill;
import com.example.dicerealmandroid.fragments.DiceDialogFragment;
import com.example.dicerealmandroid.fragments.InventoryDialogFragment;
import com.example.dicerealmandroid.game.dialog.Dialog;
import com.example.dicerealmandroid.R;
import com.dicerealm.core.command.ShowPlayerActionsCommand;
import com.dicerealm.core.dm.DungeonMasterResponse;
import com.example.dicerealmandroid.game.GameStateHolder;
import com.example.dicerealmandroid.game.dialog.DialogStateHolder;
import com.example.dicerealmandroid.player.wrapper.PlayerInventoryWrapper;
import com.example.dicerealmandroid.player.PlayerStateHolder;
import com.example.dicerealmandroid.room.RoomStateHolder;
import com.example.dicerealmandroid.util.ScreenDimensions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

// TODO: Add player edit text input(Optional)
// TODO: Roll dice modal when action has skillcheck
// TODO: Add Location Visited traversal history

public class DialogScreen extends AppCompatActivity {
	private CardView selectedCardView;
	private CardView waitingForPartyCard;
	private GameStateHolder gameSh;
	private PlayerStateHolder playerSh;
	private RoomStateHolder roomSh;
	private DialogStateHolder dialogSh;
	private LocationAdapter locationAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EdgeToEdge.enable(this);
		setContentView(R.layout.activity_dialog_screen);
		ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
			Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
			v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
			return insets;
		});
		// Hide android bottom nav bar
		View decorView = getWindow().getDecorView();
		WindowInsetsControllerCompat controller = ViewCompat.getWindowInsetsController(decorView);



		if (controller != null) {
			controller.hide(WindowInsetsCompat.Type.systemBars());
			controller.setSystemBarsBehavior(
					WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
		}

		LinearLayout messageLayout = findViewById(R.id.messageContainer);
		LinearLayout actionLayout = findViewById(R.id.playerActionsContainer);

		BottomSheetDialog itemInventoryModal = new BottomSheetDialog(DialogScreen.this);
		View itemInventoryView =
				LayoutInflater.from(getApplicationContext()).inflate(R.layout.modal_item_inventory, null);

		gameSh = new ViewModelProvider(this).get(GameStateHolder.class);
		playerSh = new ViewModelProvider(this).get(PlayerStateHolder.class);
		roomSh = new ViewModelProvider(this).get(RoomStateHolder.class);
		dialogSh = new ViewModelProvider(this).get(DialogStateHolder.class);

		this.trackTurns(messageLayout, actionLayout);
		this.displayPlayerDetails(itemInventoryView);
		this.openItemInventory();
		this.trackGameServer(messageLayout, actionLayout, itemInventoryView);
		this.trackLocation();
		this.showActionResults();
	}

	private void showActionResults() {
		dialogSh.subscribeDialogLatestActionResult().observe(this,
				new Observer<SkillCheck.ActionResultDetail>() {
					@Override
					public void onChanged(SkillCheck.ActionResultDetail actionResultDetail) {
						DiceDialogFragment diceDialogFragment =
								DiceDialogFragment.newInstance(actionResultDetail);
						diceDialogFragment.show(getSupportFragmentManager(), "DiceDialog");
					}
				});
	}

	private void trackLocation() {
		// For the location dropdown styling
		GradientDrawable shape = new GradientDrawable();
		shape.setCornerRadius(15); // 15dp rounded corners
		shape.setColor(Color.argb(204, 255, 255, 255)); // White with 80% opacity (204/255 = 80%)

		LinearLayout locationDropdown = findViewById(R.id.topBarRight);
		// Connect to adapter
		locationAdapter = new LocationAdapter();

		MaterialTextView currentLocationText = findViewById(R.id.location);
		currentLocationText.setTextSize(11);
		currentLocationText.setTextColor(getResources().getColor(R.color.white, null));

		locationDropdown.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				// Inflate the custom layout for the spinner
				View dropdownView =
						LayoutInflater.from(DialogScreen.this).inflate(R.layout.location_dropdown, null);

				TextView currentLocationDialogText = dropdownView.findViewById(R.id.currLocation);
				TextView currentLocationDialogDesc = dropdownView.findViewById(R.id.currLocationDesc);

				// Set the current location text in the spinner dialog through the adapter
				if (locationAdapter.getCurrentLocation() != null) {
					currentLocationDialogText.setText(locationAdapter.getCurrentLocation().getDisplayName());
					currentLocationDialogDesc.setText(locationAdapter.getCurrentLocation().getDescription());
				}

				RecyclerView locationRecyclerView = dropdownView.findViewById(R.id.locationRecyclerView);
				locationRecyclerView.setAdapter(locationAdapter);
				locationRecyclerView.setLayoutManager(
						new LinearLayoutManager(DialogScreen.this, LinearLayoutManager.VERTICAL, false));

				PopupWindow popupWindow = new PopupWindow(dropdownView, ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT, true);

				popupWindow.setElevation(10);
				popupWindow.setBackgroundDrawable(shape);
				popupWindow.setHeight(ScreenDimensions.getScreenHeight() / 3);
				popupWindow.setWidth(3 * ScreenDimensions.getScreenWidth() / 4);
				popupWindow.showAsDropDown(locationDropdown, 0, 0);
			}
		});


		gameSh.getCurrentLocation().observe(this, new Observer<Location>() {
			@Override
			public void onChanged(Location location) {
				locationAdapter.updateCurrentLocation(location);
				currentLocationText.setText(location.getDisplayName());
			}
		});

		gameSh.getAdjacentLocations().observe(this, new Observer<List<Location>>() {
			@Override
			public void onChanged(List<Location> locations) {
				locationAdapter.updateAdjacentLocations(locations);
			}
		});
	}

	private void trackGameServer(LinearLayout messageLayout, LinearLayout actionLayout,
			View itemInventoryView) {
		CardView dmCard = new CardView(DialogScreen.this);
		TextView dmMessage = new TextView(DialogScreen.this);

		// Set LayoutParams for CardView
		LinearLayout.LayoutParams cardLayoutParams =
				new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 150);
		cardLayoutParams.setMargins(30, 15, 30, 15);

		// dm prop
		dmCard.setCardBackgroundColor(getResources().getColor(R.color.lightgrayText, null));
		dmCard.setRadius(10);
		dmCard.setPadding(40, 40, 40, 40);
		dmCard.setLayoutParams(cardLayoutParams);

		dmMessage.setText("Dungeon Master is thinking...");
		dmMessage.setTextSize(18);
		dmMessage.setTextColor(getColor(R.color.darkred));
		dmMessage.setPadding(20, 20, 20, 20);
		dmMessage.setGravity(Gravity.CENTER);
		dmCard.addView(dmMessage);

		// Tracks turn status and set accordingly the message and button's status
		roomSh.trackState().observe(this, new Observer<RoomState.State>() {
			@Override
			public void onChanged(RoomState.State state) {
				if (state == RoomState.State.DIALOGUE_PROCESSING) {
					// Remove "Waiting for Party" card if it exists
					if (waitingForPartyCard != null) {
						messageLayout.removeView(waitingForPartyCard);
						waitingForPartyCard = null;
					}

					// Show dungeon master is thinking and disable action buttons
					if (dmCard.getParent() == null) {
						messageLayout.addView(dmCard);
						disableButtons(actionLayout);
					}
				} else if (state == RoomState.State.DIALOGUE_TURN) {
					// Remove dungeon master is thinking and enable action buttons
					messageLayout.removeView(dmCard);
					enableButtons(actionLayout);
				} else if (state == RoomState.State.BATTLE) {
					// Navigate to combat screen
					Intent intent = new Intent(DialogScreen.this, CombatScreen.class);
					startActivity(intent);
				}
			}
		});
	}

	private void disableButtons(LinearLayout actionLayout) {
		FloatingActionButton itemInventory = findViewById(R.id.itemInventory);
		// Disable action buttons
		for (int i = 0; i < actionLayout.getChildCount(); i++) {
			CardView actionContainer = (CardView) actionLayout.getChildAt(i);
			actionContainer
					.setCardBackgroundColor(getResources().getColor(R.color.palepurpleCardPress, null));
			actionContainer.setClickable(false);
		}
		// Disable item inventory
		itemInventory.setEnabled(false);
	}

	private void enableButtons(LinearLayout actionLayout) {
		FloatingActionButton itemInventory = findViewById(R.id.itemInventory);
		// Enable action btns
		for (int i = 0; i < actionLayout.getChildCount(); i++) {
			CardView actionContainer = (CardView) actionLayout.getChildAt(i);
			actionContainer.setCardBackgroundColor(getResources().getColor(R.color.palepurpleCard, null));
			actionContainer.setClickable(true);
		}
		// Enable item inventory
		itemInventory.setEnabled(true);
	}


	// Keeps track of the dialog latest turn only, type out the message character by character
	private void trackTurns(LinearLayout messageLayout, LinearLayout actionLayout) {

		dialogSh.subscribeDialogLatestTurn().observe(this, new Observer<Dialog>() {

			@Override
			public void onChanged(Dialog turn) {
				int currTurn = turn.getTurnNumber();
				Optional<UUID> playerId = turn.getSender();
				String id = currTurn + playerId.toString();

				CardView turnContainer = messageLayout.findViewWithTag(id);
				TextView currentTurnView;
				TextView ownerView;

				if (turnContainer != null) {

					// Player alrdy has a card for this turn
					currentTurnView = (TextView) turnContainer.getChildAt(0);
					currentTurnView.setText(turn.getMessage());

				} else {

					// Create a new card for this turn
					turnContainer = new CardView(DialogScreen.this);
					currentTurnView = new TextView(DialogScreen.this);
					ownerView = new TextView(DialogScreen.this);

					turnContainer.setCardBackgroundColor(Color.parseColor("#D9D9D9"));
					turnContainer.setRadius(20);
					turnContainer.setPadding(0, 20, 0, 0);

					currentTurnView.setPadding(20, 20, 20, 20);

					ownerView.setTextSize(9);
					ownerView.setPadding(0, 0, 0, 10);

					// Check if its DM or Player
					if (playerId.isEmpty()) {
						// DM
						TextView numberOfTurns = new TextView(DialogScreen.this);
						numberOfTurns.setPadding(20, 0, 20, 20);
						numberOfTurns.setGravity(1); // center
						numberOfTurns.setTextColor(Color.parseColor("#000000"));
						numberOfTurns.setTextSize(12);

						numberOfTurns.setText("Turn " + currTurn);
						ownerView.setText("Dungeon Master");
						ownerView.setGravity(Gravity.START); // flush left

						messageLayout.addView(numberOfTurns);
					} else {
						// Player
						turnContainer.setTag(id);
						ownerView.setGravity(Gravity.END); // flush right

						Player[] players = roomSh.getAllPlayers();
						Player player1 = playerSh.getPlayer().getValue();

						// Show all players name for their respective action
						for (Player player : players) {
							if (playerId.orElse(null).equals(player.getId())) {

								// Check if player is you
								if (playerId.orElse(null).equals(playerSh.getPlayerId())) {
									ownerView.setText("You");
								} else {
									ownerView.setText(player.getDisplayName());
								}
							}
						}
					}

					turnContainer.addView(currentTurnView);

					messageLayout.addView(turnContainer);
					messageLayout.addView(ownerView);

					currentTurnView.setText(""); // clear before start of each turn

					// Add each turn to the message layout and action layout
					displayMessageStream(turn.getMessage(), currentTurnView);
					displayActionButtons(actionLayout);
				}
			}
		});
	}

	private void displayMessageStream(String message, TextView currentTurnView) {
		ScrollView messagesScroll = findViewById(R.id.messages);
		// Run this on another thread/logical core to achieve true parallelism unlike python which
		// thread is limited by GIL
		Thread backgroundThread = new Thread(() -> {
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


	private void displayActionButtons(LinearLayout actionLayout) {

		dialogSh.subscribeDialogPlayerActions().observe(this, new Observer<ShowPlayerActionsCommand>() {
			@Override
			public void onChanged(ShowPlayerActionsCommand actions) {
				actionLayout.removeAllViews(); // Clear previous views before adding new ones

				for (DungeonMasterResponse.PlayerAction action : actions.getActions()) {

					// Create a new CardView dynamically
					CardView actionContainer = new CardView(DialogScreen.this);

					// Set LayoutParams for CardView
					LinearLayout.LayoutParams cardLayoutParams =
							new LinearLayout.LayoutParams(600, LinearLayout.LayoutParams.MATCH_PARENT);
					cardLayoutParams.setMargins(40, 20, 40, 20);

					// Get ripple effect attribute when a view is clicked and store in outValue
					TypedValue outValue = new TypedValue();
					getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue,
							true);

					// Set CardView properties
					actionContainer.setLayoutParams(cardLayoutParams);
					actionContainer.setForeground(
							AppCompatResources.getDrawable(DialogScreen.this, outValue.resourceId));
					actionContainer
							.setCardBackgroundColor(getResources().getColor(R.color.palepurpleCard, null));
					actionContainer.setCardElevation(20f);
					actionContainer.setClickable(true);
					actionContainer.setRadius(20);
					actionContainer.setFocusable(true);
					actionContainer.setOnClickListener(v -> {
						setSelectedActon(actionContainer, action);
					});

					// TextView to display the action text
					TextView actionView = new TextView(DialogScreen.this);
					actionView.setText(action.action + "\n");
					actionView.setSingleLine(false);
					actionView.setPadding(20, 20, 20, 20);

					// SkillChecks
					// Show relevant skills required using java reflection to loop through every attributes in
					// class
					for (Field skill : action.skillCheck.getClass().getFields()) {
						try {
							int currentFieldVal = skill.getInt(action.skillCheck);
							if (currentFieldVal > 0) {
								actionView.append("\n" + skill.getName() + ": " + currentFieldVal);
							}
						} catch (IllegalAccessException e) {
							throw new RuntimeException(e);
						}
					}

					actionContainer.addView(actionView);
					actionLayout.addView(actionContainer);
				}
			}
		});
	}

	private void setSelectedActon(CardView selectedCardView,
			DungeonMasterResponse.PlayerAction action) {
		// Unselect prev card
		if (!selectedCardView.equals(this.selectedCardView) && this.selectedCardView != null) {
			this.selectedCardView
					.setCardBackgroundColor(getResources().getColor(R.color.palepurpleCard, null));
			this.selectedCardView.setCardElevation(20f);
		}

		selectedCardView
				.setCardBackgroundColor(getResources().getColor(R.color.palepurpleCardPress, null));
		selectedCardView.setCardElevation(80f);
		this.selectedCardView = selectedCardView;
		dialogSh.sendPlayerDialogAction(action);

		// Check if player has selected an action this turn, if so add the "Waiting for Party" card
		dialogSh.subscribeDialogLatestTurn().observe(this, new Observer<Dialog>() {
			@Override
			public void onChanged(Dialog turn) {
				if (turn.getSender().isPresent() && turn.getSender().get().equals(playerSh.getPlayerId())) {
					LinearLayout messageLayout = findViewById(R.id.messageContainer);

					// Remove previous "Waiting for Party" card if it exists
					if (waitingForPartyCard != null) {
						messageLayout.removeView(waitingForPartyCard);
						waitingForPartyCard = null;
					}
					// Player has selected an action, add the "Waiting for Party" card
					waitingForPartyCard = new CardView(DialogScreen.this);
					TextView waitMessage = new TextView(DialogScreen.this);

					// Set LayoutParams for CardView
					LinearLayout.LayoutParams cardLayoutParams =
							new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 150);
					cardLayoutParams.setMargins(40, 40, 40, 40);

					// Waiting card
					waitingForPartyCard.setRadius(10);
					waitingForPartyCard.setPadding(30, 15, 30, 15);
					waitingForPartyCard.setLayoutParams(cardLayoutParams);

					waitMessage.setText("Waiting for Party to do their action...");
					waitMessage.setTextColor(getColor(R.color.darkred));
					waitMessage.setTextSize(18);
					waitMessage.setPadding(20, 20, 20, 20);
					waitMessage.setGravity(Gravity.CENTER);

					try {
						if (roomSh.getRoomState() != RoomState.State.DIALOGUE_PROCESSING) {
							waitingForPartyCard.addView(waitMessage);
							Log.d("WaitingForParty", "Waiting for party card is not null");
							messageLayout.addView(waitingForPartyCard);
						}
					} catch (NullPointerException e) {
						Log.d("Error", "Waiting for party card is null");
					}
				}
			}
		});
	}

	private void displayPlayerDetails(View itemInventoryView) {
		TextView username = findViewById(R.id.username);
		TextView health = findViewById(R.id.health);
		TextView race_entityclass = findViewById(R.id.race_entityclass);
		int[] statsIds = gameSh.getStatsIds();

		playerSh.getPlayer().observe(this, new Observer<Player>() {
			@Override
			public void onChanged(Player player) {
				// When player details change, update the UI
				username.setText(player.getDisplayName());
				health.setText(playerSh.remainingHealth());
				race_entityclass.setText(player.getRace().name() + " " + player.getEntityClass().name());
				// Update player stats
				try {
					List<Stat> sortedStats = new ArrayList<>(player.getStats().keySet());
					Collections.sort(sortedStats, Comparator.comparing(Enum::name));
					Log.d("DisplayStats", "displayPlayerDetails: " + sortedStats);
					int currentStatId = 0;
					for (Stat stat : sortedStats) {
						// we render max health separately
						if (stat == Stat.MAX_HEALTH) {
							continue;
						}
						int id = statsIds[currentStatId++];
						TextView currentStat = findViewById(id);
						currentStat.setText(StatsMap.getStatText(stat) + ": " + player.getStat(stat));
					}
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void openItemInventory() {
		FloatingActionButton itemInventory = findViewById(R.id.itemInventory);
		// Open player inventory dialog
		itemInventory.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Player inventory interface for All Items: EquippableItems, EquippedItems, Potions,
				// Scrolls

				PlayerInventoryWrapper inventoryData =
						new PlayerInventoryWrapper(playerSh.getSpecificInventoryType(EquippableItem.class),
								playerSh.getEquippedItems(), playerSh.getSpecificInventoryType(Potion.class),
								playerSh.getSpecificInventoryType(Scroll.class));
				InventoryDialogFragment inventoryDialog =
						InventoryDialogFragment.newInstance(inventoryData, playerSh);

				inventoryDialog.show(getSupportFragmentManager(), "InventoryDialog");

			}
		});
	}


}
