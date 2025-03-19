package com.example.dicerealmandroid.activity;

import android.os.Bundle;
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

// TODO: Add Action Buttons
// TODO: Add player edit text input
// TODO: Add player inventory interface
// TODO: Add player stats interface popup

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

        this.trackTurns(gameSh, messageLayout);

    }

    private void trackTurns(GameStateHolder gameSh, LinearLayout messageLayout){
        gameSh.subscribeDialogueTurnHistory().observe(this, new Observer<Queue<StartTurnCommand>>() {
            @Override
            public void onChanged(Queue<StartTurnCommand> turns){
                // Add each turn to the message container view
                for (StartTurnCommand turn: turns){
                    TextView eachTurnView = new TextView(DialogScreen.this);
                    eachTurnView.setText(turn.getDialogueTurn().getDungeonMasterText());
                    messageLayout.addView(eachTurnView);
                }

            }
        });
    }
}