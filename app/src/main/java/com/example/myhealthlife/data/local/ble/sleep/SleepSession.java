package com.example.myhealthlife.data.local.ble.sleep;

import java.util.List;

public class SleepSession {
    public final long startTime;
    public final long endTime;
    public final int wakeCount;
    public final int wakeDuration;
    public final int deepSleepTotal;
    public final int lightSleepTotal;
    public final int remTotal;
    public final List<SleepSegment> segments;

    public SleepSession(
            long startTime,
            long endTime,
            int wakeCount,
            int wakeDuration,
            int deepSleepTotal,
            int lightSleepTotal,
            int remTotal,
            List<SleepSegment> segments
    ) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.wakeCount = wakeCount;
        this.wakeDuration = wakeDuration;
        this.deepSleepTotal = deepSleepTotal;
        this.lightSleepTotal = lightSleepTotal;
        this.remTotal = remTotal;
        this.segments = segments;
    }
}

