package com.example.myhealthlife.data.local.repository;

import static com.example.myhealthlife.domain.util.TimeUtils.getStart;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

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
import com.example.myhealthlife.domain.chart.ChartPoint;
import com.example.myhealthlife.domain.chart.DoubleIntPoint;
import com.example.myhealthlife.domain.chart.IntPoint;
import com.example.myhealthlife.domain.chart.ParsedFloatPoint;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ChartRepository {
    private HealthDao daoH;
    private CompDao daoC;
    private BloodDao daoB;
    private SportDao daoSp;
    private final SleepDao daoS;
    public enum TimeInterval {
        TODAY_REPEAT,
        TODAY_WITH_0,
        LAST_7_DAYS,
        LAST_30_DAYS;
    }

    public ChartRepository(Context context) {
        //Dato + Unidad (crear clase)
        //Dato actual
        // (presion es doble)
        // flotantes hay que juntar parte entera con decimal
        //Lista Dia
        //Lista Semana
        //Lista Mensual
        //Lista de un dia especifico (mas adelante)
        //Dato más bajo (dia, semana, mes)
        //Dato más promedio (dia, semana, mes)
        //Dato más alto (dia, semana, mes)

        daoH = AppDatabase.getInstance(context).healthDao();
        daoS = AppDatabase.getInstance(context).sleepDao();
        daoC = AppDatabase.getInstance(context).compDao();
        daoSp = AppDatabase.getInstance(context).sportDao();
        daoB = AppDatabase.getInstance(context).bloodDao();
    }

    // ---- DATA CHARTS ----
    public LiveData<List<ChartPoint>> getSleepChart(TimeInterval interval) {
        return Transformations.map(
                daoS.getSleepFrom(getStart(interval)),
                list -> {

                    // 1️. Entity → ChartPoint (raw)
                    List<ChartPoint> rawPoints = new ArrayList<>();
                    for (HistorySleepEntity e : list) {
                        long durationMillis = e.endTime - e.timestamp;
                        long totalMinutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis);

                        float hours = totalMinutes / 600f;
                        rawPoints.add(
                                new ParsedFloatPoint(
                                        e.timestamp,
                                        hours
                                )
                        );
                    }

                    // 2️. Decidir estrategia según intervalo
                    return switchInterval(interval, rawPoints);
                }
        );
    }
    public LiveData<List<ChartPoint>> getHeartChart(TimeInterval interval) {
        return Transformations.map(
                daoH.getHealthFrom(getStart(interval)),
                list -> {
                    // 1️. Entity → ChartPoint (raw)
                    List<ChartPoint> rawPoints = new ArrayList<>();
                    for (HistoryHealthEntity e : list) {
                        int heart = e.heartValue;
                        rawPoints.add(
                                new IntPoint(
                                        e.timestamp,
                                        heart
                                )
                        );
                    }

                    // 2️. Decidir estrategia según intervalo
                    return switchInterval(interval, rawPoints);
                }
        );
    }
    public LiveData<List<ChartPoint>> getBloodChart(TimeInterval interval) {
        return Transformations.map(
                daoB.getBloodFrom(getStart(interval)),
                list -> {
                    // 1️. Entity → ChartPoint (raw)
                    List<ChartPoint> rawPoints = new ArrayList<>();
                    for (HistoryBloodEntity e : list) {
                        int sbp = e.sbpValue;
                        int dbp = e.dbpValue;
                        rawPoints.add(
                                new DoubleIntPoint(
                                        e.timestamp,
                                        sbp,
                                        dbp
                                )
                        );
                    }

                    // 2️. Decidir estrategia según intervalo
                    return switchIntervalDoubleInt(interval, rawPoints);
                }
        );
    }
    public LiveData<List<ChartPoint>> getSugarChart(TimeInterval interval) {
        return Transformations.map(
                daoH.getHealthFrom(getStart(interval)),
                list -> {
                    // 1️. Entity → ChartPoint (raw)
                    List<ChartPoint> rawPoints = new ArrayList<>();
                    for (HistoryHealthEntity e : list) {
                        int sugar = e.bloodSugarValue;
                        rawPoints.add(
                                new IntPoint(
                                        e.timestamp,
                                        sugar
                                )
                        );
                    }

                    // 2️. Decidir estrategia según intervalo
                    return switchInterval(interval, rawPoints);
                }
        );
    }
    public LiveData<List<ChartPoint>> getTemperatureChart(TimeInterval interval) {
        return Transformations.map(
                daoH.getHealthFrom(getStart(interval)),
                list -> {
                    // 1️. Entity → ChartPoint (raw)
                    List<ChartPoint> rawPoints = new ArrayList<>();
                    for (HistoryHealthEntity e : list) {
                        int tmpInt = e.tempIntValue;
                        int tmpFloat = e.tempFloatValue;
                        rawPoints.add(
                                new ParsedFloatPoint(
                                        e.timestamp,
                                        tmpInt,
                                        tmpFloat
                                )
                        );
                    }

                    // 2️. Decidir estrategia según intervalo
                    return switchInterval(interval, rawPoints);
                }
        );
    }
    public LiveData<List<ChartPoint>> getTrigChart(TimeInterval interval) {
        return Transformations.map(
                daoC.getCompFrom(getStart(interval)),
                list -> {
                    // 1️. Entity → ChartPoint (raw)
                    List<ChartPoint> rawPoints = new ArrayList<>();
                    for (HistoryCompEntity e : list) {
                        int tmpInt = e.triCholInt;
                        int tmpFloat = e.triCholFloat;
                        rawPoints.add(
                                new ParsedFloatPoint(
                                        e.timestamp,
                                        tmpInt,
                                        tmpFloat
                                )
                        );
                    }

                    // 2️. Decidir estrategia según intervalo
                    return switchInterval(interval, rawPoints);
                }
        );
    }
    public LiveData<List<ChartPoint>> getAcidChart(TimeInterval interval) {
        return Transformations.map(
                daoC.getCompFrom(getStart(interval)),
                list -> {
                    // 1️. Entity → ChartPoint (raw)
                    List<ChartPoint> rawPoints = new ArrayList<>();
                    for (HistoryCompEntity e : list) {
                        int uricAcid = e.uricAcid;
                        rawPoints.add(
                                new IntPoint(
                                        e.timestamp,
                                        uricAcid
                                )
                        );
                    }

                    // 2️. Decidir estrategia según intervalo
                    return switchInterval(interval, rawPoints);
                }
        );
    }

    private List<ChartPoint> switchInterval(TimeInterval interval, List<ChartPoint> rawPoints) {
        switch (interval) {

            case TODAY_REPEAT:
                return expandTodayRepeatLast(rawPoints);
            case TODAY_WITH_0:
                return expandTodayWithZeros(rawPoints);
            case LAST_7_DAYS:
                return averagePointsByDay(rawPoints, 7);
            case LAST_30_DAYS:
                return averagePointsByDay(rawPoints, 30);

            default:
                return rawPoints;
        }
    }
    private List<ChartPoint> averagePointsByDay(
            List<? extends ChartPoint> points,
            int days
    ) {

        Map<Long, List<Float>> perDay = new LinkedHashMap<>();
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        long todayStart = cal.getTimeInMillis();

        // Inicializar días (huecos incluidos)
        for (int i = days - 1; i >= 0; i--) {
            cal.setTimeInMillis(todayStart);
            cal.add(Calendar.DAY_OF_YEAR, -i);
            perDay.put(cal.getTimeInMillis(), new ArrayList<>());
        }

        // Agrupar valores reales
        for (ChartPoint p : points) {
            cal.setTimeInMillis(p.getX());
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            long dayKey = cal.getTimeInMillis();

            if (perDay.containsKey(dayKey)) {
                perDay.get(dayKey).add(extractY(p));
            }
        }

        // Construir resultado
        List<ChartPoint> result = new ArrayList<>();

        for (Map.Entry<Long, List<Float>> e : perDay.entrySet()) {
            List<Float> values = e.getValue();
            float avg = 0f;

            if (!values.isEmpty()) {
                float sum = 0f;
                for (Float v : values) sum += v;
                avg = sum / values.size();
            }

            result.add(new ParsedFloatPoint(e.getKey(), avg));
        }

        return result;
    }
    public static float extractY(ChartPoint p) {
        if (p instanceof IntPoint) {
            return ((IntPoint) p).y;
        } else if (p instanceof ParsedFloatPoint) {
            return ((ParsedFloatPoint) p).y;
        } else if (p instanceof DoubleIntPoint) {
            return ((DoubleIntPoint) p).y1; // o promedio
        }
        return 0f;
    }
    private static List<ChartPoint> expandTodayRepeatLast(
            List<? extends ChartPoint> rawPoints
    ) {

        Map<Long, Float> minuteMap = new HashMap<>();
        Calendar cal = Calendar.getInstance();

        // Convertir puntos reales a mapa por minuto
        for (ChartPoint p : rawPoints) {
            cal.setTimeInMillis(p.getX());
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            minuteMap.put(cal.getTimeInMillis(), extractY(p));
        }

        // Inicio del día
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long startDay = cal.getTimeInMillis();

        List<ChartPoint> result = new ArrayList<>();

        float lastValue = 0f;

        // 1440 minutos del día
        for (int i = 0; i < 1440; i++) {
            long minuteTs = startDay + i * 60_000L;

            if (minuteMap.containsKey(minuteTs)) {
                lastValue = minuteMap.get(minuteTs);
            }
            if (!minuteMap.containsKey(minuteTs) && minuteTs > System.currentTimeMillis()) {
                lastValue = 0f;
            }

            result.add(new ParsedFloatPoint(minuteTs, lastValue));
        }

        return result;
    }
    public static List<ChartPoint> expandTodayWithZeros(
            List<? extends ChartPoint> rawPoints
    ) {

        Map<Long, Float> minuteMap = new HashMap<>();
        Calendar cal = Calendar.getInstance();

        for (ChartPoint p : rawPoints) {
            cal.setTimeInMillis(p.getX());
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            minuteMap.put(cal.getTimeInMillis(), extractY(p));
        }

        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long startDay = cal.getTimeInMillis();

        List<ChartPoint> result = new ArrayList<>();

        for (int i = 0; i < 1440; i++) {
            long minuteTs = startDay + i * 60_000L;
            float value = minuteMap.getOrDefault(minuteTs, 0f);
            result.add(new ParsedFloatPoint(minuteTs, value));
        }

        return result;
    }

    private List<ChartPoint> switchIntervalDoubleInt(
            TimeInterval interval,
            List<ChartPoint> rawPoints
    ) {

        List<DoubleIntPoint> doublePoints = new ArrayList<>();
        for (ChartPoint p : rawPoints) {
            if (p instanceof DoubleIntPoint) {
                doublePoints.add((DoubleIntPoint) p);
            }
        }

        switch (interval) {
            case TODAY_REPEAT: //en realidad no es repeat, pero aqui es por practicidad del selector
                return expandTodaySmartDouble(doublePoints);
            case LAST_7_DAYS:
                return averageDoubleIntPointsByDay(doublePoints, 7);
            case LAST_30_DAYS:
                return averageDoubleIntPointsByDay(doublePoints, 30);
            default:
                return rawPoints;
        }
    }

    private List<ChartPoint> averageDoubleIntPointsByDay(
            List<DoubleIntPoint> points,
            int days
    ) {

        Map<Long, List<DoubleIntPoint>> perDay = new LinkedHashMap<>();
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        long todayStart = cal.getTimeInMillis();

        for (int i = days - 1; i >= 0; i--) {
            cal.setTimeInMillis(todayStart);
            cal.add(Calendar.DAY_OF_YEAR, -i);
            perDay.put(cal.getTimeInMillis(), new ArrayList<>());
        }

        for (DoubleIntPoint p : points) {
            cal.setTimeInMillis(p.getX());
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            long dayKey = cal.getTimeInMillis();
            if (perDay.containsKey(dayKey)) {
                perDay.get(dayKey).add(p);
            }
        }

        List<ChartPoint> result = new ArrayList<>();

        for (Map.Entry<Long, List<DoubleIntPoint>> e : perDay.entrySet()) {

            int sbpSum = 0;
            int dbpSum = 0;
            int count = e.getValue().size();

            if (count > 0) {
                for (DoubleIntPoint p : e.getValue()) {
                    sbpSum += p.y1;
                    dbpSum += p.y2;
                }
                result.add(new DoubleIntPoint(
                        e.getKey(),
                        sbpSum / count,
                        dbpSum / count
                ));
            } else {
                result.add(new DoubleIntPoint(e.getKey(), 0, 0));
            }
        }

        return result;
    }

    public static List<ChartPoint> expandTodayWithZerosDouble(
            List<DoubleIntPoint> rawPoints
    ) {

        Map<Long, DoubleIntPoint> minuteMap = new HashMap<>();
        Calendar cal = Calendar.getInstance();

        // Mapear puntos reales por minuto
        for (DoubleIntPoint p : rawPoints) {
            cal.setTimeInMillis(p.getX());
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            minuteMap.put(
                    cal.getTimeInMillis(),
                    new DoubleIntPoint(
                            cal.getTimeInMillis(),
                            p.y1,
                            p.y2
                    )
            );
        }

        // Inicio del día
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long startDay = cal.getTimeInMillis();

        List<ChartPoint> result = new ArrayList<>();

        // 1440 minutos del día
        for (int i = 0; i < 1440; i++) {
            long minuteTs = startDay + i * 60_000L;

            DoubleIntPoint point = minuteMap.get(minuteTs);

            if (point != null) {
                result.add(point);
            } else {
                result.add(new DoubleIntPoint(minuteTs, 0, 0));
            }
        }

        return result;
    }
    public static List<ChartPoint> expandTodayWithoutZerosDouble(
            List<DoubleIntPoint> rawPoints
    ) {

        Map<Long, DoubleIntPoint> minuteMap = new HashMap<>();
        Calendar cal = Calendar.getInstance();

        // 🔹 Mapear solo puntos válidos (≠ 0)
        for (DoubleIntPoint p : rawPoints) {

            if (p.y1 == 0 && p.y2 == 0) continue;

            cal.setTimeInMillis(p.getX());
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            minuteMap.put(
                    cal.getTimeInMillis(),
                    new DoubleIntPoint(
                            cal.getTimeInMillis(),
                            p.y1,
                            p.y2
                    )
            );
        }

        // 🔹 Inicio del día
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long startDay = cal.getTimeInMillis();

        List<ChartPoint> result = new ArrayList<>();

        // 🔹 Recorre el día pero solo agrega si hay dato real
        for (int i = 0; i < 1440; i++) {
            long minuteTs = startDay + i * 60_000L;

            DoubleIntPoint point = minuteMap.get(minuteTs);

            if (point != null) {
                result.add(point);
            }
        }

        return result;
    }

    public static List<ChartPoint> expandTodaySmartDouble(
            List<DoubleIntPoint> rawPoints
    ) {

        // Intervalo base: 15 minutos
        final long BASE_INTERVAL = 30 * 60_000L;

        // Agrupar puntos reales por timestamp
        Map<Long, List<DoubleIntPoint>> realPointsMap = new HashMap<>();

        for (DoubleIntPoint p : rawPoints) {
            realPointsMap
                    .computeIfAbsent(p.getX(), k -> new ArrayList<>())
                    .add(p);
        }

        Calendar cal = Calendar.getInstance();

        // Inicio del día
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        long startDay = cal.getTimeInMillis();
        long endDay = startDay + 24 * 60 * 60_000L;

        List<ChartPoint> result = new ArrayList<>();

        long cursor = startDay;

        while (cursor < endDay) {

            boolean addedRealData = false;

            // Buscar puntos reales dentro del bloque
            for (DoubleIntPoint p : rawPoints) {
                if (p.getX() >= cursor && p.getX() < cursor + BASE_INTERVAL) {
                    result.add(p);
                    addedRealData = true;
                }
            }

            // Si no hubo datos reales, agregar un punto vacío representativo
            if (!addedRealData) {
                result.add(new DoubleIntPoint(cursor, 0, 0));
            }

            cursor += BASE_INTERVAL;
        }

        return result;
    }




}
