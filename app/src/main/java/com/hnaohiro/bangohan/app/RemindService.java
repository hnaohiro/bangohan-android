package com.hnaohiro.bangohan.app;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by hnaohiro on 2014/05/25.
 */
public class RemindService extends IntentService {

    public RemindService() {
        super("RemindService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Config config = new Config(this);
        int sleepTime = 45 * 1000;

        while (true) {
            int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            int currentMin = Calendar.getInstance().get(Calendar.MINUTE);

            synchronized (this) {
                if (config.getHour() == currentHour && config.getMin() >= currentMin) {
                    sendNotification("何時に晩ご飯を食べますか？");
                }

                try {
                    wait(sleepTime);
                } catch (Exception e) {
                }
            }
        }
    }

    private void sendNotification(String message) {
        NotificationManager manager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent =
                PendingIntent.getActivity(this, 0, new Intent(this, SetActivity.class), 0);

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
}
