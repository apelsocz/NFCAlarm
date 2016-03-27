package com.example.adam.nfcalarm.scheduler;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.example.adam.nfcalarm.ApplicationActivity;
import com.example.adam.nfcalarm.ui.Display;

/**
 * This {@code IntentService} does the app's actual work. <p>
 * {@code WakefulAlarmReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class SchedulingService extends IntentService {
    private static final int ID_NOTIFICATION = 1;

    public SchedulingService() {
        super("SchedulingService");
    }
    
    public static final String TAG = "Alarm Scheduling Service";

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("AlarmSchedulingService", "{Alarm Scheduling Service}");

        Bundle b = new Bundle();
        b.putBoolean(Display.NAME, true);
        Intent activityIntent = new Intent(getApplicationContext(), ApplicationActivity.class);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activityIntent.putExtras(b);

        getApplication().startActivity(activityIntent);

        // Release the wake lock provided by the BroadcastReceiver.
        WakefulAlarmReceiver.completeWakefulIntent(intent);
    }
}