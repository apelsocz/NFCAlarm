package com.example.adam.nfcalarm.data;


import android.content.Context;
import android.util.Log;
import com.example.adam.nfcalarm.MyApplication;
import com.example.adam.nfcalarm.model.AlarmModel;
import com.example.adam.nfcalarm.util.Data;

import org.json.JSONArray;
import org.json.JSONException;

import java.security.Key;
import java.util.ArrayList;
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

//        read();
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
            if (list.contains(model)) {
                int index = list.indexOf(model);
                list.remove(model);
                list.add(index, model);

                Log.d(NAME + " - update()", mJSON.toString());

                try {
                    JSONArray update = new JSONArray();
                    for (int i = 0; i < length; i++) {
                        update.put(i, list.get(i).json);
                    }
                    mJSON = update;
                    write();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(NAME + " - update()", mJSON.toString());
            }
        }
    }

    private void read() {
        String modelsAsString = mPrefsMngr.getKeyValueString(PreferencesManager.KEY_VALUE_ALARMS);
        mJSON = Data.modelsAsJSON(modelsAsString);
        mList = Data.modelsAsList(mJSON);
//        readAlarms();
    }

//    private void readAlarms() {
//        readJSON();
//        List<AlarmModel> modelsAsList = new ArrayList<>();
//
//        int length = mJSON.length();
//        if (length > 0) {
//            for (int i = 0; i < length; i++) {
//                modelsAsList.add(new AlarmModel(mJSON.optString(i)));
//            }
//        }
//        mList = modelsAsList;
//    }

//    private void readJSON() {
//        String modelsAsString = mPrefsMngr.getKeyValueString(PreferencesManager.KEY_VALUE_ALARMS);
//        JSONArray modelsAsJSON;
//        try {
//            modelsAsJSON = new JSONArray(modelsAsString);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Log.e(NAME, e.getMessage());
//            modelsAsJSON = new JSONArray();
//        }
//        mJSON = modelsAsJSON;
//    }

    private void write() {
        mPrefsMngr.setKeyValue(PreferencesManager.KEY_VALUE_ALARMS, mJSON.toString());
    }

    public AlarmModel getScheduledModel() {
        long id = mPrefsMngr.getKeyValueLong(PreferencesManager.KEY_VALUE_ID);
        return getModel(id);
    }

    public long getScheduledMillis() {
        return mPrefsMngr.getKeyValueLong(PreferencesManager.KEY_VALUE_MILLIS);
    }
}