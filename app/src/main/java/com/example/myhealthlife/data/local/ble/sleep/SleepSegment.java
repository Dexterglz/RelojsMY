package com.example.myhealthlife.data.local.ble.sleep;

import com.yucheng.ycbtsdk.Constants;

public class SleepSegment {
    //Cada tramo de sueño
    public final long startTime;
    public final int duration;
    public final Constants.SleepType type;

    public SleepSegment(long startTime, int duration, Constants.SleepType type) {
        this.startTime = startTime;
        this.duration = duration;
        this.type = type;
    }
}

