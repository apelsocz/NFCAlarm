package com.pelsoczi.adam.tapthat.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pelsoczi.adam.tapthat.R;
import com.pelsoczi.adam.tapthat.RingingActivity;
import com.pelsoczi.adam.tapthat.util.Format;

import java.util.Calendar;


public class Ringing extends Fragment {
    public static final String NAME = Ringing.class.getSimpleName();
    private static final String LOG_NAME = Ringing.class.getSimpleName();

    private TextView mTime;
    private TextView mDate;
    private ProgressBar mProgress;
    private FloatingActionButton mFAB;

    private BroadcastReceiver mTimeTickReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Log.d("mTimeTickReceiver", "Got message ");
            update();
        }
    };

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.alarm_ringing, container, false);
        mTime = (TextView) rootView.findViewById(R.id.display_time);
        mDate = (TextView) rootView.findViewById(R.id.display_date);
        mProgress = (ProgressBar) rootView.findViewById(R.id.display_progress);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        final Activity activity = getActivity();

        mFAB = (FloatingActionButton) activity.findViewById(R.id.floating_action_btn);
        mFAB.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (v.equals(mFAB)){
                    ((RingingActivity)activity).snooze();
                }
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
//        Log.d(LOG_NAME, "onResume()");
        update();

        //register the receiver
        getActivity().registerReceiver(mTimeTickReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    public void onPause() {
//        Log.d(LOG_NAME, "onPause()");

        // unregsiter the receiver to avoid any leakages
        getActivity().unregisterReceiver(mTimeTickReceiver);
        super.onPause();
    }

    private void update() {
        mDate.setText(Format.formatDate(Calendar.getInstance().getTimeInMillis()));
        mTime.setText(Format.formatTime(Calendar.getInstance().getTimeInMillis()));
    }
}