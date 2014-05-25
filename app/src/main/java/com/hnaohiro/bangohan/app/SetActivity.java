package com.hnaohiro.bangohan.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.cengalabs.flatui.FlatUI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


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

        fetchUser();
    }

    private void fetchUser() {
        final Config config = new Config(this);

        int id = config.getUserId();
        if (id == 0) {
            config.alertNoConfig("Userを設定してください。");
            return;
        }

        new APIClient(this).getUser(id, new APIClient.APIActionListener() {
            @Override
            public void onSuccess(String content) {
                if (config == null) {
                    Toast.makeText(SetActivity.this, "Failed to get User!", 10000).show();
                    return;
                }

                try {
                    JSONObject json = new JSONObject(content);
                    setUser(json);
                } catch (JSONException e) {
                    Toast.makeText(SetActivity.this, e.getMessage(), 10000).show();
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(SetActivity.this, message, 10000).show();
            }
        });
    }

    private void setUser(JSONObject json) throws JSONException {
        UserData user = UserData.fromJSONObject(json);

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
        Map<String, String> userData = getUserData();
        userData.put("defined", "true");

        new APIClient(this).updateUser(1, userData, new APIClient.APIActionListener() {
            @Override
            public void onSuccess(String result) {
                Intent intent = new Intent(SetActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(SetActivity.this, message, 10000).show();
            }
        });
    }

    private void onClearButtonClick() {
        Map<String, String> userData = new HashMap<String, String>();
        userData.put("defined", "false");

        new APIClient(this).updateUser(1, userData, new APIClient.APIActionListener() {
            @Override
            public void onSuccess(String result) {
                Intent intent = new Intent(SetActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(SetActivity.this, message, 10000).show();
            }
        });
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
