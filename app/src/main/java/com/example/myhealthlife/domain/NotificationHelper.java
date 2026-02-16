package com.example.myhealthlife.domain;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.myhealthlife.R;

public class NotificationHelper {

    private static final String CHANNEL_ID = "battery_channel";

    public static void showLowBatteryNotification(Context context, int battery) {

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Canal (Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Battery alerts",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Low battery alerts");
            manager.createNotificationChannel(channel);
        }

        Notification notification =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.battery_low)
                        .setContentTitle("Batería baja")
                        .setContentText("Nivel de batería: " + battery + "%")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .build();

        manager.notify(1001, notification);
    }

}

