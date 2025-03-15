package com.example.dicerealmandroid.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.dicerealmandroid.MyApp;

/*
* Show message to user using Toast globally in the app
* */
public class Message {

    public static void showMessage(String message) {
        Context app_context = MyApp.getAppContext();
        if(app_context == null){
            return;
        }

        // Ensure message is shown on UI thread
        new Handler(Looper.getMainLooper()).post(() ->
                Toast.makeText(app_context, message, Toast.LENGTH_SHORT).show()
        );

    }
}
