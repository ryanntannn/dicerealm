package com.example.dicerealmandroid.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.dicerealmandroid.core.entity.Entity;
import com.example.dicerealmandroid.core.item.Item;
import com.example.dicerealmandroid.core.player.Player;
import com.example.dicerealmandroid.game.dialog.DialogueClass;
import com.example.dicerealmandroid.R;
import com.example.dicerealmandroid.command.ShowPlayerActionsCommand;
import com.example.dicerealmandroid.core.DungeonMasterResponse;
import com.example.dicerealmandroid.game.GameStateHolder;
import com.example.dicerealmandroid.player.PlayerStateHolder;
import com.example.dicerealmandroid.util.ScreenDimensions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;

// TODO: Add player edit text input
// TODO: Add player inventory interface
// TODO: Add player stats interface popup

public class DialogScreen extends AppCompatActivity {
    private CardView selectedCardView;
    private GameStateHolder gameSh;
    private PlayerStateHolder playerSh;
    private CountDownTimer countDownTimer;

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

        LinearLayout messageLayout = findViewById(R.id.messageContainer);
        LinearLayout actionLayout = findViewById(R.id.playerActionsContainer);
        TextView timerView = findViewById(R.id.timer);

        BottomSheetDialog itemInventoryModal = new BottomSheetDialog(DialogScreen.this);
        View itemInventoryView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.modal_item_inventory, null);

        gameSh = new ViewModelProvider(this).get(GameStateHolder.class);
        playerSh = new ViewModelProvider(this).get(PlayerStateHolder.class);

        this.getTurnHistory(messageLayout);
        this.trackTurns(messageLayout, actionLayout, timerView);
        this.displayPlayerDetails(itemInventoryView);
        this.openItemInventory(itemInventoryModal, itemInventoryView);
    }

    private void getTurnHistory(LinearLayout messageLayout){
        for(DialogueClass turn : gameSh.getDialogTurnHistory()){
            // Each turn number
            TextView numberOfTurns = new TextView(DialogScreen.this);
            numberOfTurns.setText("Turn " + turn.getTurnNumber());
            numberOfTurns.setPadding(20, 20, 20, 20);
            numberOfTurns.setGravity(1);
            numberOfTurns.setTextColor(Color.parseColor("#000000"));
            numberOfTurns.setTextSize(12);

            // Each turn messages
            CardView turnContainer = new CardView(DialogScreen.this);
            TextView eachTurnView = new TextView(DialogScreen.this);

            turnContainer.setPadding(0, 10, 0, 10);
            turnContainer.setCardBackgroundColor(Color.parseColor("#D9D9D9"));  // Set background color
            eachTurnView.setText(turn.getMessage());
            eachTurnView.setPadding(20,20,20,20);

            turnContainer.addView(eachTurnView);
            messageLayout.addView(numberOfTurns);
            messageLayout.addView(turnContainer);
        }
    }



    // Keeps track of the dialog latest turn only, type out the message character by character
    private void trackTurns(LinearLayout messageLayout, LinearLayout actionLayout, TextView timerView){

        gameSh.subscribeDialogLatestTurn().observe(this, new Observer<DialogueClass>() {

            @Override
            public void onChanged(DialogueClass turn){
                int currTurn = turn.getTurnNumber();
                Optional<UUID> playerId = turn.getSender();
                String id = currTurn + playerId.toString();

                CardView turnContainer = messageLayout.findViewWithTag(id);
                TextView currentTurnView;
                TextView ownerView;

                if(turnContainer != null){

                    // Player alrdy has a card for this turn
                    currentTurnView = (TextView) turnContainer.getChildAt(0);
                    currentTurnView.setText(turn.getMessage());

                }else{

                    // Create a new card for this turn
                    turnContainer = new CardView(DialogScreen.this);
                    currentTurnView = new TextView(DialogScreen.this);
                    ownerView = new TextView(DialogScreen.this);

                    turnContainer.setCardBackgroundColor(Color.parseColor("#D9D9D9"));
                    turnContainer.setRadius(20);
                    turnContainer.setPadding(0, 20, 0, 0);

                    currentTurnView.setPadding(20,20,20,20);

                    ownerView.setTextSize(9);
                    ownerView.setPadding(0,0,0,10);

                    // Check if its DM or Player
                    if(playerId.isEmpty()){
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
                    }else{
                        // Player
                        turnContainer.setTag(id);
                        ownerView.setGravity(Gravity.END); // flush right

                        if(playerId.orElse(null).equals(playerSh.getPlayerId())){
                            ownerView.setText("You");
                        }else{
                            ownerView.setText("Other player");
                        }
                    }

                    turnContainer.addView(currentTurnView);

                    messageLayout.addView(turnContainer);
                    messageLayout.addView(ownerView);

                    currentTurnView.setText(""); // clear before start of each turn

                    // Add each turn to the message layout, action layout and start the timer
                    displayMessageStream(turn.getMessage(), currentTurnView);
                    displayActionButtons(actionLayout);
                    timer(messageLayout, timerView);
                }
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
                    Thread.sleep(10);
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


    private void displayActionButtons(LinearLayout actionLayout) {

        gameSh.subscribeDialogPlayerActions().observe(this, new Observer<ShowPlayerActionsCommand>() {
            @Override
            public void onChanged(ShowPlayerActionsCommand actions) {
                actionLayout.removeAllViews();  // Clear previous views before adding new ones

                for (DungeonMasterResponse.PlayerAction action : actions.getActions()) {

                    // Create a new CardView dynamically
                    CardView actionContainer = new CardView(DialogScreen.this);

                    // Set LayoutParams for CardView
                    LinearLayout.LayoutParams cardLayoutParams = new LinearLayout.LayoutParams(
                            600, LinearLayout.LayoutParams.MATCH_PARENT
                    );
                    cardLayoutParams.setMargins(40, 20, 40, 20);

                    // Get ripple effect attribute when a view is clicked and store in outValue
                    TypedValue outValue = new TypedValue();
                    getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true);

                    // Set CardView properties
                    actionContainer.setLayoutParams(cardLayoutParams);
                    actionContainer.setForeground(AppCompatResources.getDrawable(DialogScreen.this, outValue.resourceId));
                    actionContainer.setCardBackgroundColor(Color.parseColor("#D9D9D9"));
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
                    // Show relevant skills required using java reflection to loop through every attributes in class
                    for (Field skill : action.skillCheck.getClass().getFields()){
                        try {
                            int currentFieldVal = skill.getInt(action.skillCheck);
                            if(currentFieldVal > 0){
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

    private void setSelectedActon(CardView selectedCardView, DungeonMasterResponse.PlayerAction action){
        // Unselect prev card
        if(!selectedCardView.equals(this.selectedCardView) && this.selectedCardView != null){
            this.selectedCardView.setCardBackgroundColor(Color.parseColor("#D9D9D9"));
            this.selectedCardView.setCardElevation(20f);
        }

        selectedCardView.setCardBackgroundColor(Color.parseColor("#9B9B9B"));
        selectedCardView.setCardElevation(80f);
        this.selectedCardView = selectedCardView;
        gameSh.sendPlayerDialogAction(action);
    }


    private void displayPlayerDetails(View itemInventoryView){
        TextView username = findViewById(R.id.username);
        TextView stats = findViewById(R.id.stats);

        // Initialize player details
        username.setText(playerSh.getPlayer().getValue().getDisplayName());
        stats.setText(playerSh.getPlayer().getValue().getStats().toString());
        displayItemInventory(playerSh.getPlayer().getValue(), itemInventoryView);

        playerSh.getPlayer().observe(this, new Observer<Player>() {
           @Override
           public void onChanged(Player player){
               // When player details change, update the UI
               username.setText(player.getDisplayName());
               stats.setText(player.getStats().toString());
               displayItemInventory(player, itemInventoryView);
            }
        });
    }

    private void timer(LinearLayout messageLayout, TextView timerView){

        // Timer for player's turn
        timerView.setPadding(20, 20, 20, 20);

        Thread backgroundThread = new Thread(() -> {
            runOnUiThread(() -> {
                countDownTimer = new CountDownTimer(gameSh.getTimeLeftInMillis(), gameSh.getIntervalInMillis()){
                    @Override
                    public void onTick(long millisUntilFinished){
                        gameSh.setTimeLeftInMillis(millisUntilFinished);
                        timerView.setText("Time Left: " + millisUntilFinished / 1000);
                    }

                    @Override
                    public void onFinish() {
                        gameSh.setTimeLeftInMillis(0);
                    }
                }.start();
            });
        });
        backgroundThread.start();
    }

    private void openItemInventory(BottomSheetDialog itemInventoryModal, View bottomSheetView){
        FloatingActionButton itemInventory= findViewById(R.id.itemInventory);
        // Open player inventory
        itemInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

                // Set fixed height
                bottomSheetView.setLayoutParams(new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        ScreenDimensions.getScreenHeight() / 2
                ));
                itemInventoryModal.setContentView(bottomSheetView);
                itemInventoryModal.show();
            }
        });
    }

    private void displayItemInventory(Player player, View itemInventoryView){

        // Display player items in modal
        LinearLayout itemInventoryLayout = itemInventoryView.findViewById(R.id.itemInventoryList);
        itemInventoryLayout.removeAllViews();

        // Set LayoutParams for CardView
        LinearLayout.LayoutParams cardLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 200
        );
        cardLayoutParams.setMargins(40, 40, 40, 40);

        LinearLayout.LayoutParams btnLayoutParams = new LinearLayout.LayoutParams(
                300, 100
        );
        btnLayoutParams.setLayoutDirection(LinearLayout.HORIZONTAL);


        for(Item item : player.getInventory().getItems()){
            TextView itemTitle = new TextView(DialogScreen.this);
            TextView itemDescription = new TextView(DialogScreen.this);
            Button equipButton = new Button(DialogScreen.this);
            LinearLayout itemCardLayout = new LinearLayout(DialogScreen.this);
            CardView itemCard = new CardView(DialogScreen.this);

            // item card layout
            itemCardLayout.setOrientation(LinearLayout.VERTICAL);

            // Card properties
            itemCard.setLayoutParams(cardLayoutParams);

            // title properties
            itemTitle.setText(item.getDisplayName());

            // desc properties
            itemDescription.setText(item.getDescription());

            // btn properties
            equipButton.setText("Equip");
            equipButton.setGravity(Gravity.RIGHT);
            equipButton.setLayoutParams(btnLayoutParams);

            // Hardcode the body part for now
            equipButton.setOnClickListener(v -> {
                Log.d("info", "Equip button clicked");
                playerSh.equipItem(item.getId(), Entity.BodyPart.HEAD);
            });

            itemCardLayout.addView(itemTitle);
            itemCardLayout.addView(itemDescription);
            itemCardLayout.addView(equipButton);
            itemCard.addView(itemCardLayout);
            itemInventoryLayout.addView(itemCard);
        }
    }
}