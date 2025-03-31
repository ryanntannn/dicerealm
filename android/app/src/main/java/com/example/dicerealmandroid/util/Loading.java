package com.example.dicerealmandroid.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textview.MaterialTextView;

/*
* Call this class whenever you want to show a loading screen
* Used specific activity rather than app context to adhere to best practices (UI changes should be done relative to activity)
* */
public class Loading {
    private MaterialTextView message;
    private CircularProgressIndicator progress;
    private FrameLayout loadingLayout;
    private Activity activity;

    public Loading(Activity activity){
        this.message = new MaterialTextView(activity);
        this.message.setText("Loading...");
        this.initializeLoadingLayout(activity, message);
    }

    public Loading(Activity activity, String message){
        this.message = new MaterialTextView(activity);
        this.message.setText(message);
        this.initializeLoadingLayout(activity, this.message);
    }

    private void initializeLoadingLayout(Activity activity, MaterialTextView message){
        this.activity = activity;
        loadingLayout = new FrameLayout(activity);
        progress = new CircularProgressIndicator(activity);
        LinearLayout container = new LinearLayout(activity);

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
        message.setTextSize(16);

        // indicator prop
        progress.setIndeterminate(true);

        FrameLayout.LayoutParams progressLayout = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );
        progressLayout.gravity = Gravity.CENTER;
        progress.setLayoutParams(progressLayout);


        container.addView(message);
        container.addView(progress);


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
