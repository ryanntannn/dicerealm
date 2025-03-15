package com.example.dicerealmandroid;

import android.app.Application;
import android.content.Context;

/*
* This file is used to get the application context
* */
public class MyApp extends Application {
    private static Context context;

    @Override
    public void onCreate(){
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getAppContext(){
        return context;
    }
}
