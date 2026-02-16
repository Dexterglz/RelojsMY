package com.example.myhealthlife.repository;

import com.example.myhealthlife.data.Mapper;
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
import com.example.myhealthlife.domain.ResponseBody;
import com.example.myhealthlife.io.response.ApiClient;
import com.example.myhealthlife.io.response.ApiService;
import com.example.myhealthlife.io.response.HistorySendData;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;

import retrofit2.Response;

public class SyncRepository {

    private final HealthDao healthDao;
    private final SleepDao sleepDao;
    private final BloodDao bloodDao;
    private final SportDao sportDao;
    private final CompDao compDao;
    private final ApiService apiService;

    public SyncRepository(AppDatabase db) {
        healthDao = db.healthDao();
        sleepDao = db.sleepDao();
        bloodDao = db.bloodDao();
        sportDao = db.sportDao();
        compDao = db.compDao();
        apiService = ApiClient.newClient().create(ApiService.class);
    }

    public interface OnSuccess {
        void done();
    }

    public interface OnError {
        void error(String msg);
    }

    public void syncAll(
            OnSuccess success,
            OnError error
    ) {

        Executors.newSingleThreadExecutor().execute(() -> {

            List<HistoryHealthEntity> healthList = healthDao.getUnsyncedHealth();

            for (HistoryHealthEntity health : healthList) {

                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(health.timestamp);

                // Inicio del día
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                long startOfDay = cal.getTimeInMillis();

                // Fin del día
                cal.add(Calendar.DAY_OF_MONTH, 1);
                long endOfDay = cal.getTimeInMillis() - 1;

                HistorySleepEntity sleep =
                        sleepDao.getSleepForDay(startOfDay, endOfDay);
                HistoryBloodEntity blood =
                        bloodDao.getClosestBlood(health.timestamp);
                HistorySportEntity sport =
                        sportDao.getClosestSport(health.timestamp);
                HistoryCompEntity comp =
                        compDao.getClosestComp(health.timestamp);

                HistorySendData data = Mapper.toSendData(
                        health,
                        sleep,
                        blood,
                        sport,
                        comp
                );

                try {
                    Response<ResponseBody> response =
                            apiService.agregarHistorial(
                                    data
                            ).execute();

                    if (response.isSuccessful()) {
                        healthDao.markAsSynced(health.id);
                        success.done();
                    }

                } catch (Exception e) {
                    error.error(e.getMessage());
                    break;
                }
            }
        });
    }
}

