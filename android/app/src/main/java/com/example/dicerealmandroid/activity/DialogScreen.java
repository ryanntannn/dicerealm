package com.example.dicerealmandroid.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.dicerealmandroid.R;
import com.example.dicerealmandroid.command.dialogue.StartTurnCommand;
import com.example.dicerealmandroid.core.player.Player;
import com.example.dicerealmandroid.game.GameStateHolder;
import com.example.dicerealmandroid.room.RoomStateHolder;

import java.util.Queue;
import java.util.UUID;

// TODO: Add Action Buttons
// TODO: Add player edit text input
// TODO: Add player inventory interface
// TODO: Add player stats interface popup

// TODO: Change turn showing such that it adds just 1 messsage at a time rather than all at once, causing slow rendering and lag
// TODO: Add a count down timer for the player's turn

public class DialogScreen extends AppCompatActivity {

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
        GameStateHolder gameSh = new ViewModelProvider(this).get(GameStateHolder.class);

        this.getTurnHistory(gameSh, messageLayout);
        this.trackTurns(gameSh, messageLayout);
    }

    private void getTurnHistory(GameStateHolder gameSh, LinearLayout messageLayout){
        for(StartTurnCommand turn : gameSh.getDialogTurnHistory()){
            TextView eachTurnView = new TextView(DialogScreen.this);
            eachTurnView.setText(turn.getDialogueTurn().getDungeonMasterText());
            messageLayout.addView(eachTurnView);
        }
    }

    private void trackTurns(GameStateHolder gameSh, LinearLayout messageLayout){
        // Keeps track of the dialog latest turn only, type out the message character by character
        TextView currentTurnView = new TextView(DialogScreen.this);
        currentTurnView.setText("Dungeon Master is thinking....");
        messageLayout.addView(currentTurnView);

        gameSh.subscribeDialogLatestTurn().observe(this, new Observer<StartTurnCommand>() {
            @Override
            public void onChanged(StartTurnCommand turn){
                currentTurnView.setText("");
                // Add each turn to the message container view
                displayMessageStream(turn.getDialogueTurn().getDungeonMasterText(), currentTurnView);
//                displayActionButtons(turn.getDialogueTurn().getDialogueTurnAction());
            }
        });
    }

    private void displayMessageStream(String message, TextView currentTurnView){
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            int i =0;

            @Override
            public void run(){
                if(i < message.length()){
                    currentTurnView.append(String.valueOf(message.charAt(i)));
                    i++;
                    handler.postDelayed(this, 10); // Delay by 10ms between each char
                }
            }
        };
        handler.post(runnable);
    }

    private void displayActionButtons(){

    }

}