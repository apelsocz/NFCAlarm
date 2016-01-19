package com.example.adam.nfcalarm.util;

import android.app.Activity;

/**
 * Created by adam on 15-06-20.
 */
public class Views {
    public static boolean isActivityNull(Activity activity) {
        return activity == null || activity.isFinishing() || activity.isDestroyed();
    }
}