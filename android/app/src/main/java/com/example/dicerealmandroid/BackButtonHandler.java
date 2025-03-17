package com.example.dicerealmandroid;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class BackButtonHandler {
    public static void setupBackButtonHandler(Activity activity, int buttonId){
        Button backButton = activity.findViewById(buttonId);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
    }

    public static void setupBackImageButtonHandler(Activity activity, int imageButtonId){
        ImageButton backButton = activity.findViewById(imageButtonId);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
    }
}
