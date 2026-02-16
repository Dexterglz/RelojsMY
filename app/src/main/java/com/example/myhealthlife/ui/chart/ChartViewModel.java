package com.example.myhealthlife.ui.chart;

import static androidx.core.content.ContextCompat.getColor;

import static com.example.myhealthlife.repository.ChartRepository.extractY;

import android.app.Activity;
import android.app.Application;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.myhealthlife.R;
import com.example.myhealthlife.domain.chart.ChartPoint;
import com.example.myhealthlife.domain.common.CustomMarkerView;
import com.example.myhealthlife.domain.chart.DoubleIntPoint;
import com.example.myhealthlife.domain.chart.IntPoint;
import com.example.myhealthlife.domain.chart.ParsedFloatPoint;
import com.example.myhealthlife.domain.util.TimeUtils;
import com.example.myhealthlife.repository.ChartRepository;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChartViewModel extends AndroidViewModel {
    private final ChartRepository repository;
    private LiveData<List<ChartPoint>> chartData;
    private static final MutableLiveData<ChartRepository.TimeInterval> interval =
            new MutableLiveData<>();
    private final LiveData<List<ChartPoint>> sleepChart;
    private final LiveData<List<ChartPoint>> heartChart;
    private final LiveData<List<ChartPoint>> bloodChart;
    private final LiveData<List<ChartPoint>> sugarChart;
    private final LiveData<List<ChartPoint>> tempChart;
    private final LiveData<List<ChartPoint>> trigChart;
    private final LiveData<List<ChartPoint>> acidChart;
    public ChartViewModel(@NonNull Application application) {
        super(application);
        repository = new ChartRepository(application);
        sleepChart = Transformations.switchMap(interval,
                repository::getSleepChart
        );
        heartChart = Transformations.switchMap(interval,
                repository::getHeartChart
        );
        bloodChart = Transformations.switchMap(interval,
                repository::getBloodChart
        );
        sugarChart = Transformations.switchMap(interval,
                repository::getSugarChart
        );
        tempChart = Transformations.switchMap(interval,
                repository::getTemperatureChart
        );
        trigChart = Transformations.switchMap(interval,
                repository::getTrigChart
        );
        acidChart = Transformations.switchMap(interval,
                repository::getAcidChart
        );
    }
    public LiveData<List<ChartPoint>> getSleepChart() {
        return sleepChart;
    }
    public LiveData<List<ChartPoint>> getHeartChart() {
        return heartChart;
    }
    public LiveData<List<ChartPoint>> getBloodChart() {
        return bloodChart;
    }
    public LiveData<List<ChartPoint>> getSugarChart() {
        return sugarChart;
    }
    public LiveData<List<ChartPoint>> getTempetureChart() {
        return tempChart;
    }
    public LiveData<List<ChartPoint>> getTrigChart() {
        return trigChart;
    }
    public LiveData<List<ChartPoint>> getAcidChart() {
        return acidChart;
    }


    public static LiveData<ChartRepository.TimeInterval> getInterval() {
        return interval;
    }
    public static void chartHistoryHealth(
            Activity activity,
            int graphID,
            LiveData<List<ChartPoint>> source
    ) {

        LineChart chart = activity.findViewById(graphID);

        source.observe((LifecycleOwner) activity, points -> {

            if (points == null || points.isEmpty()) {
                chart.clear();
                return;
            }

            List<Entry> entries = new ArrayList<>();
            List<String> labels = new ArrayList<>();
            List<Entry> sbpEntries = new ArrayList<>();
            List<Entry> dbpEntries = new ArrayList<>();


            int index = 0;
            for (ChartPoint p : points) {

                if (p instanceof IntPoint) {
                    IntPoint ip = (IntPoint) p;
                    entries.add(new Entry(index, ip.y));

                } else if (p instanceof ParsedFloatPoint) {
                    ParsedFloatPoint fp = (ParsedFloatPoint) p;
                    entries.add(new Entry(index, fp.y));

                } else if (p instanceof DoubleIntPoint) {
                    DoubleIntPoint dp = (DoubleIntPoint) p;

                    sbpEntries.add(new Entry(index, dp.y1)); // SBP
                    dbpEntries.add(new Entry(index, dp.y2)); // DBP
                }

                labels.add(
                        TimeUtils.formatForInterval(
                                p.getX(),
                                getInterval().getValue()
                        )
                );
                index++;
            }

            LineDataSet dataSet = new LineDataSet(entries, "");
            LineData lineData = new LineData(dataSet);
            chart.setData(lineData);

            int primaryColor = getColor(activity, R.color.accent);

            // Estilo de línea
            dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            dataSet.setColor(primaryColor);
            dataSet.setLineWidth(0f);
            dataSet.setDrawCircles(false);
            dataSet.setDrawValues(false);

            // Degradado de relleno
            GradientDrawable gradient =
                    new GradientDrawable(
                            GradientDrawable.Orientation.TOP_BOTTOM,
                            new int[]{primaryColor,
                                    Color.TRANSPARENT} );
            dataSet.setDrawFilled(true);
            dataSet.setFillDrawable(gradient);

            // Configuración general
            chart.getDescription().setEnabled(false);
            chart.getLegend().setEnabled(false);
            chart.setDrawGridBackground(false);
            chart.setDrawBorders(false);
            chart.setTouchEnabled(true);
            chart.setPinchZoom(false);
            chart.setDoubleTapToZoomEnabled(false);
            chart.animateY(1000);
            chart.setExtraOffsets(8f, 8f, 8f, 8f);

            // Configurar eje X
            XAxis xAxis = chart.getXAxis();
            xAxis.setDrawGridLines(false);
            xAxis.setDrawAxisLine(false);
            xAxis.setTextColor(Color.DKGRAY);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(1f);
            //xAxis.setLabelCount(12, true);
            xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
            xAxis.setLabelRotationAngle(0);
            xAxis.setTextSize(10f);


            //Configurar eje Y izquierdo
            YAxis leftAxis = chart.getAxisLeft();
            leftAxis.setDrawGridLines(false);
            leftAxis.setDrawAxisLine(false);
            leftAxis.setTextSize(10f);
            leftAxis.setTextColor(Color.DKGRAY);
            leftAxis.setLabelCount(4, true);
            leftAxis.setGranularity(1f);
            leftAxis.setAxisMinimum(0);

            // Deshabilitar eje Y derecho
            chart.getAxisRight().setEnabled(false);

            // Crear y asignar el marker
            List<String> list = labels;
            String[] array = list.toArray(new String[0]);

            CustomMarkerView marker = new CustomMarkerView(
                    activity,
                    R.layout.custom_marker,
                    array,
                    "hoy",
                    "heartValue"
            );
            marker.setChartView(chart);
            chart.setMarker(marker);

            xAxis.setSpaceMax(0.5f);

            // Refrescar gráfico
            chart.invalidate();

        });
    }
    public static void chartDoubleIntHealthBar(
        Activity activity,
        int graphID,
        LiveData<List<ChartPoint>> source
    ) {

        BarChart chart = activity.findViewById(graphID);

        source.observe((LifecycleOwner) activity, points -> {

            if (points == null || points.isEmpty()) {
                chart.clear();
                return;
            }

            List<BarEntry> backEntries = new ArrayList<>();
            List<BarEntry> frontEntries = new ArrayList<>();
            List<String> labels = new ArrayList<>();

            int index = 0;

            for (ChartPoint p : points) {

                if (!(p instanceof DoubleIntPoint)) continue;

                DoubleIntPoint dp = (DoubleIntPoint) p;

                // ⚠️ MISMO X
                backEntries.add(new BarEntry(index, dp.y2));
                frontEntries.add(new BarEntry(index, dp.y1));

                labels.add(
                        TimeUtils.formatForInterval(
                                dp.getX(),
                                getInterval().getValue()
                        )
                );

                index++;
            }

            if (backEntries.isEmpty()) {
                chart.clear();
                return;
            }

            // ===== DATASETS =====

            // 🔹 BARRA DE FONDO
            BarDataSet backSet = new BarDataSet(backEntries, "dp");
            backSet.setColor(Color.parseColor("#D90073")); // rosa
            backSet.setDrawValues(false);

            // 🔹 BARRA FRONTAL
            BarDataSet frontSet = new BarDataSet(frontEntries, "sp");
            frontSet.setColor(Color.parseColor("#1E88E5")); // azul fuerte
            frontSet.setDrawValues(false);

            // ⚠️ ORDEN IMPORTANTE
            BarData barData = new BarData(backSet, frontSet);

            // 👉 anchos distintos
            backSet.setBarBorderWidth(0f);
            barData.setBarWidth(0.6f);   // se aplica al último set
            frontSet.setBarBorderWidth(0f);

            chart.setData(barData);

            // ===== CHART CONFIG =====
            chart.getDescription().setEnabled(false);
            chart.getLegend().setEnabled(true);
            chart.getAxisRight().setEnabled(false);
            chart.setTouchEnabled(true);
            chart.setScaleEnabled(false);
            chart.animateY(800);

            // ===== X AXIS =====
            XAxis xAxis = chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setGranularity(1f);
            xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
            xAxis.setTextSize(10f);

            xAxis.setAxisMinimum(-0.5f);
            xAxis.setAxisMaximum(labels.size() - 0.5f);

            // ===== Y AXIS =====
            YAxis leftAxis = chart.getAxisLeft();
            leftAxis.setAxisMinimum(0f);
            leftAxis.setDrawGridLines(true);

            chart.invalidate();
        });
    }



    public static LiveData<String> minFromChart(LiveData<List<ChartPoint>> source) {
        return Transformations.map(source, list -> {
            if (list == null || list.isEmpty()) return "--";

            float min = Float.MAX_VALUE;
            boolean found = false;

            for (ChartPoint p : list) {
                float v = extractY(p);
                if (v > 0) {
                    min = Math.min(min, v);
                    found = true;
                }
            }

            return found ? format(min) : "--";
        });
    }
    public static LiveData<String> maxFromChart(LiveData<List<ChartPoint>> source) {
        return Transformations.map(source, list -> {
            if (list == null || list.isEmpty()) return "--";

            float max = 0f;

            for (ChartPoint p : list) {
                float v = extractY(p);
                if (v > max) max = v;
            }

            return max > 0 ? format(max) : "--";
        });
    }
    public static LiveData<String> avgFromChart(LiveData<List<ChartPoint>> source) {
        return Transformations.map(source, list -> {
            if (list == null || list.isEmpty()) return "--";

            float sum = 0f;
            int count = 0;

            for (ChartPoint p : list) {
                float v = extractY(p);
                if (v > 0) {
                    sum += v;
                    count++;
                }
            }

            return count > 0 ? format(sum / count) : "--";
        });
    }
    private static String format(float v) {
        if (v == (int) v) {
            return String.valueOf((int) v);
        }
        return String.format(Locale.getDefault(), "%.2f", v);
    }
    public void setInterval(ChartRepository.TimeInterval i) {
        interval.setValue(i);
    }
}


