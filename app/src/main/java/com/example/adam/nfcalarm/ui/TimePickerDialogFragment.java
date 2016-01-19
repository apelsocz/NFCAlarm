package com.example.adam.nfcalarm.ui;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.text.TextUtilsCompat;
import android.util.Log;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.adam.nfcalarm.ApplicationActivity;
import com.example.adam.nfcalarm.model.AlarmModel;

/**
 * Created by adam on 15-07-19.
 */
public class TimePickerDialogFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener,
        TimePickerDialog.OnDismissListener {
    public static final String NAME = TimePickerDialogFragment.class.getSimpleName();

    private int initialHour;
    private int initialMin;
    private boolean is24HourFormat;

    public TimePickerDialogFragment(){}

    public static TimePickerDialogFragment newInstance(int hour, int minute, boolean is24HourFormat) {
        TimePickerDialogFragment frag = new TimePickerDialogFragment();

        Bundle args = new Bundle();
        args.putInt("hour", hour);
        args.putInt("min", minute);
        args.putBoolean("format", is24HourFormat);
        frag.setArguments(args);
        frag.setCancelable(false);

        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialHour = getArguments().getInt("hour");
        initialMin = getArguments().getInt("min");
        is24HourFormat = getArguments().getBoolean("format");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //TODO
        // return instantiated timepickerdialog and set 24hr boolean through neutral button
        // return new TimePickerDialog(getActivity(), this, initialHour, initialMin, is24HourFormat);
        Dialog dialog = new TimePickerDialog(getActivity(), this, initialHour, initialMin, is24HourFormat);
        dialog.setCancelable(false);
        return dialog;
    }

    @Override
    public void onDismiss(DialogInterface unused) {
        super.onDismiss(unused);
    }

    @Override
    public void onCancel(DialogInterface unused) {
//        ((ApplicationActivity)getActivity()).onEditUpdate();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        /*
            AlarmModel model;
            model = new AlarmModel(false, String.valueOf(hourOfDay), String.valueOf(minute));
            ((ApplicationActivity)getActivity()).onAlarmClick(model);
        */
    }
}