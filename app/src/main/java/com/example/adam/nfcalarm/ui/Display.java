package com.example.adam.nfcalarm.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.adam.nfcalarm.RingingActivity;
import com.example.adam.nfcalarm.R;

import java.text.DateFormat;
import java.util.Calendar;

public class Display extends Fragment {
    public static final String NAME = Display.class.getName();

    private TextView mTime;
    private TextView mDate;
    private ImageView mBackground;
    private FloatingActionButton mFAB;

    private BroadcastReceiver mTimeTickReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("mTimeTickReceiver", "Got message ");
            update();
        }
    };

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Window window = getActivity().getWindow();
//        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
//                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
//                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
//                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
//                WindowManager.LayoutParams.FLAG_FULLSCREEN
//        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.alarm_display, container, false);

        mTime = (TextView) rootView.findViewById(R.id.display_time);
        mDate = (TextView) rootView.findViewById(R.id.display_date);
        mBackground = (ImageView) rootView.findViewById(R.id.display_background);

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
                    ((RingingActivity)activity).dismiss();
                }
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        Log.d("display", "onResume()");
        super.onResume();
        update();
        getActivity().registerReceiver(mTimeTickReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    public void onPause() {
        Log.d("display", "onPause()");
        getActivity().unregisterReceiver(mTimeTickReceiver);
        super.onPause();
    }

    private void update() {
        final DateFormat df = DateFormat.getDateInstance(DateFormat.FULL);
        final DateFormat tf = DateFormat.getTimeInstance(DateFormat.SHORT);

        mDate.setText(df.format(Calendar.getInstance().getTime()));
        mTime.setText(tf.format(Calendar.getInstance().getTime()));
    }

    public void snooze() {
        // TODO: 16-04-02 set text label
    }

    public void dismiss() {
        // TODO: 16-04-02 set text label
    }
}