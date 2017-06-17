package com.pelsoczi.adam.tapthat.data;


import com.pelsoczi.adam.tapthat.model.AlarmModel;

import org.json.JSONArray;

import java.util.List;

public interface DAOInterface {
    public List<AlarmModel> getModelsAsList();
    public JSONArray getModelsAsJSON();
    public AlarmModel getModel(long id);
    public void setModels(JSONArray json);
    public void updateModel(AlarmModel model);
}