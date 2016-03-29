package com.example.adam.nfcalarm.scheduler;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.example.adam.nfcalarm.AlarmActivity;
import com.example.adam.nfcalarm.R;
import com.example.adam.nfcalarm.data.AlarmDataManager;
import com.example.adam.nfcalarm.ui.Display;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

public class AlarmService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener, OnErrorListener {

    public static final int ID_NOTIFICATION = 1;
    public static final String ACTION_PLAY = "com.example.adam.nfcalarm.PLAY";

    MediaPlayer mMediaPlayer = null;

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("AlarmService", "onStartCommand()");
        if (intent.getAction().equals(ACTION_PLAY)) {
            Log.d("AlarmService", "Action = PLAY");
            initMediaPlayer();
        }
        return START_CONTINUATION_MASK;
    }

    public void initMediaPlayer() {
        mMediaPlayer = new MediaPlayer();

        // CPU will be kept on until all partial wake locks have been released.
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        if (uri == null) {
            uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        }
        try {
            mMediaPlayer.setDataSource(this, uri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Prepare aysnc to not block main thread
        mMediaPlayer.prepareAsync();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /** Called when MediaPlayer is ready */
    public void onPrepared(MediaPlayer player) {
        player.setLooping(true);
        player.start();

        showNotification();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        // Resets the MediaPlayer to its uninitialized state.
        mp.reset();
        // Initialize and prepare MediaPlayer
        initMediaPlayer();
        return true;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d("AlarmService", "onCompletion()");
        mp.stop();
        mp.release();
    }

    @Override
    public void onDestroy() {
        Log.d("AlarmService", "onDestroy()");
        mMediaPlayer.stop();
        mMediaPlayer.release();
        mMediaPlayer = null;
        stopForeground(true);
        super.onDestroy();
    }

    private void showNotification() {

        long millis;
        try {
            millis = AlarmDataManager.getInstance().getNextMillisValue();
        } catch (IllegalStateException e) {
            AlarmDataManager.initializeInstance(getApplicationContext());
            millis = AlarmDataManager.getInstance().getNextMillisValue();
        }
        Date d = new Date(millis);
        DateFormat tf = DateFormat.getTimeInstance(DateFormat.SHORT);

        // TODO: 16-03-27 modify intent to only create a new activity if activity is not already running
        Intent activityIntent = new Intent(getApplicationContext(), AlarmActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0 , activityIntent, 0);

        Intent doneIntent = new Intent(getApplicationContext(), AlarmActivity.class);
        doneIntent.setAction(AlarmActivity.ACTION_DISMISS_ALARM);
//        PendingIntent donePending = PendingIntent.getBroadcast(getApplicationContext(), 0, doneIntent, 0);
        PendingIntent donePending = PendingIntent.getActivity(getApplicationContext(), 0, doneIntent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Action dismiss = new NotificationCompat.Action(R.drawable.icon_nfc, "Done", donePending);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon_nfc)
                .setContentTitle("Active Alarm")
                .setContentText(tf.format(d))
                .setContentIntent(contentIntent)
                .setShowWhen(false)
                .setAutoCancel(false)
                .setOngoing(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .addAction(dismiss);

//        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        nm.notify(ID_NOTIFICATION, builder.build());
        startForeground(ID_NOTIFICATION, builder.build());
    }
}