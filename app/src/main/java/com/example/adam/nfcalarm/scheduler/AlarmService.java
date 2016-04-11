package com.example.adam.nfcalarm.scheduler;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.example.adam.nfcalarm.RingingActivity;
import com.example.adam.nfcalarm.R;
import com.example.adam.nfcalarm.data.AlarmDAO;
import com.example.adam.nfcalarm.data.AlarmDataManager;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;


public class AlarmService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    public static final String ACTION_PLAY = "com.example.adam.nfcalarm.alertservice.ACTION_PLAY";
    private static final int ID_NOTIFICATION = 1;

    MediaPlayer mMediaPlayer = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showNotification();
        if (intent.getAction().equals(ACTION_PLAY)) {
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
            }
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (uri == null) {
                uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
            try {
                mMediaPlayer.setDataSource(this, uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.prepareAsync();
        }
        return super.onStartCommand(intent,flags,startId);
    }

    /**
     * Called when the media file is ready for playback.
     *
     * @param mp the MediaPlayer that is ready for playback
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        mMediaPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();
    }

    private void showNotification() {

        AlarmDAO alarmDAO = new AlarmDAO();
        long millis = alarmDAO.getScheduledMillis();
//        try {
//            millis = AlarmDataManager.getInstance().getNextAlarmMillis();
//        } catch (IllegalStateException e) {
//            AlarmDataManager.initializeInstance(getApplicationContext());
//            millis = AlarmDataManager.getInstance().getNextAlarmMillis();
//        }
        Date d = new Date(millis);
        DateFormat tf = DateFormat.getTimeInstance(DateFormat.SHORT);

        // TODO: 16-03-27 modify intent to only create a new activity if activity is not already running
        Intent intentActivity = new Intent(getApplicationContext(), RingingActivity.class);

        PendingIntent content = PendingIntent.getActivity(
                getApplicationContext(), 0, new Intent(getApplicationContext(), RingingActivity.class), 0);

        Intent intentSnooze = intentActivity;
        intentSnooze.setAction(RingingActivity.ACTION_SNOOZE_ALARM);
        PendingIntent pendingSnooze = PendingIntent.getActivity(
                getApplicationContext(), 0, intentSnooze, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Action dismiss = new NotificationCompat.Action(
                R.drawable.icon_nfc, "Done", pendingSnooze);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon_nfc)
                .setContentTitle("Active Alarm")
                .setContentText(tf.format(d))
                .setContentIntent(content)
                .setShowWhen(false)
                .setAutoCancel(false)
                .setOngoing(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .addAction(dismiss);

        startForeground(ID_NOTIFICATION, builder.build());
    }

    public void stop() {
        stopForeground(true);
        stopSelf();
    }

    /**
     * Called by the system to notify a Service that it is no longer used and is being removed.  The
     * service should clean up any resources it holds (threads, registered
     * receivers, etc) at this point.  Upon return, there will be no more calls
     * in to this Service object and it is effectively dead.  Do not call this method directly.
     */
    @Override
    public void onDestroy() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        super.onDestroy();
    }

    /**
     * Called to indicate an error.
     * Returning false, or not having an OnErrorListener at all, will
     * cause the OnCompletionListener to be called.
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        stop();
        return true;
    }
}
