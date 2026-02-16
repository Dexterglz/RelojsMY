package com.example.myhealthlife.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "comprehensive_history",
        indices = {
                @Index(value = {"timestamp"}, unique = true)
        }
)
public class HistoryCompEntity {
    @PrimaryKey(autoGenerate = true)
    public int triCholInt;
    public int triCholFloat;
    public int hdlInt;
    public int hdlFloat;
    public int ldlInt;
    public int ldlFloat;
    public int cholesterolInt;
    public int cholesterolFloat;
    public int uricAcid;
    public long timestamp;
    @ColumnInfo(defaultValue = "0")
    public boolean synced;
    public HistoryCompEntity(
            int triCholInt,
            int triCholFloat,
            int hdlInt,
            int hdlFloat,
            int ldlInt,
            int ldlFloat,
            int cholesterolInt,
            int cholesterolFloat,
            int uricAcid,
            long timestamp,
            boolean synced
    )
    {
        this.triCholInt = triCholInt;
        this.triCholFloat = triCholFloat;
        this.hdlInt = hdlInt;
        this.hdlFloat = hdlFloat;
        this.ldlInt = ldlInt;
        this.ldlFloat = ldlFloat;
        this.cholesterolInt = cholesterolInt;
        this.cholesterolFloat = cholesterolFloat;
        this.uricAcid = uricAcid;
        this.timestamp = timestamp;
        this.synced = synced;
    }
}
