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
import com.example.dicerealmandroid.player.PlayerStateHolder;

public class CharacterScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_character_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        PlayerStateHolder player_sh = new ViewModelProvider(this).get(PlayerStateHolder.class);
        TextView textView = findViewById(R.id.textView2);


        player_sh.getPlayer().observe(this, new Observer<Player>() {
            @Override
            public void onChanged(Player player){
                if (player != null){
                    textView.setText(player.getDisplayName());
                }
            }
        });

    }
}