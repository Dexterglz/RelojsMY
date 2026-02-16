package com.example.myhealthlife.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "health_history",
        indices = {
                @Index(value = {"timestamp"}, unique = true)
        }
)
public class HistoryHealthEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;
    public int heartValue;
    public int hrvValue;
    public int cvrrValue;
    public int oxygenValue;
    public int stepValue;
    public int diastolicValue;
    public int systolicValue;
    public int respRateValue;
    public int bodyFatValue;
    public int bodyFatFracValue;
    public int bloodSugarValue;
    public int tempIntValue;
    public int tempFloatValue;
    public long timestamp;
    @ColumnInfo(defaultValue = "0")
    public boolean synced;

    public HistoryHealthEntity(long timestamp,
            int heartValue, int hrvValue, int cvrrValue, int stepValue, int oxygenValue,
            int diastolicValue, int systolicValue, int respRateValue, int bodyFatValue,
            int bodyFatFracValue, int bloodSugarValue, int tempIntValue, int tempFloatValue, boolean synced)
    {

        this.heartValue = heartValue;
        this.hrvValue = hrvValue;
        this.cvrrValue = cvrrValue;
        this.stepValue = stepValue;
        this.oxygenValue = oxygenValue;
        this.diastolicValue = diastolicValue;
        this.systolicValue = systolicValue;
        this.respRateValue = respRateValue;
        this.bodyFatValue = bodyFatValue;//*
        this.bodyFatFracValue = bodyFatFracValue;//*
        this.bloodSugarValue = bloodSugarValue;//*
        this.tempIntValue = tempIntValue;
        this.tempFloatValue = tempFloatValue;
        this.timestamp = timestamp;
        this.synced = synced;
    }
}
