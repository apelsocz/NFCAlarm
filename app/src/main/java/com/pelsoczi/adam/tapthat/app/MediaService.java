package com.pelsoczi.adam.tapthat.app;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.pelsoczi.adam.tapthat.R;
import com.pelsoczi.adam.tapthat.RingingActivity;
import com.pelsoczi.adam.tapthat.data.AlarmDAO;
import com.pelsoczi.adam.tapthat.util.Format;

import java.io.IOException;


public class MediaService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener {

    public static final String ACTION_PLAY = "com.pelsoczi.adam.tapthat.alertservice.ACTION_PLAY";
    private static final int ID_NOTIFICATION = 1;
    MediaPlayer mMediaPlayer = null;
    Vibrator mVibrator;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        showNotification();
        if (intent.getAction().equals(ACTION_PLAY)) {
            initMediaPlayer();
        }
        return super.onStartCommand(intent,flags,startId);
    }

    private void initMediaPlayer() {
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
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.prepareAsync();
    }

    /**
     * Called when the media file is ready for playback.
     * @param mp the MediaPlayer that is ready for playback
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        mMediaPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();
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
        mVibrator.cancel();
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

    private void showNotification() {
        AlarmDAO alarmDAO = new AlarmDAO();
        long millis = alarmDAO.scheduledMillis();
        Context context = getApplicationContext();

        Intent snooze = new Intent(context, RingingActivity.class);
        snooze.setAction(RingingActivity.ACTION_SNOOZE_ALARM);
        PendingIntent pendingSnooze = PendingIntent.getActivity(context, 0, snooze,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Action dismiss =
                new NotificationCompat.Action(R.mipmap.ic_snooze_black_18dp, "Snooze", pendingSnooze);

        PendingIntent content = PendingIntent.getActivity(context, 0, new Intent(context,
                RingingActivity.class), 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_tapthat_black)
                .setContentTitle("Wake up and " + getString(R.string.app_name))
                .setContentText(Format.formatDate(millis))
                .setContentIntent(content)
                .setAutoCancel(false)
                .setOngoing(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setUsesChronometer(true)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .addAction(dismiss);

        startForeground(ID_NOTIFICATION, builder.build());

        // This code snippet will cause the phone to vibrate "SOS" in Morse Code
        // In Morse Code, "s" = "dot-dot-dot", "o" = "dash-dash-dash"
        // There are pauses to separate dots/dashes, letters, and words
        // The following numbers represent millisecond lengths
        int dot = 200;      // Length of a Morse Code "dot" in milliseconds
        int dash = 500;     // Length of a Morse Code "dash" in milliseconds
        int short_gap = 200;    // Length of Gap Between dots/dashes
        int medium_gap = 500;   // Length of Gap Between Letters
        int long_gap = 1000;    // Length of Gap Between Words
        long[] pattern = {
                0,  // Start immediately
                dot, short_gap, dot, short_gap, dot,    // s
                medium_gap,
                dash, short_gap, dash, short_gap, dash, // o
                medium_gap,
                dot, short_gap, dot, short_gap, dot,    // s
                long_gap
        };

        mVibrator.vibrate(pattern, 0);
    }
}
