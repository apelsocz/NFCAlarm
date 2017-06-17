package com.pelsoczi.adam.tapthat.app;

import android.app.IntentService;
import android.content.Intent;

import com.pelsoczi.adam.tapthat.RingingActivity;

/**
 * This {@code IntentService} does the actual work. <p>
 * {@code WakefulReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class StartRingingService extends IntentService {
    public StartRingingService() {
        super("StartRingingService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
//        Log.d("StartRingingService", "{Ringing Service}");

        Intent activityIntent = new Intent(getApplicationContext(), RingingActivity.class);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        getApplication().startActivity(activityIntent);

        // Release the wake lock provided by the BroadcastReceiver.
        WakefulReceiver.completeWakefulIntent(intent);
    }
}