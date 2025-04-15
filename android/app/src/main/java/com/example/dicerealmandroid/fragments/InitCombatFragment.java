package com.example.dicerealmandroid.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import com.dicerealm.core.dialogue.SkillCheck;
import com.example.dicerealmandroid.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Arrays;

public class InitCombatFragment extends DialogFragment {
    String message ;
    Activity combatscreen;

    private FrameLayout loadingLayout;

    public static InitCombatFragment newInstance(String message, Activity combatscreen){
        InitCombatFragment fragment = new InitCombatFragment();
        fragment.message = message;
        fragment.combatscreen = combatscreen;
        return fragment;
    }

    @Nullable
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        TextView textView = new TextView(requireContext());
        textView.setTextSize(14); // Optional
        textView.setPadding(32, 32, 32, 32); // Optional
        Log.d("InitMessage:" , "Fragment " + message);
        displayMessageStream(message,textView);

        Dialog diceDialog = new MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_App)
                .setTitle("Combat Start!")
                .setView(textView)
                .setPositiveButton("Time to fight!", (dialog, which) -> onclick()).create();

        // Apply window attributes before showing the dialog
        Window window = diceDialog.getWindow();
        if (window != null) {
            // Set window animations
            window.setWindowAnimations(R.style.DialogAnimation_App);

            // Apply gravity immediately
            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = Gravity.CENTER;
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            window.setAttributes(params);
        }
        diceDialog.show();

        return diceDialog;
    }

    private void onclick(){
        ConstraintLayout linearLayout = combatscreen.findViewById(R.id.main);
        linearLayout.setAlpha(1);
        linearLayout.setBackground(getResources().getDrawable(R.color.white));
        dismiss();
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
                combatscreen.runOnUiThread(() -> {
                    currentTurnView.append(String.valueOf(currChar));
                });
            }
        });
        backgroundThread.start();
    }
}
