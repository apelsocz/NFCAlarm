package com.example.adam.nfcalarm.scheduler;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import com.example.adam.nfcalarm.ApplicationActivity;
import com.example.adam.nfcalarm.AlarmActivity;
import com.example.adam.nfcalarm.R;
//import com.example.adam.nfcalarm.model.AlarmData;

/**
 * This {@code IntentService} does the app's actual work. <p>
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
//        notify(TAG);
//        Bundle b = new Bundle();
//        b.putString(TAG, TAG);
//        intent.putExtras(b);

        // Release the wake lock provided by the BroadcastReceiver.
        // // TODO: 16-02-08 add boolean while notification active
        //// TODO: 16-02-14 call method from ResultActivity.class

//        Intent activityIntent = new Intent(getBaseContext(), ApplicationActivity.class);
        Intent activityIntent = new Intent(getBaseContext(), AlarmActivity.class);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplication().startActivity(activityIntent);

/*        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        Intent activityIntent = new Intent(AlarmClock.ACTION_SET_ALARM);

        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activityIntent.putExtra(AlarmClock.EXTRA_MESSAGE, "AlarmClock content provider");
        activityIntent.putExtra(AlarmClock.EXTRA_HOUR, calendar.get(Calendar.HOUR_OF_DAY));
        activityIntent.putExtra(AlarmClock.EXTRA_MINUTES, calendar.get(Calendar.MINUTE)+1);
        activityIntent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);

        getApplication().startActivity(activityIntent);*/
//        WakefulAlarmReceiver.completeWakefulIntent(intent);
    }
    
    private void sendNotification(String msg) {
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(
                Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, ApplicationActivity.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.icon_nfc)
                    .setContentTitle(getString(R.string.app_name))
                    .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(msg + "{Big Text}"))
                    .setFullScreenIntent(contentIntent, false)
                    .setContentText(msg)
                    .setOngoing(true);

        mBuilder.setCategory(NotificationCompat.CATEGORY_ALARM);

        mBuilder.setContentIntent(contentIntent);
        Notification notification = mBuilder.build();
        startForeground(NOTIFICATION_ID, mBuilder.build());
    }

    private void notify(String msg) {
        // TODO http://developer.android.com/guide/topics/ui/notifiers/notifications.html#DirectEntry

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon_nfc)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(msg);

        mBuilder.setAutoCancel(false);
        mBuilder.setOngoing(true);
        mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
        
        // Explicit intent
        Intent resultIntent = new Intent(this, ApplicationActivity.class);
//        Intent resultIntent = new Intent(this, ResultActivity.class);

        // artificial back stack for started Activity - ensures navigating backwards from Activity
        // leads out of application to homescreen
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ApplicationActivity.class);
//        stackBuilder.addParentStack(ResultActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
