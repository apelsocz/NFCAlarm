package com.example.adam.nfcalarm.data;


import android.content.Context;
import android.util.Log;
import com.example.adam.nfcalarm.MyApplication;
import com.example.adam.nfcalarm.model.AlarmModel;
import com.example.adam.nfcalarm.util.Data;
import com.example.adam.nfcalarm.util.Format;

import org.json.JSONArray;
import org.json.JSONException;

import java.security.Key;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AlarmDAO implements DAOInterface {
    private static final String NAME = AlarmDAO.class.getSimpleName();

    private PreferencesManager mPrefsMngr;

    private List<AlarmModel> mList;
    private JSONArray mJSON;

    public AlarmDAO() {
        Context context = MyApplication.getInstance().getApplicationContext();
        mList = new ArrayList<>();
        mJSON = new JSONArray();

        try {
            mPrefsMngr = PreferencesManager.getInstance();
        } catch (IllegalStateException e) {
            Log.e(NAME, e.getMessage());
            PreferencesManager.initializeInstance(context);
            mPrefsMngr = PreferencesManager.getInstance();
        }

        String modelsAsString = mPrefsMngr.getKeyValueString(PreferencesManager.KEY_VALUE_ALARMS);
        mJSON = Data.modelsAsJSON(modelsAsString);
        mList = Data.modelsAsList(mJSON);
    }

    @Override
    public JSONArray getModelsAsJSON() {
        return mJSON;
    }

    @Override
    public List<AlarmModel> getModelsAsList() {
        return mList;
    }

    @Override
    public AlarmModel getModel(long id) {
        AlarmModel retModel = AlarmModel.EMPTY;
        for (AlarmModel model : mList) {
            if (model.uniqueID == id){
                retModel = model;
            }
        }
        return retModel;
    }

    @Override
    public void setModels(JSONArray json) {
        mJSON = json;
        write();
    }

    @Override
    public void updateModel(AlarmModel model) {
        List<AlarmModel> list = mList;
        int length = list.size();

        if (length > 0) {
            Log.d(NAME, "length > 0");
            if (list.contains(model)) {
                Log.d(NAME, "list contains model");
                int index = list.indexOf(model);
                list.remove(model);
                list.add(index, model);

                try {
                    JSONArray update = new JSONArray();
                    for (int i = 0; i < length; i++) {
                        update.put(i, list.get(i).json);
                    }
                    mJSON = update;
                    write();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(NAME, e.getMessage());
                }
            }
        }
    }

    private void write() {
        mPrefsMngr.setKeyValue(PreferencesManager.KEY_VALUE_ALARMS, mJSON.toString());
        mPrefsMngr.setKeyValue(PreferencesManager.KEY_VALUE_MILLIS, 0L);
        mPrefsMngr.setKeyValue(PreferencesManager.KEY_VALUE_ID, 0L);
        findNextAlarm(mList);
        MyApplication.getInstance().doScheduling(Data.activeModelInList(mList));
    }

    public AlarmModel scheduledModel() {
        long id = mPrefsMngr.getKeyValueLong(PreferencesManager.KEY_VALUE_ID);
        return getModel(id);
    }

    public long scheduledMillis() {
        return mPrefsMngr.getKeyValueLong(PreferencesManager.KEY_VALUE_MILLIS);
    }

    private void findNextAlarm(List<AlarmModel> alarmList) {
        long nextModelID = 0L;
        Calendar calNext = Calendar.getInstance();
        Calendar calNow = Calendar.getInstance();
        Calendar calModel = Calendar.getInstance();

        calNext.setTimeInMillis(0L);
        calNow.setTimeInMillis(System.currentTimeMillis());
        calModel.setTimeInMillis(System.currentTimeMillis());

        Date dateNext = calNext.getTime();
        Date dateNow = calNow.getTime();
        Date dateModel;

        Log.d("[START] now", dateNow.toString());
        Log.d("[START] next", String.valueOf(dateNext.getTime()));
        Log.d("[START] next", dateNext.toString());

        if (alarmList.size() > 0) {
            if (Data.activeModelInList(alarmList) ) {
                for (AlarmModel model : alarmList) {
                    if (model.isActive) {
                        String tag = String.valueOf(model.uniqueID) + ", [" +
                                model.hour + ":" + model.minute + "]";
                        Log.d(tag, "<- isActive");
                        calModel.set(Calendar.DAY_OF_YEAR, calNow.get(Calendar.DAY_OF_YEAR));
                        calModel.set(Calendar.HOUR_OF_DAY, 0);
                        calModel.set(Calendar.MINUTE, 0);
                        // allow for 5 seconds to be flexible
                        calModel.set(Calendar.SECOND, 5);
                        dateModel = calModel.getTime();
                        Log.d(tag, "<- refreshed (" + dateModel.toString() + ")");
                        int hourOfDay = Integer.parseInt(model.hour);
                        int minute = Integer.parseInt(model.minute);
                        Log.d(tag, String.valueOf(hourOfDay) + ":" + String.valueOf(minute));
                        calModel.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calModel.set(Calendar.MINUTE, minute);

                        dateModel = calModel.getTime();
                        Log.d(tag, "<- add (" + dateModel.toString() + ")");

                        if (model.once) {
                            Log.d(tag, "model.once");
                            if (dateModel.before(dateNow)) {
                                calModel.add(Calendar.DAY_OF_YEAR, 1);
                                dateModel = calModel.getTime();
                            }
                        }
                        else {
                            Log.d(tag, "model.repeats");
                            // iterate, until first occurence of:
                            // dateModel being active after dateNow
                            int i = 0;
                            boolean iterate = true;
                            do {
                                Log.d(tag, "Iteration [" + String.valueOf(i) + "]");
                                // refresh model calendar to today and increment by i
                                calModel.set(Calendar.DAY_OF_YEAR, calNow.get(
                                        Calendar.DAY_OF_YEAR));
                                calModel.add(Calendar.DAY_OF_YEAR, i);
                                dateModel = calModel.getTime();

                                if (Data.isDayActive(calModel.get(Calendar.DAY_OF_WEEK),
                                        model)) {
                                    Log.d(tag, "> "
                                            + Format.dayOfYear(calModel.get(Calendar.DAY_OF_WEEK)) +
                                            " " + String.valueOf(calModel.get(Calendar.DAY_OF_YEAR))
                                            + ", repeat: true"
                                    );
                                    // check if model is after now
                                    if (dateModel.after(dateNow)) {
                                        iterate = false;
                                    }
                                }
                                else {
                                    Log.d(tag, "> "
                                            + Format.dayOfYear(calModel.get(Calendar.DAY_OF_WEEK)) +
                                            " " + String.valueOf(calModel.get(Calendar.DAY_OF_YEAR))
                                            + ", repeat: false"
                                    );
                                }
                                String msg = iterate ? "keep going" : "STOP!";
                                Log.d(tag, "> " + msg);

                                i++;
                            } while (iterate);
                        }

                        // assign dateNext
                        Log.d(tag, dateModel.toString());
                        if (dateNext.getTime() == 0L) {
                            // assign knowing that dateModel is more ideal
                            Log.d(tag, "dateNext.getTime() == 0");
                            dateNext = dateModel;
                            nextModelID = model.uniqueID;
                        }
                        else {
                            //assign, if dateModel is after dateNow && before current assignment
                            Log.d(tag, "dateNext.getTime() != 0");
                            dateNext = dateModel.after(dateNow) && dateModel.before(dateNext) ?
                                    dateModel : dateNext;
                            nextModelID = dateNext.compareTo(dateModel) == 0 ? model.uniqueID : nextModelID;
                        }
                    }
                }
                //write millis and ID to preferences
                mPrefsMngr.setKeyValue(PreferencesManager.KEY_VALUE_MILLIS, dateNext.getTime());
                mPrefsMngr.setKeyValue(PreferencesManager.KEY_VALUE_ID, nextModelID);
            }
            else {
                Log.d("[ERROR]", "there are no active alarms");
            }
        }
        else {
            Log.d("[ERROR]", "alarm list is empty");
        }

        Log.d("[FOUND] getTime", String.valueOf(dateNext.getTime()));
        Log.d("[FOUND] dateNext", dateNext.toString());
    }
}