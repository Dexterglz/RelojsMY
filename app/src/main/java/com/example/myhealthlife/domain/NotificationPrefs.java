package com.example.myhealthlife.domain;

import android.content.Context;
import android.content.SharedPreferences;

public class NotificationPrefs {

    private static final String PREFS = "device_battery_prefs";
    private static final String KEY_NOTIFIED = "notified";

    public static boolean shouldNotify(Context context) {

        SharedPreferences prefs =
                context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        boolean notified = prefs.getBoolean(KEY_NOTIFIED, false);

        if (!notified) {
            prefs.edit().putBoolean(KEY_NOTIFIED, true).apply();
            return true;
        }
        return false;
    }

    public static void reset(Context context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(KEY_NOTIFIED, false)
                .apply();
    }
}

