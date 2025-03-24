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

import com.example.dicerealmandroid.core.item.InventoryOf;
import com.example.dicerealmandroid.core.item.Item;
import com.example.dicerealmandroid.core.skill.Skill;
import com.example.dicerealmandroid.game.GameStateHolder;
import com.example.dicerealmandroid.handler.BackButtonHandler;
import com.example.dicerealmandroid.R;
import com.example.dicerealmandroid.core.entity.Entity;
import com.example.dicerealmandroid.core.player.Player;
import com.example.dicerealmandroid.core.player.PresetPlayerFactory;
import com.example.dicerealmandroid.player.PlayerStateHolder;
import com.example.dicerealmandroid.room.RoomStateHolder;


public class CharacterScreen extends AppCompatActivity {
    private Player selectedPlayer;
    private PlayerStateHolder playerSh;
    private RoomStateHolder roomSh;
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

        // Observe if game has started
        GameStateHolder gameStateHolder = new ViewModelProvider(this).get(GameStateHolder.class);
        gameStateHolder.isGameRunning().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isGameRunning) {
                if (isGameRunning) {
                    Intent intent = new Intent(CharacterScreen.this, DialogScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });

        // Access PlayerStateHolder
        playerSh = new ViewModelProvider(this).get(PlayerStateHolder.class);
        roomSh = new ViewModelProvider(this).get(RoomStateHolder.class);
        BackButtonHandler.setupBackImageButtonHandler(this, R.id.backToLobby);
        this.setRoomCode();

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

                // Keep the default inventory and skills inventory for the selected player as generated by PresetPlayerFactory
                selectedPlayer.updateInventory(playerSh.getPlayer().getValue().getInventory());
                selectedPlayer.updateSkillsInventory(playerSh.getPlayer().getValue().getSkillsInventory());
                playerSh.updatePlayerRequest(selectedPlayer);

                // Navigate to lobby waiting screen when Next button is clicked
                Intent intent = new Intent(CharacterScreen.this, RoomScreen.class);
                startActivity(intent);
            }
        });

    }
    private void setRoomCode(){
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
            selectedPlayer = new Player(chara_name1.getText().toString(),
                    Entity.Race.DWARF ,
                    Entity.EntityClass.WARRIOR,
                    Entity.ClassStats.getStatsForClass(Entity.EntityClass.WARRIOR));

        } else if (viewId == R.id.character2) {
            selectedPlayer = new Player(chara_name2.getText().toString(),
                    Entity.Race.HUMAN ,
                    Entity.EntityClass.WIZARD,
                    Entity.ClassStats.getStatsForClass(Entity.EntityClass.WIZARD));

        } else if (viewId == R.id.character3) {
            selectedPlayer = new Player(chara_name3.getText().toString(),
                    Entity.Race.TIEFLING ,
                    Entity.EntityClass.ROGUE,
                    Entity.ClassStats.getStatsForClass(Entity.EntityClass.ROGUE));

        } else if (viewId == R.id.character4) {
            // Archer
            selectedPlayer = new Player(chara_name4.getText().toString(),
                    Entity.Race.ELF ,
                    Entity.EntityClass.RANGER,
                    Entity.ClassStats.getStatsForClass(Entity.EntityClass.RANGER));
        }
        Log.d("CharacterScreen", "Selected Player: " + selectedPlayer.getDisplayName()+" "+selectedPlayer.getRace()+" "+selectedPlayer.getEntityClass());
    }
}