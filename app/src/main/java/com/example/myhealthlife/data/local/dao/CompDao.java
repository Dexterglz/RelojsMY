package com.example.myhealthlife.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.myhealthlife.data.local.entity.HistoryCompEntity;

import java.util.List;

@Dao
public interface CompDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(HistoryCompEntity entity);

    @Query(
            "SELECT * FROM comprehensive_history " +
                    "ORDER BY timestamp DESC " +
                    "LIMIT 1"
    )
    LiveData<HistoryCompEntity> getLastComprehensive();
    // ÚLTIMOS X DÍAS
    @Query(
            "SELECT * FROM comprehensive_history " +
                    "WHERE timestamp >= :from " +
                    "ORDER BY timestamp ASC"
    )
    LiveData<List<HistoryCompEntity>> getCompFrom(long from);

    @Query(
            "SELECT * FROM comprehensive_history " +
                    "WHERE synced = 0 " +
                    "ORDER BY timestamp ASC"
    )
    List<HistoryCompEntity> getUnsyncedComp();

    @Query(
            "UPDATE comprehensive_history SET synced = 1 WHERE timestamp = :id"
    )
    void markAsSynced(int id);

    @Query(
            "SELECT * FROM comprehensive_history "+
                    "WHERE timestamp <= :healthTimestamp " +
                    "ORDER BY timestamp DESC " +
                    "LIMIT 1"
    )
    HistoryCompEntity getClosestComp(long healthTimestamp);
}



