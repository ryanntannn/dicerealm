package com.example.dicerealmandroid.util;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.dicerealmandroid.R;
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


    public Initcombat(Activity activity, String message){
        this.message = new MaterialTextView(activity);
        this.initializeLoadingLayout(activity, this.message , message);
    }

    private void initializeLoadingLayout(Activity activity, MaterialTextView message ,String textmessage){
        this.activity = activity;
        loadingLayout = new FrameLayout(activity);
        RelativeLayout container = new RelativeLayout(activity);
        LinearLayout cardview = new LinearLayout(activity);
        MaterialButton buttonclose = new MaterialButton(activity);
        TextView textView = new TextView(activity);
        buttonclose.setText("Start Battle!");

        cardview.setOrientation(LinearLayout.VERTICAL);
        cardview.setGravity(Gravity.CENTER);
        cardview.setPadding(40,20,40,20);
        cardview.setBackgroundResource(R.drawable.card_border);

        textView.setText("Combat time!");
        textView.setTextAppearance(android.R.style.TextAppearance_DeviceDefault_Large);
        textView.setForegroundGravity(Gravity.CENTER);

        View line = new View(activity);
        line.setBackgroundColor(Color.BLACK);
        line.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                1));

        buttonclose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                hide();
            }
        });

        // container prop
        container.setBackgroundColor(Color.TRANSPARENT);
        container.setGravity(Gravity.CENTER);

        // message prop
        LinearLayout.LayoutParams textLayout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        textView.setLayoutParams(textLayout);
        textView.setPadding(0,0,0, 15);
        buttonclose.setLayoutParams(textLayout);
        message.setLayoutParams(textLayout);
        message.setForegroundGravity(Gravity.CENTER);
        message.setTextColor(Color.BLACK);
        message.setTextAppearance(android.R.style.TextAppearance_DeviceDefault_Small);
        message.setTextSize(13);
        message.setMaxWidth(700);
        message.setPadding(0,0,0,20);
        buttonclose.setMaxWidth(700);


        FrameLayout.LayoutParams progressLayout = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );

        Log.d("progressLayout", message.getText().toString());
        cardview.addView(textView);
        cardview.addView(message);
        cardview.addView(buttonclose);
        container.addView(cardview);
        displayMessageStream(textmessage,message);

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
    private void displayMessageStream(String message, TextView currentTurnView) {
        // Run this on another thread/logical core to achieve true parallelism unlike python which thread is limited by GIL
        Thread backgroundThread = new Thread(() -> {
            for (int i = 0; i < message.length(); i++) {
                char currChar = message.charAt(i);
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
                // Important to run on UI thread
                activity.runOnUiThread(() -> {
                    currentTurnView.append(String.valueOf(currChar));
                });
            }
        });
        backgroundThread.start();
    }
}
