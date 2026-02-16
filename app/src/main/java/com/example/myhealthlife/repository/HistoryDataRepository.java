package com.example.myhealthlife.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.myhealthlife.data.local.ble.BleCallback;
import com.example.myhealthlife.data.local.dao.BloodDao;
import com.example.myhealthlife.data.local.dao.CompDao;
import com.example.myhealthlife.data.local.dao.HealthDao;
import com.example.myhealthlife.data.local.dao.SleepDao;
import com.example.myhealthlife.data.local.dao.SportDao;
import com.example.myhealthlife.data.local.db.AppDatabase;
import com.example.myhealthlife.data.local.entity.HistoryBloodEntity;
import com.example.myhealthlife.data.local.entity.HistoryCompEntity;
import com.example.myhealthlife.data.local.entity.HistoryHealthEntity;
import com.example.myhealthlife.data.local.entity.HistorySleepEntity;
import com.example.myhealthlife.data.local.entity.HistorySportEntity;

import java.util.concurrent.Executors;

public class HistoryDataRepository {
    private HealthDao daoH;
    private SleepDao daoS;
    private CompDao daoC;
    private BloodDao daoB;
    private SportDao daoSp;
    private BleCallback bleSource;

    public HistoryDataRepository(Context context) {
        daoH = AppDatabase.getInstance(context).healthDao();
        daoS = AppDatabase.getInstance(context).sleepDao();
        daoC = AppDatabase.getInstance(context).compDao();
        daoSp = AppDatabase.getInstance(context).sportDao();
        daoB = AppDatabase.getInstance(context).bloodDao();
        bleSource = new BleCallback();
    }
    //Health
    public LiveData<HistoryHealthEntity> getLastHealth() {
        return daoH.getLastHealth();
    }
    public void insertHealth(HistoryHealthEntity entity) {
        Executors.newSingleThreadExecutor().execute(() -> {
            daoH.insert(entity);
        });
    }

    //Sleep
    public LiveData<HistorySleepEntity> getLastSleep() {
        return daoS.getLastSleep();
    }
    public void insertSleep(HistorySleepEntity entity) {
        Executors.newSingleThreadExecutor().execute(() -> {
            daoS.insert(entity);
        });
    }
    //Blood
    public LiveData<HistoryBloodEntity> getLastBlood() {
        return daoB.getLastBlood();
    }

    public void insertBlood(HistoryBloodEntity entity) {
        Executors.newSingleThreadExecutor().execute(() -> {
            daoB.insert(entity);
        });
    }
    //Sport
    public LiveData<HistorySportEntity> getLastSport() {
        return daoSp.getLastSport();
    }
    public void insertSport(HistorySportEntity entity) {
        Executors.newSingleThreadExecutor().execute(() -> {
            daoSp.insert(entity);
        });
    }
    public void updateSport(HistorySportEntity entity) {
        Executors.newSingleThreadExecutor().execute(() -> {
            daoSp.upsert(entity);
        });
    }
    //Comprehensive
    public LiveData<HistoryCompEntity> getLastComp() {
        return daoC.getLastComprehensive();
    }
    public void insertComp(HistoryCompEntity entity) {
        Executors.newSingleThreadExecutor().execute(() -> {
            daoC.insert(entity);
        });
    }
    /*public void syncAllHistory() {
        getAllHealthData();
    }*/
}
