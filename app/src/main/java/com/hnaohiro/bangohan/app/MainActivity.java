package com.hnaohiro.bangohan.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cengalabs.flatui.FlatUI;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FlatUI.initDefaultValues(this);
        FlatUI.setDefaultTheme(FlatUI.DEEP);
        getActionBar().setBackgroundDrawable(FlatUI.getActionBarDrawable(this, FlatUI.DEEP, true));

        startRemindService();
        registerInBackground();
    }

    private void startRemindService() {
        Intent intent = new Intent(this, RemindService.class);
        startService(intent);
    }

    private String registrationId = "";

    private void registerInBackground() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(MainActivity.this);
                    String projectId = getString(R.string.gcm_project_number);
                    registrationId = gcm.register(projectId);

                    sendRegistrationId(registrationId);

                    Log.d("Bangohan", "Device registered, registration ID=" + registrationId);
                } catch (IOException e) {
                    Log.e("Bangohan", "Error: " + e.getMessage());
                }

                return null;
            }
        }.execute(null, null, null);
    }

    @Override
    public void onStart() {
        super.onStart();

        fetchUsers();
    }

    private void fetchUsers() {
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

    private void sendRegistrationId(String registrationId) {
        Config config = new Config(this);
        int userId = config.getUserId();
        if (userId != -1) {
            new APIClient(MainActivity.this).register(userId, registrationId);
        }
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
                Intent setActivityIntent = new Intent(this, SetActivity.class);
                setActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(setActivityIntent);
                break;
            case R.id.menu_config:
                Intent configActivityIntent = new Intent(this, ConfigActivity.class);
                configActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(configActivityIntent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setUsers(JSONArray json) throws JSONException {
        List<UserData> users = UserData.fromJSONObject(json);
        ListView listView = (ListView) findViewById(R.id.user_list);
        listView.setAdapter(new UserDataAdaptor(this, 0, users));
    }

    private class UserDataAdaptor extends ArrayAdapter<UserData> {

        private LayoutInflater layoutInflater;
        private Resources resources;

        public UserDataAdaptor(Context context, int textViewResourceId, List<UserData> objects) {
            super(context, textViewResourceId, objects);

            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            resources = layoutInflater.getContext().getResources();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            UserData user = getItem(position);

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.list_item, null);
            }

            Bitmap statusImage;
            String userTime;
            int textColor;

            if (user.isDefined()) {
                if (user.isNeed()) {
                    userTime = user.getTime();
                    statusImage = BitmapFactory.decodeResource(resources, R.drawable.fork);
                    textColor = resources.getColor(R.color.sky_primary);
                } else {
                    userTime = "";
                    statusImage = BitmapFactory.decodeResource(resources, R.drawable.remove);
                    textColor = resources.getColor(R.color.candy_primary);
                }
            } else {
                userTime = "";
                statusImage = BitmapFactory.decodeResource(resources, R.drawable.question);
                textColor = resources.getColor(R.color.dark_light);
            }

            ImageView userStatusImageView = (ImageView) convertView.findViewById(R.id.user_status);
            userStatusImageView.setImageBitmap(statusImage);

            TextView userTimeTextView = (TextView) convertView.findViewById(R.id.user_time);
            userTimeTextView.setTextColor(textColor);
            userTimeTextView.setText(userTime);

            TextView userNameTextView = (TextView) convertView.findViewById(R.id.user_name);
            userNameTextView.setTextColor(textColor);
            userNameTextView.setText(user.getName());

            return convertView;
        }
    }
}
