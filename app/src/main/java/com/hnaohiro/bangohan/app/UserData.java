package com.hnaohiro.bangohan.app;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hnaohiro on 2014/05/11.
 */
public class UserData {

    private String name;
    private int hour;
    private int min;
    private boolean need;
    private boolean defined;

    public UserData() {}

    public UserData(String name, int hour, int min, boolean need, boolean defined) {
        this.name = name;
        this.hour = hour;
        this.min = min;
        this.need = need;
        this.defined = defined;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() { return String.format("%02d : %02d", hour, min); }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public boolean isNeed() {
        return need;
    }

    public void setNeed(boolean need) {
        this.need = need;
    }

    public boolean isDefined() {
        return defined;
    }

    public void setDefined(boolean defined) {
        this.defined = defined;
    }

    public static UserData fromJSONObject(JSONObject json) throws JSONException {
        UserData userData = new UserData();

        userData.name = json.getString("name");
        userData.hour = json.getInt("hour");
        userData.min = json.getInt("min");
        userData.need = json.getBoolean("need");
        userData.defined = json.getBoolean("defined");

        return userData;
    }

    public static List<UserData> fromJSONObject(JSONArray json) throws JSONException {
        List<UserData> list = new ArrayList<UserData>();

        for (int i = 0; i < json.length(); i++) {
            JSONObject user = json.getJSONObject(i);
            list.add(UserData.fromJSONObject(user));
        }

        return list;
    }
}
