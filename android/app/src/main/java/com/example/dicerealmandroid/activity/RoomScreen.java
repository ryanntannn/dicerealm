package com.example.dicerealmandroid.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.dicerealmandroid.R;
import com.example.dicerealmandroid.core.Player;
import com.example.dicerealmandroid.room.RoomRepo;
import com.example.dicerealmandroid.room.RoomStateHolder;

public class RoomScreen extends AppCompatActivity {

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
        RoomStateHolder roomSh = new ViewModelProvider(this).get(RoomStateHolder.class);
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
}