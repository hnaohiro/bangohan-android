package com.hnaohiro.bangohan.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.cengalabs.flatui.FlatUI;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FlatUI.initDefaultValues(this);
        FlatUI.setDefaultTheme(FlatUI.DEEP);
        getActionBar().setBackgroundDrawable(FlatUI.getActionBarDrawable(this, FlatUI.DEEP, true));
    }

    @Override
    public void onStart() {
        super.onStart();

        new APIClient(this).getUsers(new APIClient.APIActionListener() {
             @Override
             public void onSuccess(String content) {
                 try {
                     JSONArray json = new JSONArray(content);
                     setUsers(json);
                 } catch (JSONException e) {
                     Toast.makeText(MainActivity.this, e.getMessage(), 10000).show();
                 }
             }

             @Override
             public void onError(String message) {
                 Toast.makeText(MainActivity.this, message, 10000).show();
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
                onStart();
                break;
            case R.id.menu_set:
                Intent intent = new Intent(this, SetActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.menu_config:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setUsers(JSONArray json) throws JSONException {
        List<UserData> users = UserData.fromJSONObject(json);
        ListView listView = (ListView) findViewById(R.id.user_list);
        listView.setAdapter(new UsersAdaptor(this, 0, users));
    }
}
