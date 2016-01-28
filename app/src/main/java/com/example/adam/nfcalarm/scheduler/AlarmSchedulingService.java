package com.example.adam.nfcalarm.scheduler;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.adam.nfcalarm.ApplicationActivity;
import com.example.adam.nfcalarm.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This {@code IntentService} does the app's actual work.
 * {@code WakefulAlarmReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class AlarmSchedulingService extends IntentService {
    public AlarmSchedulingService() {
        super("SchedulingService");
    }
    
    public static final String TAG = "Alarm Scheduling Service";
    // An ID used to post the notification.
    public static final int NOTIFICATION_ID = 1;

    @Override
    protected void onHandleIntent(Intent intent) {
        sendNotification(TAG);
        // Release the wake lock provided by the BroadcastReceiver.
        WakefulAlarmReceiver.completeWakefulIntent(intent);
        // END_INCLUDE(service_onhandle)
    }
    
    // Post a notification indicating whether a doodle was found.
    private void sendNotification(String msg) {
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
    
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
            new Intent(this, ApplicationActivity.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.icon_nfc)
                    .setContentTitle(getString(R.string.app_name))
                    .setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(msg + "{Big Text}"))
                    .setContentText(msg + "{Context Text}");

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
