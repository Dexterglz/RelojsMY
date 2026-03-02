package com.example.myhealthlife.data.local.repository.mapper;

import com.example.myhealthlife.data.local.entity.HistoryBloodEntity;
import com.example.myhealthlife.data.local.entity.HistoryCompEntity;
import com.example.myhealthlife.data.local.entity.HistoryHealthEntity;
import com.example.myhealthlife.data.local.entity.HistorySleepEntity;
import com.example.myhealthlife.data.local.entity.HistorySportEntity;
import com.example.myhealthlife.io.response.HistorySendData;

import java.util.concurrent.TimeUnit;

public class HealthMapper {

    public static HistorySendData toSendData(
            HistoryHealthEntity health,
            HistorySleepEntity sleep,
            HistoryBloodEntity blood,
            HistorySportEntity sport,
            HistoryCompEntity comp
    ) {

        return new HistorySendData(
                "138",
                health != null ? health.heartValue : null,
                health != null ? health.oxygenValue : null,
                blood != null ? blood.dbpValue : null,
                blood != null ? blood.sbpValue : null,
                health != null ? health.respRateValue : null,
                health != null ? health.bloodSugarValue : null,
                health != null ? (health.tempIntValue == 0 ?  35 : health.tempIntValue) : null ,
                health != null ? health.tempFloatValue : null,
                null /*String.valueOf(health.timestamp)*/,
                comp != null ? Float.parseFloat(comp.triCholInt + "." + comp.cholesterolFloat) : null,
                sleep != null ? getIntSleep(sleep.endTime, sleep.timestamp) : null,
                comp != null ? (float) comp.uricAcid : null,
                sport != null ? sport.sportStep : null,
                sport != null ? sport.sportCalorie : null,
                health != null ? (float) health.cvrrValue : null,
                health != null ? (float) health.cvrrValue : null
        );
    }

    public static Float getSleepHours(long endTime, long timestamp){
        long durationMillis = endTime - timestamp;
        float hours = durationMillis / (1000f * 60 * 60);
        return hours;
    }
    public static Integer getIntSleep(long endTime, long timestamp){
        long durationMillis = endTime - timestamp;
        int hours = (int) TimeUnit.MILLISECONDS.toHours(durationMillis);
        return hours;
    }
}

