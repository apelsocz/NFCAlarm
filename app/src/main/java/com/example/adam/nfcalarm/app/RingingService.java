package com.example.adam.nfcalarm.app;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.example.adam.nfcalarm.RingingActivity;

/**
 * This {@code IntentService} does the app's actual work. <p>
 * {@code WakefulReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class RingingService extends IntentService {
    public RingingService() {
        super("RingingService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("Service", "{Ringing Service}");

        Intent activityIntent = new Intent(getApplicationContext(), RingingActivity.class);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        getApplication().startActivity(activityIntent);

        // Release the wake lock provided by the BroadcastReceiver.
        WakefulReceiver.completeWakefulIntent(intent);
    }
}