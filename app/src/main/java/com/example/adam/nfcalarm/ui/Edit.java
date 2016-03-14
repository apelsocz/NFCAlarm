package com.example.adam.nfcalarm.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.adam.nfcalarm.ApplicationActivity;
import com.example.adam.nfcalarm.R;
import com.example.adam.nfcalarm.data.AlarmDataManager;
//import com.example.adam.nfcalarm.model.AlarmData;
import com.example.adam.nfcalarm.model.AlarmModel;
import com.example.adam.nfcalarm.util.Views;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by adam on 15-12-30.
 */
public class Edit extends Fragment implements View.OnClickListener {
    public static final String NAME = Edit.class.getSimpleName();

    private static final String MODEL_KEY = "modelKey";

    public static Edit newInstance(AlarmModel model) {
        Bundle bundle = new Bundle();
        String modelAsString = model != null ? model.json.toString() : "";
        bundle.putString(MODEL_KEY, modelAsString);

        Edit fragment = new Edit();
        fragment.setArguments(bundle);
        return fragment;
    }

    private SwitchCompat isActive;
    private TimePicker picker;
    private CheckBox once;
    private CheckBox sunday;
    private CheckBox monday;
    private CheckBox tuesday;
    private CheckBox wednesday;
    private CheckBox thursday;
    private CheckBox friday;
    private CheckBox saturday;

    private long uniqueID;
//    private AlarmData alarmData;
    private AlarmDataManager alarmManager;
    private JSONArray alarms;
    private int savedModelIndex;

    private AlarmModel currentModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.edit, container, false);
        isActive = (SwitchCompat) rootView.findViewById(R.id.edit_active);
        picker = (TimePicker) rootView.findViewById(R.id.edit_picker);
        once = (CheckBox) rootView.findViewById(R.id.edit_repeat_once);
        sunday = (CheckBox) rootView.findViewById(R.id.edit_repeat_sun);
        monday = (CheckBox) rootView.findViewById(R.id.edit_repeat_mon);
        tuesday = (CheckBox) rootView.findViewById(R.id.edit_repeat_tue);
        wednesday = (CheckBox) rootView.findViewById(R.id.edit_repeat_wed);
        thursday = (CheckBox) rootView.findViewById(R.id.edit_repeat_thu);
        friday = (CheckBox) rootView.findViewById(R.id.edit_repeat_fri);
        saturday = (CheckBox) rootView.findViewById(R.id.edit_repeat_sat);
        picker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
        isActive.setOnClickListener(this);
        once.setOnClickListener(this);
        sunday.setOnClickListener(this);
        monday.setOnClickListener(this);
        tuesday.setOnClickListener(this);
        wednesday.setOnClickListener(this);
        thursday.setOnClickListener(this);
        friday.setOnClickListener(this);
        saturday.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Activity activity = getActivity();
        if(Views.isActivityNull(activity)) {
            return;
        }

        setHasOptionsMenu(true);

        alarmManager = AlarmDataManager.getInstance();

        // retrieve the model being edited, either in savedinstance or the fragments bundle
        String modelAsString = savedInstanceState != null && savedInstanceState.containsKey(MODEL_KEY) ?
                savedInstanceState.getString(MODEL_KEY, "") : getArguments().getString(MODEL_KEY, "");

        currentModel = new AlarmModel(modelAsString);
        uniqueID = currentModel.uniqueID;
        alarms = alarmManager.getJSONArray();

        savedModelIndex = -1;
        // // TODO: 16-01-19 Investigate, retrieving length may not be viable - JSONArray Null value expected
        int length = alarms.length();
        if (length > 0 && !currentModel.isEmpty) {
            for (int i = 0; i < length; i++) {
                AlarmModel model = new AlarmModel(alarms.optString(i));
                if (currentModel.equals(model)) {
                    savedModelIndex = i;
                    break;
                }
            }
        }

        updateUI(currentModel);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.edit, menu);

        // hide order menu item - defined in ContactFragment
        final MenuItem settingsMenuItem = menu.findItem(R.id.action_settings);
        if (settingsMenuItem != null) {
            settingsMenuItem.setVisible(false);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Activity activity = getActivity();
        if(Views.isActivityNull(activity)) {
            // return if activity destroyed or about to
            return super.onOptionsItemSelected(item);
        }

        AlarmModel currentModel = toAlarmModel();

        // // TODO: 16-01-20 investigate use of isUpdated
        boolean isUpdated = false;

        switch (item.getItemId()) {
            case R.id.save:
                if (!currentModel.isEmpty) {
                    //// TODO: 16-01-20 remove try catch
                    try{
                        if (savedModelIndex > -1) {
                            alarms.put(savedModelIndex, currentModel.json);
                        }
                        else {
                            alarms.put(currentModel.json);
                        }
                        /*alarmData.setAlarms(alarms);*/
                        ((ApplicationActivity)activity).doAlarmsUpdate(alarms);
                        isUpdated = true;
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    // validation would be required here
                }
                break;
            case R.id.delete:
                if (savedModelIndex > -1) {
                    alarms.remove(savedModelIndex);
                    /*alarmData.setAlarms(alarms);*/
                    ((ApplicationActivity)activity).doAlarmsUpdate(alarms);
                }
                isUpdated = true;
                break;
        }

        if (isUpdated) {
            ((ApplicationActivity)activity).onEditUpdate();
        }

        return isUpdated;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save current data to restore
        outState.putString(MODEL_KEY, toAlarmModel().json.toString());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void updateUI(AlarmModel model)  {
        //TODO
        // conversions. find out if this is where.
        if (model != null && !model.isEmpty) {
            isActive.setChecked(model.isActive);
            picker.setHour(Integer.parseInt(model.hour));
            picker.setMinute(Integer.parseInt(model.minute));
            once.setChecked(model.once);
            sunday.setChecked(model.sunday);
            monday.setChecked(model.monday);
            tuesday.setChecked(model.tuesday);
            wednesday.setChecked(model.wednesday);
            thursday.setChecked(model.thursday);
            friday.setChecked(model.friday);
            saturday.setChecked(model.saturday);
        }
        else {
            isActive.setChecked(false);
            // TODO take steps for picker being shown as 24 hour / if 12 hour AM PM should be set
            /*picker.setHour((Calendar.getInstance())
                    .get(DateFormat.is24HourFormat(getActivity()) ? Calendar.HOUR_OF_DAY : Calendar.HOUR
            ));*/
            picker.setHour((Calendar.getInstance())
                    .get(DateFormat.is24HourFormat(getActivity()) ? Calendar.HOUR_OF_DAY : Calendar.HOUR
                    ));
            picker.setMinute(((Calendar.getInstance()).get(Calendar.MINUTE)));
            once.setChecked(true);
            sunday.setChecked(false);
            monday.setChecked(false);
            tuesday.setChecked(false);
            wednesday.setChecked(false);
            thursday.setChecked(false);
            friday.setChecked(false);
            saturday.setChecked(false);
        }
    }

    private AlarmModel toAlarmModel() {
        return new AlarmModel(
                uniqueID,
                isActive.isChecked(),
                String.valueOf(picker.getHour()),
                String.valueOf(picker.getMinute()),
                once.isChecked(),
                sunday.isChecked(),
                monday.isChecked(),
                tuesday.isChecked(),
                wednesday.isChecked(),
                thursday.isChecked(),
                friday.isChecked(),
                saturday.isChecked()
        );
    }

    public void showTimePickerDialogFragment() {
        Calendar c = Calendar.getInstance();
        boolean is24HourFormat = DateFormat.is24HourFormat(getActivity());
        int hour = is24HourFormat ? c.get(Calendar.HOUR_OF_DAY) : c.get(Calendar.HOUR);
        int minute = c.get(Calendar.MINUTE);

        DialogFragment myDialogFragment = TimePickerDialogFragment.newInstance(hour, minute, is24HourFormat);
        myDialogFragment.show(getFragmentManager(), TimePickerDialogFragment.NAME);
    }

    @Override
    public void onClick(View view) {

        if (view.equals(once)) {
            if (once.isChecked()) {
                sunday.setChecked(false);
                monday.setChecked(false);
                tuesday.setChecked(false);
                wednesday.setChecked(false);
                thursday.setChecked(false);
                friday.setChecked(false);
                saturday.setChecked(false);
                Toast.makeText(getActivity(), "once.isChecked", Toast.LENGTH_SHORT).show();
            }
            else {
                once.setChecked(true);
            }
        }
        else if (view.equals(sunday) || view.equals(monday) || view.equals(tuesday) ||
                view.equals(wednesday) || view.equals(thursday) || view.equals(friday) ||
                view.equals(saturday)) {
            if (once.isChecked()) {
                once.setChecked(false);
            }
            else if (!sunday.isChecked() && !monday.isChecked() && !tuesday.isChecked() &&
                    !wednesday.isChecked() && !thursday.isChecked() && !friday.isChecked() &&
                    !saturday.isChecked() ){
                once.setChecked(true);
            }
        }
    }
}