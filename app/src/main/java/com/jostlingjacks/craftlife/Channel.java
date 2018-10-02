package com.jostlingjacks.craftlife;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class Channel extends Application {
    public static final String CHANNEL_ID_1 = "Regular Notification";
    public static final String CHANNEL_ID_2 = "Art Location Notification";
    public static final String CHANNEL_ID_3 = "Event Notification";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(CHANNEL_ID_1, "Regular Notification", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("Regular Notification");
            NotificationChannel channel2 = new NotificationChannel(CHANNEL_ID_2, "Art Location Notification", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("Art Location Notification");
            NotificationChannel channel3 = new NotificationChannel(CHANNEL_ID_3, "Event Notification", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("Event Notification");


            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);
            manager.createNotificationChannel(channel3);
        }

    }
}
