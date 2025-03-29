package com.example.dicerealmandroid.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.dicerealmandroid.game.GameStateHolder;
import com.example.dicerealmandroid.handler.BackButtonHandler;
import com.example.dicerealmandroid.R;
import com.dicerealm.core.player.Player;
import com.example.dicerealmandroid.room.RoomStateHolder;

public class RoomScreen extends AppCompatActivity {
    private

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_room_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Observe if game has started
        GameStateHolder gameStateHolder = new ViewModelProvider(this).get(GameStateHolder.class);
        gameStateHolder.isGameRunning().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isGameRunning) {
                if (isGameRunning) {
                    Intent intent = new Intent(RoomScreen.this, DialogScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });


        RoomStateHolder roomSh = new ViewModelProvider(this).get(RoomStateHolder.class);
        GameStateHolder gameSh = new ViewModelProvider(this).get(GameStateHolder.class);

        BackButtonHandler.setupBackImageButtonHandler(this, R.id.backBtn);

        this.startGame(R.id.startGameBtn, gameSh);
        this.setRoomCode(roomSh);
        this.trackPlayers(roomSh);
    }

    private void setRoomCode(RoomStateHolder roomSh){
        TextView roomCode = findViewById(R.id.roomCode);
        String roomCodeText = "Room Code: " + roomSh.getRoomCode();
        roomCode.setText(roomCodeText);
    }

    private void trackPlayers(RoomStateHolder roomSh){
        roomSh.trackAllPlayers().observe(this, new Observer<Player[]>() {
            @Override
            public void onChanged(Player[] players){
                TextView playerCount = findViewById(R.id.amtOfPlayers);
                String counter = players.length + "/4";
                playerCount.setText(counter);
            }
        });
    }

    private void startGame(int id, GameStateHolder gameSh){
        Button startGame = findViewById(id);
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                gameSh.startGame();
            }
        });
    }
}