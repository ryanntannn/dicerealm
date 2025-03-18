package com.example.dicerealmandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
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

import com.dicerealm.core.entity.ClassStats;
import com.dicerealm.core.entity.EntityClass;
import com.dicerealm.core.entity.Race;
import com.example.dicerealmandroid.handler.BackButtonHandler;
import com.example.dicerealmandroid.R;
import com.dicerealm.core.entity.Entity;
import com.dicerealm.core.player.Player;
import com.dicerealm.core.player.PresetPlayerFactory;
import com.example.dicerealmandroid.player.PlayerStateHolder;
import com.example.dicerealmandroid.room.RoomStateHolder;


public class CharacterScreen extends AppCompatActivity {
    private Player selectedPlayer;
    LinearLayout previousSelected = null;

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

        // Access PlayerStateHolder
        PlayerStateHolder playerSh = new ViewModelProvider(this).get(PlayerStateHolder.class);
        RoomStateHolder roomSh = new ViewModelProvider(this).get(RoomStateHolder.class);
        BackButtonHandler.setupBackImageButtonHandler(this, R.id.backToLobby);
        this.setRoomCode(roomSh);

        TextView chara_name1 = findViewById(R.id.chara_name1);
        TextView chara_name2 = findViewById(R.id.chara_name2);
        TextView chara_name3 = findViewById(R.id.chara_name3);
        TextView chara_name4 = findViewById(R.id.chara_name4);
        TextView chara_description1 = findViewById(R.id.chara_description1);
        TextView chara_description2 = findViewById(R.id.chara_description2);
        TextView chara_description3 = findViewById(R.id.chara_description3);
        TextView chara_description4 = findViewById(R.id.chara_description4);

        // Randomized Preset Character Names
        chara_name1.setText(PresetPlayerFactory.getRandomCharacterName());
        chara_name2.setText(PresetPlayerFactory.getRandomCharacterName());
        chara_name3.setText(PresetPlayerFactory.getRandomCharacterName());
        chara_name4.setText(PresetPlayerFactory.getRandomCharacterName());
        // Preset Character Races and Entity Classes
        chara_description1.setText("DWARF" + " " + "WARRIOR");
        chara_description2.setText("HUMAN" + " " + "WIZARD");
        chara_description3.setText("TIEFLING" + " " + "ROGUE");
        chara_description4.setText("ELF" + " " + "RANGER");


        playerSh.getPlayer().observe(this, new Observer<Player>() {
            @Override
            public void onChanged(Player player){
                if (player != null){
                    Log.d("Player", "Player has been updated"+player.getId());
                }
            }
        });

        // Create event listeners for clickable containers
        int[] characterIds = {R.id.character1, R.id.character2, R.id.character3, R.id.character4};
        for (int characterId : characterIds) {
            LinearLayout character = findViewById(characterId);
            character.setOnClickListener(v -> handleCharacterClick(characterId));
        }
        // Event Listeners for Next button
        Button nextButton = findViewById(R.id.nextButton);
        // Disable the Next button initially until a character is selected
        nextButton.setEnabled(false);
        nextButton.setOnClickListener(v -> {
            if (selectedPlayer != null) {
                playerSh.updatePlayerRequest(selectedPlayer);
                // Navigate to lobby waiting screen when Next button is clicked
                Intent intent = new Intent(CharacterScreen.this, RoomScreen.class);
                startActivity(intent);
            }
        });

    }
    private void setRoomCode(RoomStateHolder roomSh){
        TextView roomCode = findViewById(R.id.roomCode);
        String roomCodeText = "Room Code: " + roomSh.getRoomCode();
        roomCode.setText(roomCodeText);
    }

    // Handle selected character clicked
    public void handleCharacterClick(int viewId) {
        TextView chara_name1 = findViewById(R.id.chara_name1);
        TextView chara_name2 = findViewById(R.id.chara_name2);
        TextView chara_name3 = findViewById(R.id.chara_name3);
        TextView chara_name4 = findViewById(R.id.chara_name4);

        // If a character was previously selected, Remove the previous border
        if (previousSelected != null) {
            previousSelected.setBackgroundResource(0);
        }
        else {
            Button nextButton = findViewById(R.id.nextButton);
            nextButton.setEnabled(true);
        }

        LinearLayout selectedContainer = findViewById(viewId);
        // Set the black border for the currently selected character
        selectedContainer.setBackgroundResource(R.drawable.border);
        // Update the previousSelected to the current one
        previousSelected = selectedContainer;

        if (viewId == R.id.character1) {
            selectedPlayer = new Player(chara_name1.getText().toString(), Race.DWARF , EntityClass.WARRIOR, ClassStats.getStatsForClass(EntityClass.WARRIOR));
        } else if (viewId == R.id.character2) {
            selectedPlayer = new Player(chara_name2.getText().toString(), Race.HUMAN , EntityClass.WIZARD, ClassStats.getStatsForClass(EntityClass.WIZARD));
        } else if (viewId == R.id.character3) {
            selectedPlayer = new Player(chara_name3.getText().toString(), Race.TIEFLING ,EntityClass.ROGUE, ClassStats.getStatsForClass(EntityClass.ROGUE));
        } else if (viewId == R.id.character4) {
            selectedPlayer = new Player(chara_name4.getText().toString(), Race.ELF ,EntityClass.RANGER, ClassStats.getStatsForClass(EntityClass.RANGER));
        }
        Log.d("CharacterScreen", "Selected Player: " + selectedPlayer.getDisplayName()+" "+selectedPlayer.getRace()+" "+selectedPlayer.getEntityClass());
    }
}