package com.example.adam.nfcalarm.model;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Objects;

public class AlarmModel {
    public static final AlarmModel EMPTY = new AlarmModel(null);
    public static final int SUNDAY = Calendar.SUNDAY;
    public static final int MONDAY = Calendar.MONDAY;
    public static final int TUESDAY = Calendar.TUESDAY;
    public static final int WEDNESDAY = Calendar.WEDNESDAY;
    public static final int THURSDAY = Calendar.THURSDAY;
    public static final int FRIDAY = Calendar.FRIDAY;
    public static final int SATURDAY = Calendar.SATURDAY;

    public final JSONObject json;
    public final String hour;
    public final String minute;
    public final boolean once;
    public final boolean sunday;
    public final boolean monday;
    public final boolean tuesday;
    public final boolean wednesday;
    public final boolean thursday;
    public final boolean friday;
    public final boolean saturday;

    public final boolean isActive;
    public final boolean isEmpty;
    public final long uniqueID;


    public AlarmModel(String jsonAsString) {
        JSONObject j = new JSONObject();
        try {
            jsonAsString = jsonAsString != null ? jsonAsString : "{}";
            j = new JSONObject(jsonAsString);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        json = j;

        hour = json.optString("hr", "");
        minute = json.optString("mn", "");
        once = json.optBoolean("on", true);
        sunday = json.optBoolean("su", false);
        monday = json.optBoolean("mo", false);
        tuesday = json.optBoolean("tu", false);
        wednesday = json.optBoolean("we", false);
        thursday = json.optBoolean("th", false);
        friday = json.optBoolean("fr", false);
        saturday = json.optBoolean("sa", false);
        isActive = json.optBoolean("ac", false);

        isEmpty = json.length() == 0 || TextUtils.isEmpty(hour) || TextUtils.isEmpty(minute);
        // this may cause errors depending on how AlarmModel is being handled - investigate
        uniqueID = isEmpty ? 0 : json.optLong("id", System.currentTimeMillis());
    }

    public AlarmModel(final long uniqueID, final boolean isActive, final String hour, final String minute,
                      final boolean once, final boolean sunday, final boolean monday, final boolean tuesday,
                      final boolean wednesday, final boolean thursday, final boolean friday,
                      final boolean saturday) {

        this.uniqueID = uniqueID;
        this.isActive = isActive;
        this.hour = hour;
        this.minute = minute;
        this.once = once;
        this.sunday = sunday;
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;

        json = new JSONObject();

        try {
            json.put("hr", !TextUtils.isEmpty(this.hour) ? this.hour : "");
            json.put("mn", !TextUtils.isEmpty(this.minute) ? this.minute : "");
            json.put("ac", this.isActive);
            json.put("id", this.uniqueID > 0 ? this.uniqueID : System.currentTimeMillis());
            json.put("on", this.once);
            json.put("su", !this.once && this.sunday);
            json.put("mo", !this.once && this.monday);
            json.put("tu", !this.once && this.tuesday);
            json.put("we", !this.once && this.wednesday);
            json.put("th", !this.once && this.thursday);
            json.put("fr", !this.once && this.friday);
            json.put("sa", !this.once && this.saturday);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        isEmpty = json.length() == 0 || TextUtils.isEmpty(this.hour) || TextUtils.isEmpty(this.minute);
    }

    @Override
    public boolean equals(Object object) {
        if(object == null) {
            return false;
        }

        if(getClass() != object.getClass()) {
            return false;
        }

        AlarmModel other = (AlarmModel) object;

        if(isEmpty || other.isEmpty) {
            return false;
        }

        return Objects.equals(hour, other.hour) &&
                Objects.equals(minute, other.minute) &&
                Objects.equals(uniqueID, other.uniqueID);
    }
}