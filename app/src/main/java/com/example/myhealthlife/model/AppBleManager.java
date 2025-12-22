package com.example.myhealthlife.model;

import static android.content.Context.MODE_PRIVATE;

import static com.realsil.sdk.core.preference.SharedPrefesHelper.getSharedPreferences;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.yucheng.ycbtsdk.Constants;

public class AppBleManager {

    private static AppBleManager instance;
    private static final MutableLiveData<Boolean> isConnected = new MutableLiveData<>(false);

    private static BleManager ble;

    private AppBleManager(Context context) {
        ble = BleManager.getInstance(context);
    }

    public static synchronized AppBleManager getInstance(Context context) {
        if (instance == null) {
            instance = new AppBleManager(context);
        }
        return instance;
    }

    public LiveData<Boolean> getConnectionState() {
        return isConnected;
    }

    public void connect(String mac, Context context) {

        ble.connectDevice(mac, code -> {

            switch (ble.getState()) {

                case Constants.BLEState.ReadWriteOK:
                    isConnected.postValue(true);
                    saveLastMac(mac, context);
                    break;

                case Constants.BLEState.Disconnect:
                case Constants.BLEState.TimeOut:
                    isConnected.postValue(false);
                    break;
            }
        });
    }


    public void disconnect() {
        ble.disconnect();
        isConnected.postValue(false);
    }

    public BleManager getBle() {
        return ble;
    }

    private void saveLastMac(String mac, Context context) {
        getSharedPreferences(context,"ble_prefs")
                .edit()
                .putString("last_mac", mac)
                .apply();
    }



}

