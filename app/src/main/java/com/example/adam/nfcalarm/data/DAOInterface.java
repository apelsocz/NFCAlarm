package com.example.adam.nfcalarm.data;


import android.content.SharedPreferences;

import com.example.adam.nfcalarm.model.AlarmModel;

import org.json.JSONArray;

import java.util.List;

public interface DAOInterface {
    public List<AlarmModel> getModelsAsList();
    public JSONArray getModelsAsJSON();
    public AlarmModel getModel(long id);
    public void setModels(JSONArray json);
    public void updateModel(AlarmModel model);
}