package com.example.dicerealmandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.dicerealmandroid.R;
import com.example.dicerealmandroid.room.RoomStateHolder;
import com.google.android.material.textfield.TextInputLayout;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.home), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button join = findViewById(R.id.joinBtn);
        TextInputLayout textInputLayout = findViewById(R.id.textInputLayout2);


        RoomStateHolder room_sh = new ViewModelProvider(this).get(RoomStateHolder.class);

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String roomId = textInputLayout.getEditText() != null ? textInputLayout.getEditText().getText().toString() : null;
//                Log.d("info", "Room code entered: " + roomId);
                if (roomId == null || roomId.isEmpty()){
                    Log.e("error", "No room code entered");
                    return;
                }
                room_sh.createRoom(roomId);
                Intent intent = new Intent(HomeActivity.this, CharacterScreen.class);
                startActivity(intent);
                Log.d("Info", "Room created with code: " + roomId);
            }
        });
    }


}