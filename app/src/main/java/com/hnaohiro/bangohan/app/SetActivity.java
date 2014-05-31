package com.hnaohiro.bangohan.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.cengalabs.flatui.FlatUI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class SetActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);

        FlatUI.initDefaultValues(this);
        FlatUI.setDefaultTheme(FlatUI.DEEP);
        getActionBar().setBackgroundDrawable(FlatUI.getActionBarDrawable(this, FlatUI.DEEP, true));

        Button submitButton = (Button) findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSubmitButtonClick();
            }
        });

        Button clearButton = (Button) findViewById(R.id.clear_button);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClearButtonClick();
            }
        });

        setUserId();
        fetchUser();
    }

    private int userId;

    private void setUserId() {
        Config config = new Config(this);
        userId = config.getUserId();
        if (userId == -1) {
            config.alertNoConfig("Userを設定してください。");
        }
    }

    private void fetchUser() {
        new APIAsyncTask(this, "Loading...", new APIAsyncTaskActionListener() {
            @Override
            public Object doTask() {
                try {
                    return APIClient.getUser(userId);
                } catch (Exception e) {
                    Log.e(getString(R.string.app_name), e.getMessage());
                    return null;
                }
            }
            @Override
            public void onComplete(Object result) {
                if (result != null) {
                    UserData user = (UserData) result;
                    setUser(user);
                }
            }
        }).execute();
    }

    private void setUser(UserData user) {
        if (user.isDefined()) {
            Spinner hourSpinner = (Spinner) findViewById(R.id.hour_spinner);
            hourSpinner.setSelection(user.getHour() - 17);

            Spinner minSpinner = (Spinner) findViewById(R.id.min_spinner);
            minSpinner.setSelection(user.getMin() / 10);

            Spinner needSpinner = (Spinner) findViewById(R.id.need_spinner);
            needSpinner.setSelection(user.isNeed() ? 0 : 1);
        }
    }

    private void onSubmitButtonClick() {
        submit(true);
    }

    private void onClearButtonClick() {
        submit(false);
    }

    private void submit(final boolean defined) {
        new APIAsyncTask(this, "Processing...", new APIAsyncTaskActionListener() {
            @Override
            public Object doTask() {
                Map<String, String> userData = getUserData();
                userData.put("defined", Boolean.toString(defined));

                try {
                    boolean result = APIClient.updateUser(userId, userData);
                    if (result) {
                        return true;
                    } else {
                        Log.e(getString(R.string.app_name), "Failed to submit!");
                    }
                } catch (Exception e) {
                    Log.e(getString(R.string.app_name), e.getMessage());
                }

                return false;
            }
            @Override
            public void onComplete(Object result) {
                if ((Boolean) result) {
                    moveToTop();
                }
            }
        }).execute();
    }

    private void moveToTop() {
        Intent intent = new Intent(SetActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private Map<String, String> getUserData() {
        Map<String, String> params = new HashMap<String, String>();

        String hour = ((Spinner) findViewById(R.id.hour_spinner)).getSelectedItem().toString();
        params.put("hour", hour);

        String min = ((Spinner) findViewById(R.id.min_spinner)).getSelectedItem().toString();
        params.put("min", min);

        String need = ((Spinner) findViewById(R.id.need_spinner)).getSelectedItem().toString();
        params.put("need", need.equalsIgnoreCase("Yes") ? "true" : "false");

        return params;
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
                break;
            case R.id.menu_config:
                Intent configActivityIntent = new Intent(this, ConfigActivity.class);
                configActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(configActivityIntent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
