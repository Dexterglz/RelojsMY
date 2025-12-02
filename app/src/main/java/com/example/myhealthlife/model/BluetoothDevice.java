package com.example.myhealthlife.model;

public class BluetoothDevice {
    private String name, mac, rssi;

    public BluetoothDevice(String name, String mac, String rssi) {
        this.name = name;
        this.mac = mac;
        this.rssi = rssi;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getMac() {
        return mac;
    }

    public String getRssi() {
        return rssi;
    }

    // Setters (opcionales)
    public void setName(String name) {
        this.name = name;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    @Override
    public String toString() {
        return name + " - " + mac;
    }
}