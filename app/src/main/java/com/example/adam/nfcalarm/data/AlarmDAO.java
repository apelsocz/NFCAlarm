package com.example.adam.nfcalarm.data;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.example.adam.nfcalarm.MyApplication;
import com.example.adam.nfcalarm.model.AlarmModel;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class AlarmDAO implements DAOInterface {
    private static final String NAME = AlarmDAO.class.getSimpleName();

    private Context mContext;
    private PreferencesManager mPrefMgr;
    private List<AlarmModel> mList;
    private JSONArray mJSON;

    public AlarmDAO() {
        mContext = MyApplication.getInstance().getApplicationContext();
        mList = new ArrayList<>();
        mJSON = new JSONArray();

        try {
            mPrefMgr = PreferencesManager.getInstance();
        } catch (IllegalStateException e) {
            Log.e(NAME, e.getMessage());
            PreferencesManager.initializeInstance(mContext);
            mPrefMgr = PreferencesManager.getInstance();
        }
        read();
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
    public void addModels(JSONArray json) {
        mJSON = json;
        write();
    }

    @Override
    public void updateModel(AlarmModel model) {

        write();
    }

    @Override
    public void deleteModel(AlarmModel model) {

        write();
    }

    private void read() {
        readAlarms();
    }

    private void readAlarms() {
        readJSON();
        List<AlarmModel> modelsAsList = new ArrayList<>();

        int length = mJSON.length();
        if (length > 0) {
            for (int i = 0; i < length; i++) {
                modelsAsList.add(new AlarmModel(mJSON.optString(i)));
            }
        }
        mList = modelsAsList;
    }

    private void readJSON() {
        String modelsAsString = mPrefMgr.getKeyValueString(PreferencesManager.KEY_VALUE_ALARMS);
        JSONArray modelsAsJSON;
        try {
            modelsAsJSON = new JSONArray(modelsAsString);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(NAME, e.getMessage());
            modelsAsJSON = new JSONArray();
        }
        mJSON = modelsAsJSON;
    }

    private void write() {
        mPrefMgr.setKeyValue(PreferencesManager.KEY_VALUE_ALARMS, mJSON.toString());
    }
}