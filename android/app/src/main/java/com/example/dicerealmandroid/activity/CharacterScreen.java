package com.example.dicerealmandroid.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
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

        ImageButton backButton = findViewById(R.id.backToLobby);
        backButton.setOnClickListener(v -> finish());

        // Loop through the character IDs and create the event listeners
        int[] characterIds = {R.id.character1, R.id.character2, R.id.character3, R.id.character4};
        for (int characterId : characterIds) {
            LinearLayout character = findViewById(characterId);
            character.setOnClickListener(v -> handleCharacterClick(characterId));
        }

    }
//    TODO Update Player Object with selected character
    public void handleCharacterClick(int viewId) {
        if (viewId == R.id.character1) {
            Log.d("Character", "Character 1 clicked");
        } else if (viewId == R.id.character2) {
            Log.d("Character", "Character 2 clicked");
        } else if (viewId == R.id.character3) {
            Log.d("Character", "Character 3 clicked");
        } else if (viewId == R.id.character4) {
            Log.d("Character", "Character 4 clicked");
        }
    }
}