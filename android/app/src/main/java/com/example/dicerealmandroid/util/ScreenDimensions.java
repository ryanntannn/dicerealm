package com.example.dicerealmandroid.util;


import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class ScreenDimensions {

    // Doesnt take into account nav bar
    public static int getScreenHeight(){
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public static int getScreenWidth(){
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }
}
