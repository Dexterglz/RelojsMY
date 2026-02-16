package com.example.myhealthlife.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "sleep_history")

public class HistorySleepEntity {
    @PrimaryKey
    public long timestamp;
    public final long endTime;
    public final int wakeCount;
    public final int wakeDuration;
    public final int deepSleepTotal;
    public final int lightSleepTotal;
    public final int remTotal;
    @ColumnInfo(defaultValue = "0")
    public boolean synced;

    public HistorySleepEntity(
            long timestamp,
            long endTime,
            int wakeCount,
            int wakeDuration,
            int deepSleepTotal,
            int lightSleepTotal,
            int remTotal,
            boolean synced
    ) {
        this.timestamp = timestamp;
        this.endTime = endTime;
        this.wakeCount = wakeCount;
        this.wakeDuration = wakeDuration;
        this.deepSleepTotal = deepSleepTotal;
        this.lightSleepTotal = lightSleepTotal;
        this.remTotal = remTotal;
        this.synced = synced;
    }
}
