package com.example.dicerealmandroid.util;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textview.MaterialTextView;

/*
 * Call this class whenever you want to show a loading screen
 * Used specific activity rather than app context to adhere to best practices (UI changes should be done relative to activity)
 * */
public class Initcombat {
    private MaterialTextView message;
    private CircularProgressIndicator progress;
    private FrameLayout loadingLayout;
    private Activity activity;

    public Initcombat(Activity activity){
        this.message = new MaterialTextView(activity);
        this.message.setText("Loading...");
        this.initializeLoadingLayout(activity, message);
    }

    public Initcombat(Activity activity, String message){
        this.message = new MaterialTextView(activity);
        this.message.setText(message);
        this.initializeLoadingLayout(activity, this.message);
    }

    private void initializeLoadingLayout(Activity activity, MaterialTextView message){
        this.activity = activity;
        loadingLayout = new FrameLayout(activity);
        LinearLayout container = new LinearLayout(activity);
        MaterialButton buttonclose = new MaterialButton(activity);
        buttonclose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                hide();
            }
        });

        // container prop
        container.setBackgroundColor(Color.TRANSPARENT);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setGravity(Gravity.CENTER);

        // message prop
        LinearLayout.LayoutParams textLayout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        message.setLayoutParams(textLayout);
        message.setForegroundGravity(Gravity.CENTER);
        message.setTextAppearance(android.R.style.TextAppearance_DeviceDefault_Small);
        message.setTextColor(Color.WHITE);
        message.setTextSize(13);
        message.setWidth(700);


        FrameLayout.LayoutParams progressLayout = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );


        container.addView(message);
        container.addView(buttonclose);


        loadingLayout.setBackgroundColor(Color.parseColor("#66000000")); // 40% black
        loadingLayout.setClickable(true);
        loadingLayout.setFocusable(true);
        loadingLayout.setVisibility(View.INVISIBLE);

        loadingLayout.addView(container);
    }

    public void show(){
        if(activity != null && !activity.isFinishing()){
            activity.runOnUiThread(() -> {
                // Check if the layout is inside that activity, if not add it to the root
                if(loadingLayout.getParent() == null){
                    ((ViewGroup) activity.findViewById(android.R.id.content)).addView(loadingLayout);
                }
                loadingLayout.setVisibility(View.VISIBLE);
            });
        }
    }

    public void hide(){
        if(activity != null && !activity.isFinishing()){
            activity.runOnUiThread(() -> {
                loadingLayout.setVisibility(View.INVISIBLE);
            });
        }
    }
}
