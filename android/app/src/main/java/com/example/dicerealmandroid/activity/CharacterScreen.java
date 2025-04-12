package com.example.dicerealmandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
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
import com.dicerealm.core.inventory.InventoryOf;
import com.dicerealm.core.item.Item;
import com.dicerealm.core.item.Potion;
import com.dicerealm.core.item.Scroll;
import com.dicerealm.core.room.RoomState;
import com.dicerealm.core.skills.Skill;
import com.example.dicerealmandroid.game.GameStateHolder;
import com.example.dicerealmandroid.handler.BackButtonHandler;
import com.example.dicerealmandroid.R;
import com.dicerealm.core.entity.Entity;
import com.dicerealm.core.player.Player;
import com.dicerealm.core.player.PresetPlayerFactory;
import com.example.dicerealmandroid.player.PlayerStateHolder;
import com.example.dicerealmandroid.room.RoomStateHolder;
import com.example.dicerealmandroid.util.Loading;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class CharacterScreen extends AppCompatActivity {
    private Player selectedPlayer;
    private PlayerStateHolder playerSh;
    private RoomStateHolder roomSh;
    LinearLayout previousSelected = null;
    private Loading loading;

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
        playerSh = new ViewModelProvider(this).get(PlayerStateHolder.class);
        roomSh = new ViewModelProvider(this).get(RoomStateHolder.class);

        this.backToHome();
        this.setRoomCode();

        TextView chara_name1 = findViewById(R.id.chara_name1);
        TextView chara_name2 = findViewById(R.id.chara_name2);
        TextView chara_name3 = findViewById(R.id.chara_name3);
        TextView chara_name4 = findViewById(R.id.chara_name4);
        TextView chara_description1 = findViewById(R.id.chara_description1);
        TextView chara_description2 = findViewById(R.id.chara_description2);
        TextView chara_description3 = findViewById(R.id.chara_description3);
        TextView chara_description4 = findViewById(R.id.chara_description4);

        // Randomized Preset Character Names, Make sure that the names are unique
        Set<String> uniqueNames = new HashSet<>();
        chara_name1.setText(getUniqueCharacterName(uniqueNames));
        chara_name2.setText(getUniqueCharacterName(uniqueNames));
        chara_name3.setText(getUniqueCharacterName(uniqueNames));
        chara_name4.setText(getUniqueCharacterName(uniqueNames));

        // Preset Character Races and Entity Classes
        chara_description1.setText("DWARF" + " " + "WARRIOR");
        chara_description2.setText("HUMAN" + " " + "WIZARD");
        chara_description3.setText("TIEFLING" + " " + "ROGUE");
        chara_description4.setText("ELF" + " " + "RANGER");


        playerSh.getPlayer().observe(this, new Observer<Player>() {
            @Override
            public void onChanged(Player player) {
                if (player != null) {
                    Log.d("Player", "Player has been updated" + player.getId());
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


        // Observe if game has started and user is still in the character screen, if so use the randomized character provided by the server when first joining the room
        loading = new Loading(this, "Initializing Game, using randomized character generated by server...");
        RoomStateHolder roomSh = new ViewModelProvider(this).get(RoomStateHolder.class);
        roomSh.trackState().observe(this, new Observer<RoomState.State>() {
            @Override
            public void onChanged(RoomState.State state) {
                if (state == RoomState.State.DIALOGUE_PROCESSING) {
                    loading.show();
                } else if (state == RoomState.State.DIALOGUE_TURN) {
                    loading.hide();
                    Intent intent = new Intent(CharacterScreen.this, DialogScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });
    }

    private void backToHome() {
        BackButtonHandler.setupBackImageButtonHandler(this, R.id.backToLobby);
    }

    private void setRoomCode() {
        TextView roomCode = findViewById(R.id.roomCode);
        String roomCodeText = "Room Code: " + roomSh.getRoomCode();
        roomCode.setText(roomCodeText);
    }

    private String getUniqueCharacterName(Set<String> uniqueNames) {
        String name;
        do {
            name = PresetPlayerFactory.getRandomCharacterName();
        } while (uniqueNames.contains(name));
        uniqueNames.add(name);
        return name;
    }

    // Handle selected character clicked
    public void handleCharacterClick(int viewId) {
        TextView chara_name1 = findViewById(R.id.chara_name1);
        TextView chara_name2 = findViewById(R.id.chara_name2);
        TextView chara_name3 = findViewById(R.id.chara_name3);
        TextView chara_name4 = findViewById(R.id.chara_name4);

        Button nextButton = findViewById(R.id.nextButton);
        // If a character was previously selected, Remove the previous border
        if (previousSelected != null) {
            previousSelected.setBackgroundResource(0);
        } else {
            nextButton.setEnabled(true);
        }

        LinearLayout selectedContainer = findViewById(viewId);
        // Set the black border for the currently selected character
        selectedContainer.setBackgroundResource(R.drawable.border);
        // Update the previousSelected to the current one
        previousSelected = selectedContainer;

        if (viewId == R.id.character1) {
            selectedPlayer = new Player(chara_name1.getText().toString(),
                    Race.DWARF,
                    EntityClass.WARRIOR,
                    ClassStats.getStatsForClass(EntityClass.WARRIOR));

        } else if (viewId == R.id.character2) {
            selectedPlayer = new Player(chara_name2.getText().toString(),
                    Race.HUMAN,
                    EntityClass.WIZARD,
                    ClassStats.getStatsForClass(EntityClass.WIZARD));

        } else if (viewId == R.id.character3) {
            selectedPlayer = new Player(chara_name3.getText().toString(),
                    Race.TIEFLING,
                    EntityClass.ROGUE,
                    ClassStats.getStatsForClass(EntityClass.ROGUE));

        } else if (viewId == R.id.character4) {
            // Archer
            selectedPlayer = new Player(chara_name4.getText().toString(),
                    Race.ELF,
                    EntityClass.RANGER,
                    ClassStats.getStatsForClass(EntityClass.RANGER));
        }
        Log.d("CharacterScreen", "Selected Player: " + selectedPlayer.getDisplayName() + " " + selectedPlayer.getRace() + " " + selectedPlayer.getEntityClass());
    }
}