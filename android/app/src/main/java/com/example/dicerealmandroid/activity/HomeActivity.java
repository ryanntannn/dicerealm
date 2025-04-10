package com.example.dicerealmandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.dicerealm.core.room.RoomState;
import com.example.dicerealmandroid.R;
import com.example.dicerealmandroid.game.GameStateHolder;
import com.example.dicerealmandroid.room.RoomStateHolder;
import com.example.dicerealmandroid.util.Loading;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {
    private String roomId;
    private Loading loading;
    private RoomStateHolder roomSh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.home), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        roomSh = new ViewModelProvider(this).get(RoomStateHolder.class);

        Button join = findViewById(R.id.joinBtn);
        // Disable join button by default
        join.setEnabled(false);
        TextInputLayout textInputLayout = findViewById(R.id.textInputLayout2);



        // Enable join button when input is in focus and valid code is entered
        textInputLayout.getEditText().setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                textInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        // Required override for TextWatcher interface, but not used in this case.
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String input = s.toString().trim();
                        ArrayList<String> validatedResult = roomSh.validateRoomCode(input);
                        Boolean isValid = Boolean.parseBoolean(validatedResult.get(0));
                        String errorMessage = validatedResult.get(1);

                        // Update join button state when code is valid
                        if (isValid) {
                            textInputLayout.setError(null); // Clear error
                        } else {
                            Log.d("Error", "Invalid room code");
                        }
                        join.setEnabled(isValid);
                        textInputLayout.setError(errorMessage);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        // Required override for TextWatcher interface, but not used in this case.
                    }
                });
            }
            // Clear error when focus is lost
            else {
                textInputLayout.setError(null);
            }
        });

        loading = new Loading(HomeActivity.this, "Joining room...");
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                loading.show();
                roomId = textInputLayout.getEditText().getText().toString().trim();
                // Remove all spaces and newlines
                roomId = roomId.replaceAll("\\s+", "");
                roomSh.createRoom(roomId);
            }
        });


        // Navigate when state changes to LOBBY
        roomSh.trackState().observe(this,  new Observer<RoomState.State>() {
            @Override
            public void onChanged(RoomState.State state){
                if(state == RoomState.State.LOBBY){
                    loading.hide();
                    Intent intent = new Intent(HomeActivity.this, CharacterScreen.class);
                    startActivity(intent);
                    Log.d("Info", "Room created with code: " + roomId);
                }
            }
        });


    }

    // If user navigates back to the home screen
    @Override
    public void onResume(){
        super.onResume();
        roomSh.leaveRoom();

        // Clear the room code input field and clear its focus
        TextInputLayout textInputLayout = findViewById(R.id.textInputLayout2);
        Objects.requireNonNull(textInputLayout.getEditText()).setText("");
        textInputLayout.getEditText().clearFocus();
    }


}