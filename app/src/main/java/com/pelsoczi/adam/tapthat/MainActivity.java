package com.pelsoczi.adam.tapthat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.pelsoczi.adam.tapthat.app.MediaService;

/**
 * The Application's controller activity to delegate launching the desired activity
 */
public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Log.d(LOG_TAG, "onCreate()");

        Context context = getApplicationContext();

        boolean isRinging = MyApplication.getInstance().isRinging;
        boolean isSnoozed = MyApplication.getInstance().isSnoozing;

        // determine which activity should be started
        if (isRinging || isSnoozed) {
            if (isSnoozed) {
                // play media
                Intent media = new Intent(context, MediaService.class);
                media.setAction(MediaService.ACTION_PLAY);
                context.startService(media);

                // the app was launched while snoozed, modify the global state
                MyApplication.getInstance().setSnoozing(false);
                MyApplication.getInstance().doScheduling(false);
            }
            // present the user with ringing alarm
            Intent activity = new Intent(context, RingingActivity.class);
            startActivity(activity);
        }
        else {
            // present the user the primary activity
            startActivity(new Intent(context, AlarmsActivity.class));
        }

        // done
        finish();
    }
}