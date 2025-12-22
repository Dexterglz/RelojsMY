package com.example.myhealthlife.model;

import static com.yucheng.ycbtsdk.YCBTClient.connectBle;
import static com.yucheng.ycbtsdk.YCBTClient.connectState;
import static com.yucheng.ycbtsdk.YCBTClient.disconnectBle;
import static com.yucheng.ycbtsdk.YCBTClient.startScanBle;
import static com.yucheng.ycbtsdk.YCBTClient.stopScanBle;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;

import com.yucheng.ycbtsdk.bean.ScanDeviceBean;
import com.yucheng.ycbtsdk.response.BleConnectResponse;
import com.yucheng.ycbtsdk.response.BleScanResponse;

import java.util.ArrayList;
import java.util.List;

public class BleManager {

    private static BleManager instance;

    private List<ScanDeviceBean> scannedDevices = new ArrayList<>();
    private BleScanResponse scanCallback;
    private BleConnectResponse connectCallback;

    private String lastMac = null;

    public static BleManager getInstance(Context context){
        if(instance == null){
            instance = new BleManager(context);
        }
        return instance;
    }

    private BleManager(Context context){
        Context appContext = context.getApplicationContext();
    }

    // -------- SCAN -------- //
    public void startScan(BleScanResponse callback){
        this.scanCallback = callback;
        scannedDevices.clear();
        startScanBle(internalScanCallback, 6);
    }

    private final BleScanResponse internalScanCallback = new BleScanResponse() {
        @Override
        public void onScanResponse(int code, ScanDeviceBean device) {
            if(scanCallback != null){
                scanCallback.onScanResponse(code, device);
            }
        }
    };

    public void stopScan(){
        stopScanBle();
    }

    // -------- CONNECT -------- //
    public void connectDevice(String mac, BleConnectResponse callback){
        this.connectCallback = callback;
        this.lastMac = mac;

        connectBle(mac, internalConnectCallback);
    }

    private final BleConnectResponse internalConnectCallback = new BleConnectResponse() {
        @Override
        public void onConnectResponse(int code) {
            if(connectCallback != null){
                connectCallback.onConnectResponse(code);
            }
        }
    };

    public void disconnect(){
        disconnectBle();
    }

    // -------- STATE -------- //
    public int getState(){
        return connectState();
    }

    public String getLastMac(){
        return lastMac;
    }

    public boolean hasBlePermissions(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        }
    }

}

