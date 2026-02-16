package com.example.myhealthlife.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "sport_history")

public class HistorySportEntity {
    @PrimaryKey
    public long timestamp;
    public int sportStep;
    public int sportDistance;
    public int sportCalorie;
    @ColumnInfo(defaultValue = "0")
    public boolean synced;

    public HistorySportEntity(
            int sportStep,
            int sportDistance,
            int sportCalorie,
            long timestamp,
            boolean synced
    ) {
        this.sportStep = sportStep;
        this.sportDistance = sportDistance;
        this.sportCalorie = sportCalorie;
        this.timestamp = timestamp;
        this.synced = synced;
    }
}

