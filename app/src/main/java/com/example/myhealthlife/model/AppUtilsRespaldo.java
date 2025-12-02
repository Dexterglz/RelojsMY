/*
package com.example.myhealthlife.model;

import static androidx.core.content.ContextCompat.getColor;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myhealthlife.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AppUtilsRespaldo {
    */
/** Mostrar Datos en Tabla **//*

    public static void filterShowTable(Activity activity, TableLayout tableHistory, ArrayList<HistoryData> listaRecuperada, Spinner spinnerFiltro,String titulo, String unidad, Integer colorG, Integer idG, String paremeter){
        tableHistory.removeAllViews();
        listaRecuperada = PrefsHelper.obtenerHistorial(activity);
        ArrayList<HistoryData> finalListaRecuperada = listaRecuperada;
        */
/*spinnerFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String filtro = parent.getItemAtPosition(position).toString().toLowerCase();*//*

                ArrayList<HistoryData> listaFiltrada = filtrarPorPeriodo(finalListaRecuperada, "hoy");

                Collections.sort(listaFiltrada, new Comparator<HistoryData>() {
                    @Override
                    public int compare(HistoryData h1, HistoryData h2) {
                        return Long.compare(h2.getTimestamp(), h1.getTimestamp());
                    }
                });

                mostrarTabla(activity,listaFiltrada,tableHistory,paremeter);
                AppUtilsRespaldo.graficarDatosMedicos(activity,titulo,unidad,1,colorG, idG,"hoy",paremeter);
            //}
            */
/*@Override
            public void onNothingSelected(AdapterView<?> parent) {}*//*

        //});
        */
/*//*
/ Mostrar por defecto el primer filtro (Día)
        spinnerFiltro.setSelection(0);*//*

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

    */
/** Datos Grafica **//*

    public static ArrayList<DataPoint> prepararDatosGrafico(
            ArrayList<HistoryData> lista, String parametro, String intervalo) {

        ArrayList<DataPoint> resultado = new ArrayList<>();
        Calendar ahora = Calendar.getInstance();

        switch (intervalo.toLowerCase()) {

            case "hoy": {
                // Mapa de horas (solo las que tienen datos)
                Map<Integer, ArrayList<Float>> horasConDatos = new LinkedHashMap<>();

                // Recorrer los registros y agrupar por hora (solo si hay datos)
                for (HistoryData r : lista) {
                    Calendar fecha = Calendar.getInstance();
                    fecha.setTimeInMillis(r.timestamp);

                    boolean mismoDia = ahora.get(Calendar.YEAR) == fecha.get(Calendar.YEAR) &&
                            ahora.get(Calendar.DAY_OF_YEAR) == fecha.get(Calendar.DAY_OF_YEAR);

                    if (mismoDia) {
                        int hora = fecha.get(Calendar.HOUR_OF_DAY);
                        // Si no existe la hora en el mapa, la añade
                        if (!horasConDatos.containsKey(hora)) {
                            horasConDatos.put(hora, new ArrayList<>());
                        }
                        horasConDatos.get(hora).add(obtenerValorParametro(r, parametro));
                    }
                }

                // Ordenar las horas (opcional, si quieres mantener orden cronológico)
                List<Integer> horasOrdenadas = new ArrayList<>(horasConDatos.keySet());
                Collections.sort(horasOrdenadas);

                // Llenar resultado solo con horas que tienen datos
                for (int h : horasOrdenadas) {
                    ArrayList<Float> valores = horasConDatos.get(h);
                    float promedio = promedio(valores); // Ya sabemos que hay datos
                    String label = String.format("%02d:00", h);
                    resultado.add(new DataPoint(promedio, label));
                }
                break;
            }

            case "esta semana": {
                // Promedio por hoy
                Map<String, ArrayList<Float>> dias = new LinkedHashMap<>();

                for (HistoryData r : lista) {
                    Calendar fecha = Calendar.getInstance();
                    fecha.setTimeInMillis(r.timestamp);

                    boolean mismaSemana = ahora.get(Calendar.YEAR) == fecha.get(Calendar.YEAR) &&
                            ahora.get(Calendar.WEEK_OF_YEAR) == fecha.get(Calendar.WEEK_OF_YEAR);
                    if (mismaSemana) {
                        String diaStr = new SimpleDateFormat("EEE", Locale.getDefault()).format(fecha.getTime());
                        dias.putIfAbsent(diaStr, new ArrayList<>());
                        dias.get(diaStr).add(obtenerValorParametro(r, parametro));
                    }
                }

                for (Map.Entry<String, ArrayList<Float>> entry : dias.entrySet()) {
                    float promedio = promedio(entry.getValue());
                    resultado.add(new DataPoint(promedio, entry.getKey()));
                }
                break;
            }

            case "mes": {
                // Datos relevantes del mes: min, max y promedio
                ArrayList<Float> valoresMes = new ArrayList<>();
                for (HistoryData r : lista) {
                    Calendar fecha = Calendar.getInstance();
                    fecha.setTimeInMillis(r.timestamp);

                    boolean mismoMes = ahora.get(Calendar.YEAR) == fecha.get(Calendar.YEAR) &&
                            ahora.get(Calendar.MONTH) == fecha.get(Calendar.MONTH);
                    if (mismoMes) {
                        valoresMes.add(obtenerValorParametro(r, parametro));
                    }
                }

                if (!valoresMes.isEmpty()) {
                    float min = Collections.min(valoresMes);
                    float max = Collections.max(valoresMes);
                    float prom = promedio(valoresMes);

                    resultado.add(new DataPoint(min, "Mínimo"));
                    resultado.add(new DataPoint(max, "Máximo"));
                    resultado.add(new DataPoint(prom, "Promedio"));
                }
                break;
            }
        }

        return resultado;
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
            case "tempIntValue": return r.tempIntValue;
            case "tempFloatValue": return r.tempFloatValue;
            default: return 0;
        }
    }
    private static float promedio(ArrayList<Float> lista) {
        float sum = 0;
        for (float v : lista) sum += v;
        return lista.isEmpty() ? 0 : sum / lista.size();
    }
    */
/** Grafica **//*

    public static void graficarDatosMedicos(Activity activity, String titulo, String unidad, Integer modificadorEjeX, int graphColor, int graphID, String intervalo, String parametro) {
        // Obtener los últimos x puntos de datos con sus timestamps
        ArrayList<HistoryData> listaRecuperada = PrefsHelper.obtenerHistorial(activity);
        ArrayList<DataPoint> dataPoints = prepararDatosGrafico(listaRecuperada, parametro, intervalo);
        LineChart chart = activity.findViewById(graphID);

        if (dataPoints.isEmpty()) {
            Toast.makeText(activity, activity.getString(R.string.no_hay_datos) + titulo, Toast.LENGTH_SHORT).show();
            chart.clear();
            return;
        }
        // Preparar las entradas para el gráfico
        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        int colorResId = graphColor; // Esto es R.color.graph_pastel_pink (solo el ID)
        int colorHex = getColor(activity, colorResId); // Obtiene el valor hexadecimal real

        for (int i = 0; i < dataPoints.size(); i++) {
            DataPoint point = dataPoints.get(i);
            entries.add(new Entry(i, point.getValue()));

            // Formatear el timestamp para mostrarlo en el eje X
            String text = point.getTimestamp();
            String formattedTime = formatTimestamp(text);
            labels.add(formattedTime);
        }
        LineDataSet dataSet = new LineDataSet(entries, titulo + " (" + unidad + ")");


        dataSet.setColor(Color.parseColor("#FF6B6B"));
        dataSet.setLineWidth(2.5f);
        dataSet.setDrawFilled(true);
        //dataSet.setFillDrawable(ContextCompat.getDrawable(activity.getApplicationContext(), R.drawable.gradient_fill));
        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[] {Color.parseColor("#FF6B6B"), Color.parseColor("#4ECDC4")}
        );
        dataSet.setFillDrawable(gradient);

        //dataSet.setColor(colorHex);
        dataSet.setValueTextSize(10f);
        dataSet.setLineWidth(2.5f);

        dataSet.setCircleRadius(0f);
        dataSet.setCircleColor(colorHex);
        dataSet.setCircleHoleColor(Color.WHITE);

        dataSet.setValueTextColor(Color.DKGRAY);

        dataSet.setDrawValues(true);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        //dataSet.setCubicIntensity(f);
        //dataSet.setDrawFilled(true);
        //dataSet.setFillColor(colorHex);
        //dataSet.setFillAlpha(50);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        // Configuración de la cuadrícula
        chart.setDrawGridBackground(false);
        chart.setGridBackgroundColor(Color.WHITE);

        // Configurar el eje X con los timestamps
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // Cada 4 horas (ajusta según tu periodicidad)
        //xAxis.setLabelCount(12, true); // Fuerza 6 etiquetas
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setLabelRotationAngle(-45);
        //xAxis.setLabelCount(labels.size());
        xAxis.setTextSize(5f);

        //sets the visible number in the chart at first view to 5
        chart.setVisibleXRangeMaximum(5);
        switch (intervalo) {
            case "hoy":
                chart.setVisibleXRangeMinimum(5);
                break;
            case "esta semana":
                chart.setVisibleXRangeMinimum(7);
            case "mes":
                chart.setVisibleXRangeMinimum(2);
        }
        // enables drag to left/right
        chart.setDragEnabled(true);
        // do not forget to invalidate()
        chart.invalidate();

        // Configurar el gráfico
        chart.getDescription().setText(activity.getString(R.string.ultimos) + dataPoints.size() + activity.getString(R.string.registros_de) + titulo);
        chart.getDescription().setTextSize(10f);
        chart.getDescription().setTextColor(Color.DKGRAY);
        chart.getLegend().setTextSize(12f);
        chart.getLegend().setTextColor(Color.DKGRAY);
        chart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        chart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        chart.getLegend().setOrientation(Legend.LegendOrientation.HORIZONTAL);
        chart.setExtraOffsets(10f, 10f, 10f, 10f); // Márgenes
        chart.setDrawBorders(true);
        chart.setBorderColor(Color.GRAY);
        chart.setBorderWidth(1f);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setPinchZoom(true);

        // Configurar eje Y izquierdo
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMinimum(getMinValue(dataPoints, modificadorEjeX));
        leftAxis.setAxisMaximum(getMaxValue(dataPoints, modificadorEjeX));
        leftAxis.setTextSize(10f);
        leftAxis.setTextColor(Color.DKGRAY);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.LTGRAY);
        leftAxis.setGridLineWidth(0.75f);
        leftAxis.setAxisLineColor(Color.GRAY);
        leftAxis.setAxisLineWidth(1f);
        leftAxis.setGranularity(1f); // Ajusta según tus datos
        leftAxis.setLabelCount(6, true); // Número de etiquetas en el eje Y

        // Configurar eje Y derecho
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false); // Deshabilitar eje derecho

        chart.getLegend().setEnabled(true);
        chart.animateY(1000);
        chart.invalidate();
    }
    // Función para formatear el timestamp
    private static String formatTimestamp(String timestamp) {
        try {
            // Asumiendo que el timestamp es en milisegundos
            long timeMillis = Long.parseLong(timestamp);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM HH:mm", Locale.getDefault());
            return sdf.format(new Date(timeMillis));
        } catch (NumberFormatException e) {
            return timestamp; // Si no se puede parsear, devolver el original
        }
    }
    // Funciones para establecer rangos adecuados según el parámetro
    private static float getMinValue(List<DataPoint> dataPoints, int modifier) {
        if (dataPoints == null || dataPoints.isEmpty()) {
            return Float.NaN; // o lanzar una excepción o retornar 0
        }

        float min = Float.MAX_VALUE;
        for (DataPoint point : dataPoints) {
            if (point.getValue() < min) {
                min = point.getValue();
            }
        }
        return min-modifier;
    }
    private static float getMaxValue(List<DataPoint> dataPoints, int modifier) {
        if (dataPoints == null || dataPoints.isEmpty()) {
            return Float.NaN; // o lanzar una excepción o retornar 0
        }

        float max = Float.MIN_VALUE;
        for (DataPoint point : dataPoints) {
            if (point.getValue() > max) {
                max = point.getValue();
            }
        }
        return max+modifier;
    }

}
*/
