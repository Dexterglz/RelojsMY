package com.example.myhealthlife.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Upsert;

import com.example.myhealthlife.data.local.entity.HistorySportEntity;

import java.util.List;

@Dao
public interface SportDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(HistorySportEntity entity);
    @Upsert
    void upsert(HistorySportEntity entity);

    @Query(
            "UPDATE sport_history SET " +
                    "sportStep = :sportStep, " +
                    "sportDistance = :sportDistance, " +
                    "sportCalorie = :sportCalorie " +
                    "WHERE timestamp = :timestamp"
    )
    void updateByTimestamp(
            int sportStep,
            int sportDistance,
            int sportCalorie,
            long timestamp
    );

    @Query(
            "SELECT * FROM sport_history " +
                    "ORDER BY timestamp DESC " +
                    "LIMIT 1"
    )
    LiveData<HistorySportEntity> getLastSport();
    // ÚLTIMOS X DÍAS
    @Query(
            "SELECT * FROM sport_history " +
                    "WHERE timestamp >= :from " +
                    "ORDER BY timestamp ASC"
    )
    LiveData<List<HistorySportEntity>> getSportFrom(long from);

    @Query(
            "SELECT * FROM sport_history " +
                    "WHERE synced = 0 " +
                    "ORDER BY timestamp ASC"
    )
    List<HistorySportEntity> getUnsyncedSport();

    @Query(
            "UPDATE sport_history SET synced = 1 WHERE timestamp = :id"
    )
    void markAsSynced(long id);

    @Query(
            "SELECT * FROM sport_history "+
                    "WHERE timestamp <= :healthTimestamp " +
                    "ORDER BY timestamp DESC " +
                    "LIMIT 1"
    )
    HistorySportEntity getClosestSport(long healthTimestamp);

}



