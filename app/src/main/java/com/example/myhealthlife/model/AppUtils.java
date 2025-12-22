package com.example.myhealthlife.model;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static androidx.core.content.ContextCompat.getColor;
import static androidx.core.content.ContextCompat.getString;

import static com.example.myhealthlife.model.DeviceAdapter.setDeviceImage;
import static com.yucheng.ycbtsdk.YCBTClient.getBindDeviceName;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.airbnb.lottie.L;
import com.example.myhealthlife.R;
import com.example.myhealthlife.activities.OxygenLogActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class AppUtils {



    public static void topBar(String title, Activity activity){
        //Barra de navegación superior
        TextView titleTextView = activity.findViewById(R.id.TITLE); // Titulo
        titleTextView.setText(title);
        ImageView btnBack = activity.findViewById(R.id.backTop);
        btnBack.setImageResource(R.drawable.keyboard_arrow_left);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
    }

    //-------------------------- GRAFICAS -----------------------

    /** Mostrar Datos en Tabla **/
    public static void filterShowTable(Activity activity, TableLayout tableHistory, ArrayList<HistoryData> listaRecuperada, Spinner spinnerFiltro,String titulo, String unidad, Integer colorG, Integer idG, String paremeter){
        tableHistory.removeAllViews();
        listaRecuperada = PrefsHelper.obtenerHistorial(activity);
        ArrayList<HistoryData> finalListaRecuperada = listaRecuperada;
        /*spinnerFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String filtro = parent.getItemAtPosition(position).toString().toLowerCase();*/
                ArrayList<HistoryData> listaFiltrada = filtrarPorPeriodo(finalListaRecuperada, "hoy");

                Collections.sort(listaFiltrada, new Comparator<HistoryData>() {
                    @Override
                    public int compare(HistoryData h1, HistoryData h2) {
                        return Long.compare(h2.getTimestamp(), h1.getTimestamp());
                    }
                });

                mostrarTabla(activity,listaFiltrada,tableHistory,paremeter);
                //AppUtils.graficarDatosMedicos(activity,titulo,unidad,1,colorG, idG,"hoy",paremeter);
            //}
            /*@Override
            public void onNothingSelected(AdapterView<?> parent) {}*/
        //});
        /*// Mostrar por defecto el primer filtro (Día)
        spinnerFiltro.setSelection(0);*/
    }
    public static TextView createCell(Activity activity, String text) {
        TextView tv = new TextView(activity);
        tv.setText(text);
        tv.setPadding(8, 8, 8, 8);
        return tv;
    }
    public static ArrayList<HistoryData> filtrarPorPeriodo(ArrayList<HistoryData> lista, String periodo) {
        ArrayList<HistoryData> filtrada = new ArrayList<>();
        Calendar ahora = Calendar.getInstance();

        for (HistoryData r : lista) {
            Calendar fechaRegistro = Calendar.getInstance();
            fechaRegistro.setTimeInMillis(r.timestamp);

            switch (periodo) {
                case "hoy": {
                    boolean mismoDia = ahora.get(Calendar.YEAR) == fechaRegistro.get(Calendar.YEAR) &&
                            ahora.get(Calendar.DAY_OF_YEAR) == fechaRegistro.get(Calendar.DAY_OF_YEAR);
                    if (mismoDia) filtrada.add(r);
                    break;
                }
                case "esta semana": {
                    // Crear una fecha límite de 7 días atrás
                    Calendar fechaLimite = (Calendar) ahora.clone();
                    fechaLimite.add(Calendar.DAY_OF_YEAR, -7);

                    // Verificar si la fecha de registro está dentro de los últimos 7 días
                    boolean ultimos7Dias = !fechaRegistro.before(fechaLimite) &&
                            !fechaRegistro.after(ahora);
                    if (ultimos7Dias) filtrada.add(r);
                    break;
                }
                case "este mes": {
                    // Crear una fecha límite de 30 días atrás
                    Calendar fechaLimite = (Calendar) ahora.clone();
                    fechaLimite.add(Calendar.DAY_OF_YEAR, -30);

                    // Verificar si la fecha de registro está dentro de los últimos 30 días
                    boolean ultimos30Dias = !fechaRegistro.before(fechaLimite) &&
                            !fechaRegistro.after(ahora);
                    if (ultimos30Dias) filtrada.add(r);
                    break;
                }
            }
        }
        return filtrada;
    }
    private static void mostrarTabla(Activity activity, ArrayList<HistoryData> datos, TableLayout tableHistory,String param) {
        tableHistory.removeAllViews();

        for (HistoryData r : datos) {
            Long date = r.timestamp;
            String dateStr = new SimpleDateFormat("MM/dd/yy", Locale.getDefault()).format(date);
            String timeStr = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date);
            
            Integer valor = 0;
            switch (param) {
                case "heartValue":
                    valor = r.heartValue;
                    break;
                case "hrvValue":
                    valor = r.hrvValue;
                    break;
                case "cvrrValue":
                    valor = r.cvrrValue;
                    break;
                case "oxygenValue":
                    valor = r.oxygenValue;
                    break;
                case "stepValue":
                    valor = r.stepValue;
                    break;
                case "diastolicValue":
                    valor = r.diastolicValue;
                    break;
                case "systolicValue":
                    valor = r.systolicValue;
                    break;
                case "respRateValue":
                    valor = r.respRateValue;
                    break;
                case "bodyFatValue":
                    valor = r.bodyFatValue;
                    break;
                case "bodyFatFracValue":
                    valor = r.bodyFatFracValue;
                    break;
                case "bloodSugarValue":
                    valor = r.bloodSugarValue;
                    break;
                case "tempIntValue":
                    valor = r.tempIntValue;
                    break;
                case "tempFloatValue":
                    valor = r.tempFloatValue;
                    break;
                default:
                    System.out.println("Parámetro desconocido: " + param);
                    break;
            }

            TableRow row = new TableRow(activity);
            row.addView(createCell(activity,dateStr));
            row.addView(createCell(activity,timeStr));
            row.addView(createCell(activity,String.valueOf(valor)));
            tableHistory.addView(row);
        }
    }

    /** Datos Grafica **/
    public static ArrayList<DataPoint> prepararDatosGrafico( ArrayList<HistoryData> lista,
                                                             String parametro,
                                                             String intervalo)
    {
        //Datapoints a devolver
        ArrayList<DataPoint> dataPoints = new ArrayList<>();
        Calendar ahora = Calendar.getInstance();

        switch (intervalo.toLowerCase()) {
            //Evaluar la temporalidad de la que se van a extraer los datos
            case "hoy": {
                // Crear mapa con la temporalidad
                Map<Long, Float> dia = new LinkedHashMap<>();

                // Recorrer los registros
                for (HistoryData r : lista) {
                    Calendar fecha = Calendar.getInstance();
                    fecha.setTimeInMillis(r.timestamp);
                    boolean mismoDia = ahora.get(Calendar.YEAR) == fecha.get(Calendar.YEAR) &&
                            ahora.get(Calendar.DAY_OF_YEAR) == fecha.get(Calendar.DAY_OF_YEAR);
                    //Agregar
                    if (mismoDia) {
                        Long timestamp = r.timestamp;
                        Float valor = obtenerValorParametro(r, parametro);
                        dia.put(timestamp, valor);
                    }
                }

                // Ordenar las horas (opcional, si quieres mantener orden cronológico)
                List<Long> diaOrdenado = new ArrayList<>(dia.keySet());
                Collections.sort(diaOrdenado);

                // Llenar resultado solo con horas que tienen datos
                for (Long timestampDay : diaOrdenado) {
                    Number timestampObj = dia.get(timestampDay);
                    if(timestampObj != null){
                        float valores = timestampObj.floatValue();;
                        String label = timeToString(timestampDay, "HH:mm");
                        dataPoints.add(new DataPoint(valores, label));
                    }
                }
                break;
            }
            case "mes": {
                // Mapa auxiliar: clave = día (año-mes-día), valor = lista de valores de ese día
                Map<String, List<Float>> valoresPorDia = new LinkedHashMap<>();

                // Recorrer registros y agrupar por día
                for (HistoryData r : lista) {
                    Calendar fecha = Calendar.getInstance();
                    fecha.setTimeInMillis(r.timestamp);

                    boolean mismoMes = ahora.get(Calendar.YEAR) == fecha.get(Calendar.YEAR)
                            && ahora.get(Calendar.MONTH) == fecha.get(Calendar.MONTH);

                    if (mismoMes) {
                        Float valor = obtenerValorParametro(r, parametro);

                        // Clave del día (formato yyyy-MM-dd)
                        String claveDia = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                                fecha.get(Calendar.YEAR),
                                fecha.get(Calendar.MONTH) + 1,
                                fecha.get(Calendar.DAY_OF_MONTH));

                        valoresPorDia.putIfAbsent(claveDia, new ArrayList<>());
                        valoresPorDia.get(claveDia).add(valor);
                    }
                }

                // Mapa final con promedio de cada día (clave = timestamp representativo)
                Map<Long, Float> promedioPorDia = new LinkedHashMap<>();

                for (Map.Entry<String, List<Float>> entry : valoresPorDia.entrySet()) {
                    String claveDia = entry.getKey();
                    List<Float> valores = entry.getValue();

                    // Calcular promedio
                    float suma = 0f;
                    for (Float v : valores) suma += v;
                    float promedio = valores.isEmpty() ? 0f : suma / valores.size();

                    // Convertir la fecha "yyyy-MM-dd" a timestamp
                    Calendar fecha = Calendar.getInstance();
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        Date d = sdf.parse(claveDia);
                        fecha.setTime(d);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    long timestampDia = fecha.getTimeInMillis();
                    promedioPorDia.put(timestampDia, promedio);
                }

                // Ordenar por timestamp (de menor a mayor)
                List<Long> diasOrdenados = new ArrayList<>(promedioPorDia.keySet());
                Collections.sort(diasOrdenados);

                // Llenar lista de DataPoints (para graficar)
                for (Long timestampDay : diasOrdenados) {
                    float valorPromedio = promedioPorDia.get(timestampDay);
                    String label = timeToString(timestampDay, "dd/MM");
                    dataPoints.add(new DataPoint(valorPromedio, label));
                }

                break;
            }
            case "ultimos7dias": {
                // Fecha "hoy" normalizada (inicio del día)
                Calendar hoyStart = Calendar.getInstance();
                hoyStart.set(Calendar.HOUR_OF_DAY, 0);
                hoyStart.set(Calendar.MINUTE, 0);
                hoyStart.set(Calendar.SECOND, 0);
                hoyStart.set(Calendar.MILLISECOND, 0);

                // Fecha hace 6 días (incluye hoy -> 7 días)
                Calendar hace7diasStart = (Calendar) hoyStart.clone();
                hace7diasStart.add(Calendar.DAY_OF_YEAR, -6);

                // Mapa: key = timestamp normalizado del día (inicio del día), value = lista de valores
                Map<Long, List<Float>> valoresPorDia = new LinkedHashMap<>();

                for (HistoryData r : lista) {
                    long ts = r.timestamp;

                    // Normalizar timestamp al inicio del día correspondiente
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(ts);
                    c.set(Calendar.HOUR_OF_DAY, 0);
                    c.set(Calendar.MINUTE, 0);
                    c.set(Calendar.SECOND, 0);
                    c.set(Calendar.MILLISECOND, 0);
                    long dayMillis = c.getTimeInMillis();

                    // Incluir solo si está dentro del rango [hace7diasStart .. hoyStart]
                    if (!c.before(hace7diasStart) && !c.after(hoyStart)) {
                        Float valor = obtenerValorParametro(r, parametro);
                        if (valor == null) continue; // evita nulls si tu función puede devolver null

                        if (!valoresPorDia.containsKey(dayMillis)) {
                            valoresPorDia.put(dayMillis, new ArrayList<Float>());
                        }
                        valoresPorDia.get(dayMillis).add(valor);
                    }
                }

                // Mapa final con promedio por día
                Map<Long, Float> promedioPorDia = new LinkedHashMap<>();

                for (Map.Entry<Long, List<Float>> entry : valoresPorDia.entrySet()) {
                    long dayMillis = entry.getKey();
                    List<Float> listaValores = entry.getValue();

                    float suma = 0f;
                    for (Float v : listaValores) suma += v;
                    float promedio = listaValores.isEmpty() ? 0f : (suma / listaValores.size());

                    promedioPorDia.put(dayMillis, promedio);
                }

                // Ahora recorrer desde hace7diasStart hasta hoyStart (inclusive), en orden,
                // y construir dataPoints. Esto garantiza exactamente 7 entradas, sin duplicados.
                Calendar cursor = (Calendar) hace7diasStart.clone();
                while (!cursor.after(hoyStart)) {
                    long dayMillis = cursor.getTimeInMillis();
                    float valor = 0f;
                    boolean tieneValor = promedioPorDia.containsKey(dayMillis);

                    if (tieneValor) {
                        valor = promedioPorDia.get(dayMillis);
                        valor = Float.parseFloat(String.format("%.2f", valor));
                        if(!Objects.equals(parametro, "temp")){
                            int valorint = (int) valor;
                            valor = (float) valorint;
                        }
                    } else {
                        // Si prefieres NO añadir días sin datos, comenta las siguientes 2 líneas
                        //valor = 0f; // o usa Float.NaN/null si tu gráfico soporta saltos
                    }

                    // Etiqueta dd/MM
                    String label = new SimpleDateFormat("dd/MM", Locale.getDefault()).format(new Date(dayMillis));
                    dataPoints.add(new DataPoint(valor, label));

                    cursor.add(Calendar.DAY_OF_YEAR, 1);
                }

                break;
            }
            case "ultimos30dias": {
                // Fecha "hoy" normalizada (inicio del día)
                Calendar hoyStart = Calendar.getInstance();
                hoyStart.set(Calendar.HOUR_OF_DAY, 0);
                hoyStart.set(Calendar.MINUTE, 0);
                hoyStart.set(Calendar.SECOND, 0);
                hoyStart.set(Calendar.MILLISECOND, 0);

                // Fecha hace 29 días (incluye hoy -> 30 días)
                Calendar hace30diasStart = (Calendar) hoyStart.clone();
                hace30diasStart.add(Calendar.DAY_OF_YEAR, -29);

                // Mapa: key = timestamp normalizado del día (inicio del día), value = lista de valores
                Map<Long, List<Float>> valoresPorDia = new LinkedHashMap<>();

                for (HistoryData r : lista) {
                    long ts = r.timestamp;

                    // Normalizar timestamp al inicio del día correspondiente
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(ts);
                    c.set(Calendar.HOUR_OF_DAY, 0);
                    c.set(Calendar.MINUTE, 0);
                    c.set(Calendar.SECOND, 0);
                    c.set(Calendar.MILLISECOND, 0);
                    long dayMillis = c.getTimeInMillis();

                    // Incluir solo si está dentro del rango [hace30diasStart .. hoyStart]
                    if (!c.before(hace30diasStart) && !c.after(hoyStart)) {
                        Float valor = obtenerValorParametro(r, parametro);
                        if (valor == null) continue; // evita nulls si tu función puede devolver null

                        if (!valoresPorDia.containsKey(dayMillis)) {
                            valoresPorDia.put(dayMillis, new ArrayList<Float>());
                        }
                        valoresPorDia.get(dayMillis).add(valor);
                    }
                }

                // Mapa final con promedio por día para temperatura
                Map<Long, Float> promedioPorDia = new LinkedHashMap<>();

                for (Map.Entry<Long, List<Float>> entry : valoresPorDia.entrySet()) {
                    long dayMillis = entry.getKey();
                    List<Float> listaValores = entry.getValue();

                    float suma = 0f;
                    for (Float v : listaValores) suma += v;
                    float promedio = listaValores.isEmpty() ? 0f : (suma / listaValores.size());

                    promedioPorDia.put(dayMillis, promedio);
                }

                // Ahora recorrer desde hace30diasStart hasta hoyStart (inclusive), en orden,
                // y construir dataPoints. Esto garantiza exactamente 30 entradas, sin duplicados.
                Calendar cursor = (Calendar) hace30diasStart.clone();
                while (!cursor.after(hoyStart)) {
                    long dayMillis = cursor.getTimeInMillis();
                    float valor = 0f;
                    boolean tieneValor = promedioPorDia.containsKey(dayMillis);

                    if (tieneValor) {
                        valor = promedioPorDia.get(dayMillis);
                        valor = Float.parseFloat(String.format("%.2f", valor));
                        if(!Objects.equals(parametro, "temp")){
                            int valorint = (int) valor;
                            valor = (float) valorint;
                        }
                    } else {
                        // Si prefieres NO añadir días sin datos, comenta las siguientes 2 líneas
                        valor = 0f; // o usa Float.NaN/null si tu gráfico soporta saltos
                    }

                    // Etiqueta dd/MM
                    String label = new SimpleDateFormat("dd/MM", Locale.getDefault()).format(new Date(dayMillis));
                    dataPoints.add(new DataPoint(valor, label));

                    cursor.add(Calendar.DAY_OF_YEAR, 1);
                }

                break;
            }

        }
        // Mostrar en log
        for (DataPoint point : dataPoints) {
            Log.d("DataPoints","X: "+ point.getTimestamp() + " | Y: "+ point.getValue());
        }
        return dataPoints;
    }
    private static float obtenerValorParametro(HistoryData r, String parametro) {
        switch (parametro) {
            case "heartValue": return r.heartValue;
            case "hrvValue": return r.hrvValue;
            case "cvrrValue": return r.cvrrValue;
            case "oxygenValue": return r.oxygenValue;
            case "stepValue": return r.stepValue;
            case "diastolicValue": return r.diastolicValue;
            case "systolicValue": return r.systolicValue;
            case "respRateValue": return r.respRateValue;
            case "bodyFatValue": return r.bodyFatValue;
            case "bodyFatFracValue": return r.bodyFatFracValue;
            case "bloodSugarValue": return r.bloodSugarValue;
            case "temp":
                String numeroCompleto = r.tempIntValue + "." + r.tempFloatValue;
                return Float.parseFloat(numeroCompleto);
            default: return 0;
        }
    }
    /** Grafica **/
    public static void graficarDatosMedicos(Activity activity, int graphColor, int graphID, String intervalo, TipoDato param, String unidad) {
        String parametro = paramStr(param);

        //Obtener los datapoints
        ArrayList<HistoryData> listaRecuperada = PrefsHelper.obtenerHistorial(activity);
        ArrayList<DataPoint> dataPoints = prepararDatosGrafico(listaRecuperada, parametro, intervalo);
        LineChart chart = activity.findViewById(graphID);

        if (dataPoints.isEmpty()) {
            Toast.makeText(activity, activity.getString(R.string.no_hay_datos), Toast.LENGTH_SHORT).show();
            chart.clear();
            return;
        }

        // Preparar las entradas para el gráfico
        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        //Obtener el historial completo
        for (int i = 0; i < dataPoints.size(); i++) {
            DataPoint point = dataPoints.get(i);
            entries.add(new Entry(i, point.getValue()));
            labels.add(point.getTimestamp());
        }

        // Crear el dataset
        LineDataSet dataSet = new LineDataSet(entries, parametro);
        int primaryColor = getColor(activity, R.color.colorPrimary);

        // Estilo de línea
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setColor(primaryColor);
        dataSet.setLineWidth(0f);
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);

        // Degradado de relleno
        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{primaryColor, Color.TRANSPARENT}
        );
        dataSet.setDrawFilled(true);
        dataSet.setFillDrawable(gradient);

        // Configurar LineData
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

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

        // Configurar eje Y izquierdo
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
        CustomMarkerView marker = new CustomMarkerView(activity, R.layout.custom_marker, array,intervalo,parametro);
        marker.setChartView(chart);
        chart.setMarker(marker);
        xAxis.setSpaceMax(0.1f);

        // Refrescar gráfico
        chart.invalidate();

    }
    // Funciones para establecer rangos adecuados según el parámetro
    private static float getMinValue(Activity activity, TipoDato param, String intervalo) {
        String parametro = paramStr(param);
        //Obtener los datapoints
        ArrayList<HistoryData> listaRecuperada = PrefsHelper.obtenerHistorial(activity);
        ArrayList<DataPoint> dataPoints = prepararDatosGrafico(listaRecuperada, parametro, intervalo);

        if (dataPoints == null || dataPoints.isEmpty()) {
            return Float.NaN; // o lanzar una excepción o retornar 0
        }

        float min = Float.MAX_VALUE;
        for (DataPoint point : dataPoints) {
            if (point.getValue() < min) {
                min = point.getValue();
            }
        }
        return min;
    }
    private static float getMaxValue(Activity activity, TipoDato param, String intervalo) {
        String parametro = paramStr(param);
        //Obtener los datapoints
        ArrayList<HistoryData> listaRecuperada = PrefsHelper.obtenerHistorial(activity);
        ArrayList<DataPoint> dataPoints = prepararDatosGrafico(listaRecuperada, parametro, intervalo);

        if (dataPoints == null || dataPoints.isEmpty()) {
            return Float.NaN; // o lanzar una excepción o retornar 0
        }

        float max = Float.MIN_VALUE;
        for (DataPoint point : dataPoints) {
            if (point.getValue() > max) {
                max = point.getValue();
            }
        }
        return max;
    }
    private static float getAverage(Activity activity, TipoDato param, String intervalo) {
        String parametro = paramStr(param);
        //Obtener los datapoints
        ArrayList<HistoryData> listaRecuperada = PrefsHelper.obtenerHistorial(activity);
        ArrayList<DataPoint> lista = prepararDatosGrafico(listaRecuperada, parametro, intervalo);

        float sum = 0;
        for (DataPoint v : lista) sum += v.getValue();
        return lista.isEmpty() ? 0 : sum / lista.size();
    }
    public static void setDashboardParam(Activity activity, TipoDato param, String intervalo){
        float avg = getAverage(activity,param,intervalo);
        avg = Float.parseFloat(String.format("%.2f", avg));
        float max = getMaxValue(activity,param,intervalo);
        max = Float.parseFloat(String.format("%.2f", max));
        float min = getMinValue(activity,param,intervalo);
        min = Float.parseFloat(String.format("%.2f", min));

        TextView average = activity.findViewById(R.id.average);
        TextView highest = activity.findViewById(R.id.highest);
        TextView lowest = activity.findViewById(R.id.lowest);


        String avgTxt = String.valueOf(avg);
        String maxTxt = String.valueOf(max);
        String minTxt = String.valueOf(min);

        if(param != TipoDato.TEMP){
            avgTxt = String.valueOf((int) avg);
            maxTxt = String.valueOf((int) max);
            minTxt = String.valueOf((int) min);
        }

        average.setText(avgTxt);
        highest.setText(maxTxt);
        lowest.setText(minTxt);

    }
    private static String timeToString(long timestamp,String pattern) {
        Date date = new Date(timestamp);
        return new SimpleDateFormat(pattern, Locale.getDefault()).format(date);
    }

    public static String paramStr(TipoDato tipo) {
        String parametro = "";
        switch (tipo) {
            case HEART_VALUE:
                parametro = "heartValue";
                break;
            case HRV_VALUE:
                parametro = "hrvValue";
                break;
            case CVRR_VALUE:
                parametro = "cvrrValue";
                break;
            case OXYGEN_VALUE:
                parametro = "oxygenValue";
                break;
            case STEP_VALUE:
                parametro = "stepValue";
                break;
            case DIASTOLIC_VALUE:
                parametro = "diastolicValue";
                break;
            case SYSTOLIC_VALUE:
                parametro = "systolicValue";
                break;
            case RESP_RATE_VALUE:
                parametro = "respRateValue";
                break;
            case BODY_FAT_VALUE:
                parametro = "bodyFatValue";
                break;
            case BODY_FAT_FRAC_VALUE:
                parametro = "bodyFatFracValue";
                break;
            case BLOOD_SUGAR_VALUE:
                parametro = "bloodSugarValue";
                break;
            case TEMP:
                parametro = "temp";
                break;
        }
        return parametro;
    }

    public static void setParam(Activity activity, TipoDato param, String title, String u) {
        TabManager tabManager = new TabManager(
                getColor(activity, R.color.colorPrimary),
                getColor(activity, R.color.gray)
        );
        // Configurar tabs
        tabManager.addTab(activity.findViewById(R.id.tabDay), getString(activity, R.string.day));
        tabManager.addTab(activity.findViewById(R.id.tabWeek), getString(activity, R.string.week));
        tabManager.addTab(activity.findViewById(R.id.tabMonth), getString(activity, R.string.month));
        LinearLayout tabDay = activity.findViewById(R.id.tabDay);
        LinearLayout tabWeek = activity.findViewById(R.id.tabWeek);
        LinearLayout tabMonth = activity.findViewById(R.id.tabMonth);
        TextView titulo = activity.findViewById(R.id.TITLE);

        titulo.setText(title);
        ImageView back = activity.findViewById(R.id.backTop);
        ImageView rightIcon = activity.findViewById(R.id.connect_device);
        rightIcon.setVisibility(INVISIBLE);

        back.setImageResource(R.drawable.keyboard_arrow_left);
        back.setOnClickListener(v->{
            activity.onBackPressed();
            back.setImageResource(R.mipmap.app_icon);
        });
        
        // Activar y graficar el primero por defecto
        tabManager.setActiveTab(activity.findViewById(R.id.tabDay));
        graficarDatosMedicos(activity,0,R.id.chart_log,"hoy", param,u);
        setDashboardParam(activity,param,"hoy");
        //Establecer botones
        tabDay.setOnClickListener(v-> {
            tabManager.setActiveTab(tabDay);
            graficarDatosMedicos(activity,0,R.id.chart_log,"hoy", param,u);
            setDashboardParam(activity,param,"hoy");
        });
        tabWeek.setOnClickListener(v-> {
            tabManager.setActiveTab(tabWeek);
            graficarDatosMedicos(activity,0,R.id.chart_log,"ultimos7dias", param,u);
            setDashboardParam(activity,param,"ultimos7dias");
        });
        tabMonth.setOnClickListener(v-> {
            tabManager.setActiveTab(tabMonth);
            graficarDatosMedicos(activity,0,R.id.chart_log,"ultimos30dias", param,u);
            setDashboardParam(activity,param,"ultimos30dias");
        });
    }

}
