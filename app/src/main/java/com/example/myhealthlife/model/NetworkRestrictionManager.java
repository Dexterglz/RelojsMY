package com.example.myhealthlife.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

public class NetworkRestrictionManager {
    private Context context;
    private SharedPreferences prefs;

    public NetworkRestrictionManager(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences("network_prefs", Context.MODE_PRIVATE);
    }

    public void enableDataRestriction(int durationMinutes) {
        long expirationTime = System.currentTimeMillis() + (durationMinutes * 60 * 1000);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("data_restriction_until", expirationTime);
        editor.apply();
    }

    public void disableDataRestriction() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("data_restriction_until");
        editor.apply();
    }

    public boolean isDataRestricted() {
        long restrictionUntil = prefs.getLong("data_restriction_until", 0);
        return restrictionUntil > System.currentTimeMillis();
    }

    public boolean canUseMobileData() {
        // Si hay restricción temporal, solo permitir WiFi
        if (isDataRestricted()) {
            return isWifiConnected();
        }
        return true;
    }

    public boolean isWifiConnected() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        }

        Network network = connectivityManager.getActiveNetwork();
        if (network == null) {
            return false;
        }

        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
        return capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
    }

    public long getRestrictionTimeLeft() {
        long restrictionUntil = prefs.getLong("data_restriction_until", 0);
        long currentTime = System.currentTimeMillis();

        if (restrictionUntil > currentTime) {
            return (restrictionUntil - currentTime) / 1000; // Devuelve segundos restantes
        }
        return 0;
    }
}
