package com.example.myhealthlife.data.local.repository;

import android.content.Context;
import android.util.Log;

import com.example.myhealthlife.domain.NotificationHelper;
import com.example.myhealthlife.domain.NotificationPrefs;
import com.yucheng.ycbtsdk.YCBTClient;

public class DeviceRepository {

    private static final int LOW_BATTERY = 25;
    private static final int RESET_THRESHOLD = 30;

    private final Context context;

    public DeviceRepository(Context context) {
        this.context = context.getApplicationContext();
    }

    public int checkBatteryAndNotify() {

        int battery = YCBTClient.getDeviceBatteryValue();

        Log.d("BATTERY_REPO", "Batería SDK: " + battery);

        if (battery <= LOW_BATTERY) {
            Log.d("BATTERY_REPO", "LOW_BATTERY");
            if (NotificationPrefs.shouldNotify(context)) {
                Log.d("BATTERY_REPO", "DEBE NOTIFICAR");
                NotificationHelper.showLowBatteryNotification(context, battery);
            }
        } else if (battery > RESET_THRESHOLD) {
            Log.d("BATTERY_REPO", "DEBE RESETAR");
            NotificationPrefs.reset(context);
        }

        return battery;
    }
}


