package com.example.myhealthlife.model;

public class DataPoint {
    private float value;
    private String timestamp;

    public DataPoint(float value, String timestamp) {
        this.value = value;
        this.timestamp = timestamp;
    }

    public float getValue() { return value; }
    public String getTimestamp() { return timestamp; }
}

