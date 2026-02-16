package com.example.myhealthlife.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.myhealthlife.data.local.entity.HistorySleepEntity;

import java.util.List;

@Dao
public interface SleepDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(HistorySleepEntity entity);

    @Query(
            "SELECT * FROM sleep_history " +
                    "ORDER BY timestamp DESC " +
                    "LIMIT 1"
    )
    LiveData<HistorySleepEntity> getLastSleep();

    // ÚLTIMOS X DÍAS
    @Query(
            "SELECT * FROM sleep_history " +
                    "WHERE timestamp >= :from " +
                    "ORDER BY timestamp ASC"
    )
    LiveData<List<HistorySleepEntity>> getSleepFrom(long from);

    @Query(
            "SELECT * FROM sleep_history " +
                    "WHERE synced = 0 " +
                    "ORDER BY timestamp ASC"
    )
    List<HistorySleepEntity> getUnsyncedSleep();

    @Query(
            "UPDATE sleep_history SET synced = 1 WHERE timestamp = :id"
    )
    void markAsSynced(long id);

    @Query(
            "SELECT * FROM sleep_history "+
            "WHERE timestamp <= :healthTimestamp " +
                    "ORDER BY timestamp DESC " +
                    "LIMIT 1"
    )
    HistorySleepEntity getClosestSleep(long healthTimestamp);

    @Query(
            "SELECT * FROM sleep_history " +
                    "WHERE timestamp BETWEEN :startOfDay AND :endOfDay " +
                    "ORDER BY timestamp DESC " +
                    "LIMIT 1"
    )
    HistorySleepEntity getSleepForDay(long startOfDay, long endOfDay);

}



