package com.example.adam.nfcalarm.app;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.adam.nfcalarm.R;
import com.example.adam.nfcalarm.data.AlarmDAO;
import com.example.adam.nfcalarm.util.Format;

import java.util.Calendar;
import java.util.Date;

public class ScheduleService extends IntentService {
    public ScheduleService(){
        super("ScheduleService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        AlarmDAO alarmDAO = new AlarmDAO();
        long millis = alarmDAO.scheduledMillis();
        Calendar calendar = Calendar.getInstance();
        Context context = getApplicationContext();
        int notifyID = 1;

        String title = getResources().getString(R.string.app_name);
        String text = "Touch to set an alarm";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.icon_nfc)
                .setShowWhen(false)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setCategory(NotificationCompat.CATEGORY_STATUS);

        if (millis != 0L) {
            if (millis > calendar.getTimeInMillis()) {
                calendar.setTimeInMillis(millis);
                Date date = calendar.getTime();
                text = Format.formatTime(millis) + "-" + Format.formatDate(millis);

                AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent alarmIntent = new Intent(context, WakefulReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
                alarmMgr.setExact(AlarmManager.RTC_WAKEUP, millis, pendingIntent);

                Log.d("ScheduleService", date.toString());
                Log.d("ScheduleService", String.valueOf(millis));
            }
        }

        builder.setContentTitle(title)
                .setContentText(text);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(notifyID, builder.build());
    }
}