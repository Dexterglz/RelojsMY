package com.example.myhealthlife.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "blood_history",
        indices = {
                @Index(value = {"timestamp"}, unique = true)
        }
)
public class HistoryBloodEntity {
    @PrimaryKey(autoGenerate = true)
    public int dbpValue;
    public int sbpValue;
    public long timestamp;
    @ColumnInfo(defaultValue = "0")
    public boolean synced;
    public HistoryBloodEntity(
            int sbpValue,
            int dbpValue,
            long timestamp,
            boolean synced
    )
    {

        this.dbpValue = dbpValue;
        this.sbpValue = sbpValue;
        this.timestamp = timestamp;
        this.synced = synced;
    }


}
