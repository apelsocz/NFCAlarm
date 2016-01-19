package com.example.adam.nfcalarm.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.adam.nfcalarm.ApplicationActivity;
import com.example.adam.nfcalarm.R;
import com.example.adam.nfcalarm.util.Views;

import java.util.Calendar;
import java.util.Set;

/**
 * Created by adam on 15-07-15.
 */
public class Content extends Fragment implements View.OnClickListener {
    public static final String NAME = Content.class.getSimpleName();

    private boolean isActive;
    private Switch mSwitch;
    private TextView mTime;
    private TextView mMeridiem;
    private TextView mAlarmMessage;
    private Button bTime;
    private Button bSchedule;
    private TextView mRepeat;
    private TextView mFrequency;
    private Calendar mCalendar;
    private boolean[] mCheckedItems;

    private SharedPreferences sharedPreferences;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content, container, false);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        final Activity activity = getActivity();
        sharedPreferences = activity.getSharedPreferences(ApplicationActivity.NAME, Context.MODE_PRIVATE);
        mCalendar = Calendar.getInstance();

        if (!Views.isActivityNull(activity)) {

            mSwitch = (Switch) activity.findViewById(R.id.switch_enabled);
            mTime = (TextView) activity.findViewById(R.id.alarm_time);
            mMeridiem = (TextView) activity.findViewById(R.id.alarm_meridiem);
            bTime = (Button) activity.findViewById(R.id.button_time);
            mAlarmMessage = (TextView) activity.findViewById(R.id.alarm_message);
            bSchedule = (Button) activity.findViewById(R.id.button_schedule);
            mRepeat = (TextView) activity.findViewById(R.id.alarm_repeat);
            mFrequency = (TextView) activity.findViewById(R.id.alarm_schedule);

            bTime.setOnClickListener(this);
            bSchedule.setOnClickListener(this);

            isActive = true;
//            isActive = toggle.isChecked() ? true : false;

            if (sharedPreferences.contains("TIME")){
                mTime.setText(sharedPreferences.getString("TIME", ""));
            }

            if (sharedPreferences.contains("FREQUENCY")){
                int size = sharedPreferences.getInt("FREQUENCY", 0);
                mCheckedItems = new boolean[size];

                for (int i = 0; i < mCheckedItems.length; i++) {
                    mCheckedItems[i] = sharedPreferences.getBoolean("FREQUENCY" + i, false);
                }
            }

            invalidateUI();
        }
    }

    void invalidateUI() {

        if (sharedPreferences.contains("TIME")) {
            String time = sharedPreferences.getString("TIME", "");
            String meridiem = sharedPreferences.getString("MERIDIEM", "");

            if (!TextUtils.isEmpty(time)) {
                mTime.setText(time);
                mMeridiem.setText(meridiem);

                mAlarmMessage.setText("Time for alarm to ring");
                mAlarmMessage.setTextColor(Color.GRAY);
                bTime.setBackgroundResource(R.color.accentColor);
                bTime.setText("Set");
                bSchedule.setEnabled(true);
            }
            if (bSchedule.isEnabled()) {
//                mRepeat.setText("Repeat");
                mFrequency.setText("Alarm schedule undefined");
                mFrequency.setTextColor(getResources().getColor(R.color.error_color));
                bSchedule.setBackgroundColor(getResources().getColor(R.color.error_color));
            }
        } else {
            mAlarmMessage.setText("Alarm doesn't exist");
            mAlarmMessage.setTextColor(getResources().getColor(R.color.error_color));
            bTime.setBackgroundResource(R.color.error_color);
            bTime.setText("Create");

            mRepeat.setText("");
            mFrequency.setText("Alarm doesn't exist");
            mFrequency.setTextColor(getResources().getColor(R.color.error_color));
            bSchedule.setBackgroundColor(Color.LTGRAY);
            bSchedule.setEnabled(false);
        }

        mSwitch.setEnabled(sharedPreferences.contains("TIME") && sharedPreferences.contains("SCHEDULE"));

        if (mSwitch.isEnabled()) {
            mFrequency.setTextColor(Color.GRAY);
            bSchedule.setBackgroundResource(R.color.accentColor);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Activity activity = getActivity();
        if (!Views.isActivityNull(activity)) {
            if (!TextUtils.isEmpty(mTime.getText())) {
                activity.getSharedPreferences(ApplicationActivity.NAME, Context.MODE_PRIVATE)
                        .edit()
                        .putBoolean("IS_ENABLED", isActive)
                        .putString("TIME", mTime.getText().toString())
                        .putString("MERIDIEM", mMeridiem.getText().toString())
                        .commit();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == bTime) {
            showTimeDialog();
        }
        if (v == bSchedule) {
            showRepeatDialog();
        }
        if (v == mSwitch) {

        }
    }

    private void showTimeDialog() {
        int hour;
        int minute;

        Calendar c = Calendar.getInstance();
        hour = sharedPreferences.getInt("HOUR", c.get(Calendar.HOUR));
        minute = sharedPreferences.getInt("MINUTE", c.get(Calendar.MINUTE));

        DialogFragment myDialogFragment = TimePickerDialogFragment.newInstance(hour, minute, false);
        myDialogFragment.show(getFragmentManager(), TimePickerDialogFragment.NAME);
    }

    public void doTimeSet(TimePicker view, int hourOfDay, int minute) {

        String meridiem = "adam";
        int hour = hourOfDay;

        if(!view.is24HourView()) {
            meridiem = hourOfDay > 12 ? "PM" : "AM";
        }

        sharedPreferences.edit()
                .putInt("HOUR", hourOfDay)
                .putInt("MINUTE", minute)
                .putString("TIME", hour + ":" + minute)
                .putString("MERIDIEM", meridiem)
                .apply();
        invalidateUI();
    }

    private void showRepeatDialog() {
        ScheduleDialogFragment frag = new ScheduleDialogFragment();
        frag.show(getFragmentManager(), ScheduleDialogFragment.NAME);
        frag.checkedItems = mCheckedItems;
    }

    public void doFrequencySet(boolean[] list){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("FREQUENCY", list.length);

        //TODO
        //validate this list does not equal the list which exists
        if(true){
            for (int i = 0; i < list.length; i++) {
                editor.putBoolean("FREQUENCY" + i, list[i]);
            }
            editor.apply();
        }

//        String[] spKeyNames = new String[] {
//                "ONCE", "SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT",
//        };
//        sharedPreferences.edit()
//                .putBoolean(spKeyNames[which], isChecked)
//                .apply();
    }

    public void doSchedule(boolean isScheduled){
        sharedPreferences.edit()
                .putBoolean("SCHEDULE", isScheduled)
                .apply();
    }

    public static class ScheduleDialogFragment extends DialogFragment {
        public static final String NAME = ScheduleDialogFragment.class.getSimpleName();
        private String[] mOptions = new String[] {
                "Once", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
        };
//        private boolean[] checkedItems = new boolean[mOptions.length];
        private boolean[] checkedItems;
        private ListView v;
        Button positive;

        private DialogInterface.OnMultiChoiceClickListener listener =
                new DialogInterface.OnMultiChoiceClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                v = ((AlertDialog)dialog).getListView();

                if(which == 0 && isChecked ){
                    for (int i = 1; i < mOptions.length; i++){
                        checkedItems[i] = false;
                        ((AlertDialog)dialog).getListView().setItemChecked(i, false);
                    }
                }
                else if(which != 0 && v.isItemChecked(0)){
                    checkedItems[0] = false;
                    ((AlertDialog)dialog).getListView().setItemChecked(0, false);
                }

                validateButtons();
            }
        };

        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Alarm will repeat:")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((ApplicationActivity)getActivity()).setActive(true);
                            ((ApplicationActivity)getActivity()).setCycle(checkedItems);
                        }
                    })
                    .setMultiChoiceItems(mOptions, checkedItems, listener)
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });

            return builder.create();
        }

        @Override
        public void onStart() {
            super.onStart();
            positive = ((AlertDialog)getDialog()).getButton(DialogInterface.BUTTON_POSITIVE);
            if (checkedItems == null) {
                checkedItems = new boolean[mOptions.length];
            }
            validateButtons();
        }

        private void validateButtons(){
            boolean valid = false;
            for (int i = 0; i < checkedItems.length; i++){
                if(checkedItems[i] == true){
                    valid = true;
                }
            }
            positive.setEnabled(valid);
        }
    }
}