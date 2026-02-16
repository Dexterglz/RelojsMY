package com.example.myhealthlife.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.myhealthlife.data.local.entity.HistoryBloodEntity;

import java.util.List;

@Dao
public interface BloodDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(HistoryBloodEntity entity);

    @Query(
            "SELECT * FROM blood_history " +
                    "ORDER BY timestamp DESC " +
                    "LIMIT 1"
    )
    LiveData<HistoryBloodEntity> getLastBlood();

    // ÚLTIMOS X DÍAS
    @Query(
            "SELECT * FROM blood_history " +
                    "WHERE timestamp >= :from " +
                    "ORDER BY timestamp ASC"
    )
    LiveData<List<HistoryBloodEntity>> getBloodFrom(long from);

    @Query(
            "SELECT * FROM blood_history " +
                    "WHERE synced = 0 " +
                    "ORDER BY timestamp ASC"
    )
    List<HistoryBloodEntity> getUnsyncedBlood();

    @Query(
            "UPDATE blood_history SET synced = 1 WHERE timestamp = :id"
    )
    void markAsSynced(int id);

    @Query(
            "SELECT * FROM blood_history "+
                    "WHERE timestamp <= :healthTimestamp " +
                    "ORDER BY timestamp DESC " +
                    "LIMIT 1"
    )
    HistoryBloodEntity getClosestBlood(long healthTimestamp);
}



