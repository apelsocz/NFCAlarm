package com.example.adam.nfcalarm.app;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.adam.nfcalarm.RingingActivity;
import com.example.adam.nfcalarm.R;
import com.example.adam.nfcalarm.data.AlarmDAO;
import com.example.adam.nfcalarm.util.Format;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;


public class AlarmService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener {
    //// TODO: 16-04-27 Android Ringer modes

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
//            mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//            mAudioManager.requestAudioFocus(this, AudioManager.STREAM_SYSTEM,
//                    AudioManager.AUDIOFOCUS_GAIN);
//            mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
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
        //// TODO: 16-04-27 AudioManager Stream, Ring Mode, Volume
//        mMediaPlayer.setVolume(1.0f, 1.0f);
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

        Icon icon = Icon.createWithResource(context, R.drawable.ic_snooze_black_18dp);

        NotificationCompat.Action dismiss =
                new NotificationCompat.Action(R.mipmap.ic_snooze_black_18dp, "Done", pendingSnooze);

        PendingIntent content = PendingIntent.getActivity(context, 0, new Intent(context,
                RingingActivity.class), 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon_nfc)
                .setContentTitle("Active Alarm")
                .setContentText(Format.formatDate(millis))
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
}
