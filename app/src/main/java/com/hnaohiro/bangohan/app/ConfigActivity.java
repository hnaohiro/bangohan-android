package com.hnaohiro.bangohan.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.cengalabs.flatui.FlatUI;

public class ConfigActivity extends Activity {

    private Config config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        FlatUI.initDefaultValues(this);
        FlatUI.setDefaultTheme(FlatUI.DEEP);
        getActionBar().setBackgroundDrawable(FlatUI.getActionBarDrawable(this, FlatUI.DEEP, true));

        setConfig();
    }

    private void setConfig() {
        config = new Config(this);

        setCurrentUserSpinner();
        setReminderTimePicker();
    }

    private void setCurrentUserSpinner() {
        int id = config.getUserId();
        Spinner spinner = (Spinner) findViewById(R.id.current_user_spinner);

        if (id != -1) {
            spinner.setSelection(id);
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                config.setUserId(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private void setReminderTimePicker() {
        int hour = config.getHour();
        int min = config.getMin();
        TimePicker timePicker = (TimePicker) findViewById(R.id.reminder_timepicker);

        if (hour != -1) {
            timePicker.setCurrentHour(hour);
        }
        if (min != -1) {
            timePicker.setCurrentMinute(min);
        }

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hour, int min) {
                config.setHour(hour);
                config.setMin(min);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_list:
                Intent mainActivityIntent = new Intent(this, MainActivity.class);
                mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainActivityIntent);
                break;
            case R.id.menu_set:
                Intent setActivityIntent = new Intent(this, SetActivity.class);
                setActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(setActivityIntent);
                break;
            case R.id.menu_config:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
