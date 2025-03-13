package com.example.dicerealmandroid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dicerealmandroid.core.DicerealmClientSingleton;

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

        if (DicerealmClientSingleton.getInstance() == null){
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