package com.example.adam.nfcalarm.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adam.nfcalarm.ApplicationActivity;
import com.example.adam.nfcalarm.R;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by adam on 16-02-16.
 */
public class Display extends Fragment {
    public static final String NAME = Display.class.getName();

    private TextView mTime;
    private TextView mDate;
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
        View rootView = inflater.inflate(R.layout.alarm_display, container, false);

        mTime = (TextView) rootView.findViewById(R.id.display_time);
        mDate = (TextView) rootView.findViewById(R.id.display_date);

        return rootView;
    }

    private void update() {
        final DateFormat df = DateFormat.getDateInstance(DateFormat.FULL);
        final DateFormat tf = DateFormat.getTimeInstance(DateFormat.SHORT);

        mDate.setText(df.format(Calendar.getInstance().getTime()));
        mTime.setText(tf.format(Calendar.getInstance().getTime()));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("display", "onActivityCreated()");
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.d("display", "onViewStateRestored()");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("display", "onSaveInstanceState()");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d("display", "onConfigChang()");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d("display", "onLowMemory()");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("display", "onDestroyView()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("display", "onDestroy()");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("display", "onDetach()");
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
                    ((ApplicationActivity)activity).stopRinging();
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

    @Override
    public void onStop() {
        Log.d("display", "onStop()");
        super.onStop();
    }
}