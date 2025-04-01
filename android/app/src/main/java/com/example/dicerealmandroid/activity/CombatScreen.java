package com.example.dicerealmandroid.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;

import com.example.dicerealmandroid.R;
import com.example.dicerealmandroid.game.GameStateHolder;
import com.example.dicerealmandroid.player.PlayerStateHolder;
import com.example.dicerealmandroid.room.RoomStateHolder;

public class CombatScreen extends AppCompatActivity {
    private RoomStateHolder roomSh = new RoomStateHolder();
    private PlayerStateHolder playerSh = new PlayerStateHolder();
    private GameStateHolder gameSh = new GameStateHolder();

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

        this.trackCurrentTurn();
    }

    private void trackCurrentTurn() {
        TextView turnText = findViewById(R.id.textView3);
        gameSh.subscribeCombatLatestTurn().observe(this, new Observer<String>() {
           @Override
           public void onChanged(String currTurn){
               turnText.setText(currTurn);
            }
        });
    }
}