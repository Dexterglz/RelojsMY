package com.example.myhealthlife.domain.util;

import static com.example.myhealthlife.repository.ChartRepository.TimeInterval.TODAY_REPEAT;
import static com.example.myhealthlife.repository.ChartRepository.TimeInterval.TODAY_WITH_0;

import androidx.lifecycle.LiveData;

import com.example.myhealthlife.repository.ChartRepository;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {
    public static String timeToString(long timestamp,String pattern) {
        Date date = new Date(timestamp);
        return new SimpleDateFormat(pattern, Locale.getDefault()).format(date);
    }
    public static String formatForInterval(
            long timestamp,
            ChartRepository.TimeInterval interval
    ) {
        if (interval == ChartRepository.TimeInterval.TODAY_REPEAT
                || interval == ChartRepository.TimeInterval.TODAY_WITH_0) {

            return timeToString(timestamp, "HH:mm");
        }

        return timeToString(timestamp, "dd/MM");
    }

    // TIMESTAMPS DE APOYO
    public static long startOfToday() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }
    public static long daysAgo(int days) {
        return System.currentTimeMillis() - (days * 24L * 60 * 60 * 1000);
    }
    public static long getStart(ChartRepository.TimeInterval interval) {
        switch (interval) {
            case TODAY_REPEAT:
            case TODAY_WITH_0:
                return startOfToday();
            case LAST_7_DAYS:
                return daysAgo(7);
            case LAST_30_DAYS:
                return daysAgo(30);
            default:
                throw new IllegalArgumentException("Intervalo no soportado");
        }
    }
    public static boolean isToday(long timestamp) {
        Calendar today = Calendar.getInstance();

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(timestamp));

        return today.get(Calendar.YEAR) == cal.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == cal.get(Calendar.DAY_OF_YEAR);
    }

}
