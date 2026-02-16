package com.example.myhealthlife.data.local.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.myhealthlife.data.local.dao.BloodDao;
import com.example.myhealthlife.data.local.dao.CompDao;
import com.example.myhealthlife.data.local.dao.HealthDao;
import com.example.myhealthlife.data.local.dao.UserDao;
import com.example.myhealthlife.data.local.entity.HistoryBloodEntity;
import com.example.myhealthlife.data.local.entity.HistoryCompEntity;
import com.example.myhealthlife.data.local.entity.HistoryHealthEntity;
import com.example.myhealthlife.data.local.entity.HistorySleepEntity;
import com.example.myhealthlife.data.local.entity.HistorySportEntity;
import com.example.myhealthlife.data.local.dao.SleepDao;
import com.example.myhealthlife.data.local.dao.SportDao;
import com.example.myhealthlife.data.local.entity.UserEntity;

@Database(
        entities = {
                HistoryHealthEntity.class,
                HistorySleepEntity.class,
                HistorySportEntity.class,
                HistoryCompEntity.class,
                HistoryBloodEntity.class,
                UserEntity.class
        },
        version = 9,
        exportSchema = false

)

public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract HealthDao healthDao();
    public abstract SleepDao sleepDao();
    public abstract CompDao compDao();
    public abstract BloodDao bloodDao();
    public abstract SportDao sportDao();
    public abstract UserDao userDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "health_db"
                    )
                            .fallbackToDestructiveMigration() //SOLO PRODUCCION
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}




