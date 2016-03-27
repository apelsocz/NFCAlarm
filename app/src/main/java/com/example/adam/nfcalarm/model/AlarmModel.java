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

        hour = json.optString("alarmHour", "");
        minute = json.optString("alarmMinute", "");
        once = json.optBoolean("alarmOnce", true);
        sunday = json.optBoolean("alarmSun", false);
        monday = json.optBoolean("alarmMon", false);
        tuesday = json.optBoolean("alarmTue", false);
        wednesday = json.optBoolean("alarmWed", false);
        thursday = json.optBoolean("alarmThu", false);
        friday = json.optBoolean("alarmFri", false);
        saturday = json.optBoolean("alarmSat", false);
        isActive = json.optBoolean("alarmActive", false);

        isEmpty = json.length() == 0 || TextUtils.isEmpty(hour) || TextUtils.isEmpty(minute);
        // this may cause errors depending on how AlarmModel is being handled - investigate
        uniqueID = isEmpty ? 0 : json.optLong("alarmID", System.currentTimeMillis());
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
            json.put("alarmHour", !TextUtils.isEmpty(this.hour) ? this.hour : "");
            json.put("alarmMinute", !TextUtils.isEmpty(this.minute) ? this.minute : "");
            json.put("alarmActive", this.isActive);
            json.put("alarmID", this.uniqueID > 0 ? this.uniqueID : System.currentTimeMillis());
            json.put("alarmOnce", this.once);
            json.put("alarmSun", !this.once && this.sunday);
            json.put("alarmMon", !this.once && this.monday);
            json.put("alarmTue", !this.once && this.tuesday);
            json.put("alarmWed", !this.once && this.wednesday);
            json.put("alarmThu", !this.once && this.thursday);
            json.put("alarmFri", !this.once && this.friday);
            json.put("alarmSat", !this.once && this.saturday);
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