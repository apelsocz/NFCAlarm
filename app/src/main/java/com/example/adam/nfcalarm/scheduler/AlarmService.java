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
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

public class AlarmService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener, OnErrorListener {

    private static final int ID_NOTIFICATION = 1;
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

    @Override
    public void onDestroy() {
        Log.d("AlarmService", "onDestroy()");
        mMediaPlayer.stop();
        mMediaPlayer.release();
        mMediaPlayer = null;
        super.onDestroy();
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

        showNotification();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /** Called when MediaPlayer is ready */
    public void onPrepared(MediaPlayer player) {
        player.start();
        player.setLooping(true);
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
        Log.d("AlarmService", "onDestroy()");
        mp.stop();
        mp.release();
    }

    private void showNotification() {
        Date d = new Date(AlarmDataManager.getInstance().getNextMillisValue()) ;
        DateFormat tf = DateFormat.getTimeInstance(DateFormat.SHORT);

        Intent activityIntent = new Intent(getApplicationContext(), AlarmActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0 , activityIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon_nfc)
                .setContentTitle("Active Alarm")
                .setContentText(tf.format(d))
                .setContentIntent(contentIntent)
                .setShowWhen(false)
                .setAutoCancel(false)
                .setOngoing(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(ID_NOTIFICATION, builder.build());
    }
}