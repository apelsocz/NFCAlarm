package com.pelsoczi.adam.tapthat.util;

import android.app.Activity;


public class Views {
    public static boolean isActivityNull(Activity activity) {
        return activity == null || activity.isFinishing() || activity.isDestroyed();
    }
}