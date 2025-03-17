package com.example.dicerealmandroid;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.dicerealmandroid.activity.CharacterScreen;
import com.example.dicerealmandroid.activity.HomeActivity;
import com.example.dicerealmandroid.room.RoomStateHolder;

public class MainActivity extends AppCompatActivity {
    private DicerealmClient dicerealmClient;

    // TODO: 1. Add Lobby Interface (User connects to lobby)

    /*
    * Used as the app entry point
    * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Force light mode only
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        RoomStateHolder roomSh = new ViewModelProvider(this).get(RoomStateHolder.class);

        if (roomSh.getRoomState() == null){
            // Set home as root activity
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else{
            // Set character screen as root activity
            Intent intent = new Intent(this, CharacterScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }


    }
}