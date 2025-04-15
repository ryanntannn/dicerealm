package com.example.dicerealmandroid.fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.dicerealm.core.dialogue.SkillCheck;
import com.example.dicerealmandroid.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Arrays;

public class DiceDialogFragment extends DialogFragment {

    private SkillCheck.ActionResultDetail actionResultDetail;

    private static final String ASCII_DICE_TEMPLATE = ""+
            "            ,:::,\n" +
            "       ,,,:;  :  ;:,,,\n" +
            "   ,,,:       :       :,,,\n" +
            ",,;...........:...........;,,\n" +
            "; ;          ;';          ; ;\n" +
            ";  ;        ;   ;        ;  ;\n" +
            ";   ;      ;     ;      ;   ;\n" +
            ";    ;    ;       ;    ;    ;\n" +
            ";     ;  ;    %2d   ;  ;     ;\n" +
            ";      ;:...........:;      ;\n" +
            ";     , ;           ; ,     ;\n" +
            ";   ,'   ;         ;   ',   ;\n" +
            "'';'      ;       ;      ';''\n" +
            "   ''';    ;     ;    ;'''\n" +
            "       ''':;;   ;;:'''\n" +
            "            ':::'";

    public static DiceDialogFragment newInstance(SkillCheck.ActionResultDetail actionResultDetail){
        DiceDialogFragment fragment = new DiceDialogFragment();
        fragment.actionResultDetail = actionResultDetail;
        return fragment;
    }

    @Nullable
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        TextView textView = new TextView(requireContext());
        textView.setText(String.format(ASCII_DICE_TEMPLATE, actionResultDetail.getRollResultDetails()[0].getRoll()) + "\n\n" + actionResultDetail.toString());
        textView.setTypeface(Typeface.MONOSPACE); // <-- This sets the font
        textView.setTextSize(14); // Optional
        textView.setPadding(32, 32, 32, 32); // Optional
        textView.setTextColor(Color.BLACK);  // Optional
        textView.setBackgroundColor(Color.WHITE); // Optional

        Dialog diceDialog = new MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_App)
                .setTitle("Your Roll")
                .setView(textView)
                .setPositiveButton("Close", (dialog, which) -> dismiss()).create();

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
}
