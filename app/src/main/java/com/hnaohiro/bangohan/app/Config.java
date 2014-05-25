package com.hnaohiro.bangohan.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Created by hnaohiro on 2014/05/25.
 */
public class Config {

    private static final String PREFERENCE_NAME = "Bangohan";

    private Context context;

    public Config(Context context) {
        this.context = context;
    }

    private SharedPreferences getPreferences() {
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public int getUserId() {
        return getPreferences().getInt("user_id", -1);
    }

    public void setUserId(int id) {
        getPreferences().edit()
                .putInt("user_id", id)
                .commit();
    }

    public int getHour() {
        return getPreferences().getInt("hour", -1);
    }

    public void setHour(int hour) {
        getPreferences().edit()
                .putInt("hour", hour)
                .commit();
    }

    public int getMin() {
        return getPreferences().getInt("min", -1);
    }

    public void setMin(int min) {
        getPreferences().edit()
                .putInt("min", min)
                .commit();
    }

    public void alertNoConfig(String message) {
        new AlertDialog.Builder(context)
                .setTitle("Bangohan")
                .setMessage(message)
                .setPositiveButton(
                        "OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(context, ConfigActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                context.startActivity(intent);
                            }
                        }
                )
                .show();
    }
}
