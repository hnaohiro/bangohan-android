package com.hnaohiro.bangohan.app;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hnaohiro on 2014/05/19.
 */
public class GcmIntentService extends IntentService {

    public GcmIntentService() {
        super("Bangohan");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                try {
                    String message = createMessage(extras.getString("default"));
                    sendNotification(message);
                } catch (JSONException e) {}
            }
        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String message) {
        NotificationManager manager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent =
                PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(getString(R.string.app_name))
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setContentText(message);

        builder.setContentIntent(contentIntent);
        manager.notify(0, builder.build());
    }

    private String createMessage(String json) throws JSONException {
        UserData userData = UserData.fromJSONObject(new JSONObject(json));

        if (!userData.isDefined()) {
            return userData.getName() + "が未定に変更しました。";
        } else {
            if (userData.isNeed()) {
                return userData.getName() + "は" + userData.getTime() + "に晩ご飯をたべます。";
            } else {
                return userData.getName() + "は今日はいりません。";
            }
        }
    }
}
