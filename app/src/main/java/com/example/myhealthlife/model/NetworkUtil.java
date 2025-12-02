package com.example.myhealthlife.model;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.widget.Toast;

public class NetworkUtil {

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
        return nc != null && nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
    }

    public static boolean isMobileDataConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
        return nc != null && nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
        return nc != null && (
                nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
    }

    public static boolean evaluarRed(Context context) {

        SharedPreferences prefs = context.getSharedPreferences("settings", MODE_PRIVATE);
        String mode = prefs.getString("network_mode", "any"); // default: permite todo

        boolean wifi = NetworkUtil.isWifiConnected(context);
        boolean mobile = NetworkUtil.isMobileDataConnected(context);

        switch (mode) {

            case "wifi_only":
                if (!wifi) {
                    // Mostrar aviso al usuario
                    Toast.makeText(context, "La app está configurada para usar solo Wi-Fi.", Toast.LENGTH_LONG).show();
                    return true;
                }
                break;

            case "any":
                if (!wifi && !mobile) {
                    Toast.makeText(context, "No hay conexión disponible.", Toast.LENGTH_LONG).show();
                    return false;
                }
                break;
        }
        return wifi;
    }

}

