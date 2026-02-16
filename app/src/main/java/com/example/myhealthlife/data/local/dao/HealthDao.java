package com.example.myhealthlife.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.myhealthlife.data.local.entity.HistoryHealthEntity;

import java.util.List;

@Dao
public interface HealthDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(HistoryHealthEntity entity);

    @Query(
            "SELECT * FROM health_history " +
                    "ORDER BY timestamp DESC " +
                    "LIMIT 1"
    )
    LiveData<HistoryHealthEntity> getLastHealth();

    // ÚLTIMOS X DÍAS
    @Query(
            "SELECT * FROM health_history " +
                    "WHERE timestamp >= :from " +
                    "ORDER BY timestamp ASC"
    )
    LiveData<List<HistoryHealthEntity>> getHealthFrom(long from);

    @Query(
            "SELECT * FROM health_history " +
                    "WHERE synced = 0 " +
                    "ORDER BY timestamp ASC"
    )
    List<HistoryHealthEntity> getUnsyncedHealth();

    @Query(
            "UPDATE health_history SET synced = 1 WHERE id = :id"
    )
    void markAsSynced(long id);
}



