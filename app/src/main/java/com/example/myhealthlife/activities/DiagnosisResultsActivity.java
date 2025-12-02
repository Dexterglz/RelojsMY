
package com.example.myhealthlife.activities;

import static com.example.myhealthlife.model.AppUtils.filtrarPorPeriodo;
import static com.example.myhealthlife.model.AppUtils.graficarDatosMedicos;

import static com.example.myhealthlife.model.PrefsHelper.obtenerHistorial;
import static com.example.myhealthlife.model.TablaGlucemiaHelper.setGlucosaTabla;
import static com.example.myhealthlife.model.TimestampManager.agregarTimestamp;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.myhealthlife.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;

import com.example.myhealthlife.io.response.ApiClient;
import com.example.myhealthlife.io.response.ApiService;
import com.example.myhealthlife.io.response.PatientResponse;
import com.example.myhealthlife.model.AppUtils;
import com.example.myhealthlife.model.HistoryData;
import com.example.myhealthlife.model.LoginRequest;
import com.example.myhealthlife.model.LoginResponse;
import com.example.myhealthlife.model.PrefsHelper;
import com.example.myhealthlife.model.TablaGlucemiaHelper;
import com.example.myhealthlife.model.TipoDato;
import com.example.myhealthlife.model.Usuario;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import retrofit2.Call;
import retrofit2.Callback;

public class DiagnosisResultsActivity extends AppCompatActivity {
    public SharedPreferences prefs;
    Spinner spinnerFiltro;
    public int heartRate,hrv,cvrr,oxygen,diastolic,systolic,respRate,bloodSugar,tempInt,tempFloat;
    public long timestamp;

    /** Funcion Principal */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnosis_results);
        prefs = getSharedPreferences("DatosMedicos", MODE_PRIVATE);
        topBar(getString(R.string.diagnosticos_grupos));
        setResume();
        configAllGroups();
        parametersGroups();
        setGlucosaTabla(this, findViewById(android.R.id.content));
        ScrollView scrollView = findViewById(R.id.diagnosis_result);
        scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                generarPDFDesdeScrollView(scrollView);
            }
        },5000);

    }
/**Resumne**/

    private void setResume(){
        HistoryData h = obtenerHistorial(this).get(
                obtenerHistorial(this).size()-1
        );

        // Recuperar el último valor del historial
        heartRate = h.heartValue;
        hrv = h.hrvValue;
        cvrr = h.cvrrValue;
        oxygen = h.oxygenValue;
        diastolic = h.diastolicValue;
        systolic = h.systolicValue;
        respRate = h.respRateValue;
        bloodSugar = h.bloodSugarValue;
        tempInt = h.tempIntValue;
        tempFloat = h.tempFloatValue;
        timestamp = h.timestamp;
        spinnerFiltro = findViewById(R.id.spinnerFiltroResultados);

        agregarTimestamp(this,timestamp);

        //Resumen de Parámetros
        setupParameter(R.id.tvHeartRateValue, R.id.indicatorHeartRate, heartRate, MedicalRanges.HEART_RATE, " lpm");
        setupParameter(R.id.tvHrvValue, R.id.indicatorHrv, hrv, MedicalRanges.HRV, " ms");
        setupParameter(R.id.tvCvrrValue, R.id.indicatorCvrr, cvrr, MedicalRanges.CVRR, "%");
        setupParameter(R.id.tvOxygenValue, R.id.indicatorOxygen, oxygen, MedicalRanges.OXYGEN, "%");
        setupParameter(R.id.tvDiastolicValue, R.id.indicatorDiastolic, diastolic, MedicalRanges.BLOOD_PRESSURE_DIASTOLIC, " mmHg");
        setupParameter(R.id.tvSystolicValue, R.id.indicatorSystolic, systolic, MedicalRanges.BLOOD_PRESSURE_SYSTOLIC, " mmHg");
        setupParameter(R.id.tvRespRateValue, R.id.indicatorRespRate, respRate, MedicalRanges.RESP_RATE, " rpm");
        //setupParameter(R.id.tvBodyFatValue, R.id.indicatorBodyFat, bodyFat, MedicalRanges.BODY_FAT, "%");
        setupParameter(R.id.tvBloodSugarValue, R.id.indicatorBloodSugar, bloodSugar, MedicalRanges.BLOOD_SUGAR, " mg/dL");
        setupParameter(R.id.tvTempValue, R.id.indicatorTemp, bloodSugar, MedicalRanges.BLOOD_SUGAR, " mg/dL");
        // Para temperatura con decimales
        float temperature = Float.parseFloat(tempInt + "." + tempFloat);
        // Para temperatura con decimales (caso especial)
        String tempFormatted = String.format(Locale.US, "%.1f°C", temperature);
        TextView tvTemp = findViewById(R.id.tvTempValue);
        tvTemp.setText(tempFormatted);
        setupIndicator(R.id.indicatorTemp, (int) temperature, MedicalRanges.TEMP);

        //Graficas
        spinnerFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String filtro = "hoy";
                if(position == 0) { filtro = "hoy";}
                if(position == 1) { filtro = "ultimos7dias";}
                if(position == 2) { filtro = "ultimos30dias";}
                ArrayList<HistoryData> listaFiltrada = filtrarPorPeriodo(obtenerHistorial(DiagnosisResultsActivity.this), filtro);

                // Cardiovascular
                graficarDatosMedicos(DiagnosisResultsActivity.this, R.color.graph_pastel_pink, R.id.cardiovascular_frec_chart, filtro, TipoDato.HEART_VALUE,"bpm");
                graficarDatosMedicos(DiagnosisResultsActivity.this, R.color.graph_pastel_aqua, R.id.cardiovascular_frecRes_chart, filtro, TipoDato.RESP_RATE_VALUE,"rpm");
                // Circulatorio
                graficarDatosMedicos(DiagnosisResultsActivity.this, R.color.graph_mint_green, R.id.circulatorio_diastolic_chart, filtro, TipoDato.DIASTOLIC_VALUE,"mm");
                graficarDatosMedicos(DiagnosisResultsActivity.this, R.color.graph_peach, R.id.circulatorio_systolic_chart, filtro, TipoDato.SYSTOLIC_VALUE,"hg");
                graficarDatosMedicos(DiagnosisResultsActivity.this, R.color.graph_pastel_pink, R.id.circulatorio_frec_chart, filtro, TipoDato.HEART_VALUE,"bpm");
                graficarDatosMedicos(DiagnosisResultsActivity.this, R.color.graph_lavender_blue, R.id.circulatorio_ox_chart, filtro, TipoDato.OXYGEN_VALUE,"%");
                // Fragmentado
                graficarDatosMedicos(DiagnosisResultsActivity.this, R.color.graph_pastel_pink, R.id.fragmentado_frec_chart, filtro, TipoDato.HEART_VALUE,"bpm");
                graficarDatosMedicos(DiagnosisResultsActivity.this, R.color.graph_pastel_aqua, R.id.fragmentado_frecRes_chart, filtro, TipoDato.RESP_RATE_VALUE,"rpm");
                // Actividad
                graficarDatosMedicos(DiagnosisResultsActivity.this, R.color.graph_pastel_pink, R.id.actividad_frec_chart, filtro, TipoDato.HEART_VALUE,"bpm");
                graficarDatosMedicos(DiagnosisResultsActivity.this, R.color.graph_mint_green, R.id.actividad_diast_chart, filtro, TipoDato.DIASTOLIC_VALUE,"mm");
                graficarDatosMedicos(DiagnosisResultsActivity.this, R.color.graph_peach, R.id.actividad_sist_chart, filtro, TipoDato.SYSTOLIC_VALUE,"hg");
                // Infección
                graficarDatosMedicos(DiagnosisResultsActivity.this, R.color.graph_pastel_aqua, R.id.infeccion_fr_chart, filtro, TipoDato.RESP_RATE_VALUE,"rpm");
                graficarDatosMedicos(DiagnosisResultsActivity.this, R.color.graph_lavender_blue, R.id.infeccion_ox_chart, filtro, TipoDato.OXYGEN_VALUE,"%");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        // Mostrar por defecto el primer filtro (Día)
        spinnerFiltro.setSelection(0);

    }
/**Grupos**/

    private void parametersGroups(){
        List<Integer> cardiovascular = new ArrayList<>();
        List<Integer> circulatorio = new ArrayList<>();
        List<Integer> fragmentado = new ArrayList<>();
        List<Integer> actividad = new ArrayList<>();
        List<Integer> infeccion = new ArrayList<>();

        //Grupo 1: Sistema Cardivascular
        cardiovascular.add(setupParameter(R.id.cardio_fc_valor, R.id.cardio_fc_sem, heartRate, MedicalRanges.HEART_RATE, " lpm")); //Frecuencia Cardiaca
        cardiovascular.add(setupParameter(R.id.cardio_fr_valor, R.id.cardio_fr_sem, respRate, MedicalRanges.RESP_RATE, " rpm"));//Frecuencia Respiratoria
        colorGrupos(cardiovascular,R.id.cardio_group);
        //Grupo 2: Sistema Circulatorio
        circulatorio.add(setupParameter(R.id.circulatorio_diastolic_valor, R.id.circulatorio_diastolic_sem, diastolic, MedicalRanges.BLOOD_PRESSURE_DIASTOLIC, " mmHg"));//Presion Arterial Diastólica
        circulatorio.add(setupParameter(R.id.circulatorio_systolic_valor, R.id.circulatorio_systolic_sem, systolic, MedicalRanges.BLOOD_PRESSURE_SYSTOLIC, " mmHg"));//Presion Arterial Sistólica
        circulatorio.add(setupParameter(R.id.circulatorio_fc_valor, R.id.circulatorio_fc_sem, heartRate, MedicalRanges.HEART_RATE, " lpm"));//Frecuencia Cardiaca
        circulatorio.add(setupParameter(R.id.circulatorio_ox_valor, R.id.circulatorio_ox_sem, oxygen, MedicalRanges.OXYGEN, "%"));//Oxigenacion
        colorGrupos(circulatorio,R.id.circulatorio_group);
        //Grupo 3: Sueño Fragmentado
        fragmentado.add(setupParameter(R.id.fragmentado_fc_valor, R.id.fragmentado_fc_sem, heartRate, MedicalRanges.HEART_RATE, " lpm")); //Frecuencia Cardiaca
        fragmentado.add(setupParameter(R.id.fragmentado_fr_valor, R.id.fragmentado_fr_sem, respRate, MedicalRanges.RESP_RATE, " rpm"));//Frecuencia Respiratoria
        colorGrupos(fragmentado,R.id.fragmentado_group);
        //Grupo 4: Actividad y Recuperación
        actividad.add(setupParameter(R.id.actividad_fc_valor, R.id.actividad_fc_sem, heartRate, MedicalRanges.HEART_RATE, " lpm")); //Frecuencia Cardiaca
        actividad.add(setupParameter(R.id.actividad_pad_valor, R.id.actividad_pad_sem, diastolic, MedicalRanges.BLOOD_PRESSURE_DIASTOLIC, " mmHg"));//Presion Arterial Diastólica
        actividad.add(setupParameter(R.id.actividad_pas_valor, R.id.actividad_pas_sem, systolic, MedicalRanges.BLOOD_PRESSURE_SYSTOLIC, " mmHg"));//Presion Arterial Sistólica
        colorGrupos(actividad,R.id.actividad_group);

        //Grupo 5: Infeccion
        // Para temperatura con decimales
        float temperature = Float.parseFloat(tempInt + "." + tempFloat);
        // Para temperatura con decimales (caso especial)
        String tempFormatted = String.format(Locale.US, "%.1f°C", temperature);
        TextView tvTemp = findViewById(R.id.infeccion_tmp_valor);
        tvTemp.setText(tempFormatted);
        infeccion.add(setupIndicator(R.id.infeccion_tmp_sem, (int) temperature, MedicalRanges.TEMP));
        fragmentado.add(setupParameter(R.id.infeccion_fr_valor, R.id.infeccion_fr_sem, respRate, MedicalRanges.RESP_RATE, " rpm"));//Frecuencia Respiratoria
        circulatorio.add(setupParameter(R.id.infeccion_ox_valor, R.id.infeccion_ox_sem, oxygen, MedicalRanges.OXYGEN, "%"));//Oxigenacion
        colorGrupos(infeccion,R.id.infeccion_group);
    }
    //Definir semaforos
    @SuppressLint("ResourceAsColor")
    public void colorGrupos(List<Integer> lista, Integer id) {
        int contadorUnos = 0;
        // Contar la cantidad de unos en la lista
        for (int valor : lista) {
            if (valor == 1) {
                contadorUnos++;
            }
        }
        LinearLayout miLayout = findViewById(id);
        // Mostrar Toast según la cantidad de unos
        if (contadorUnos == 0) {
            miLayout.setBackgroundColor((ContextCompat.getColor(this, R.color.green))); // Verde
        } else if (contadorUnos <= 2) {
            miLayout.setBackgroundColor((ContextCompat.getColor(this, R.color.orange))); // Amarillo
        } else {
            miLayout.setBackgroundColor((ContextCompat.getColor(this, R.color.red))); // Rojo
        }
    }
    private int setupIndicator(int indicatorId, int value, int[] ranges) {
        View indicator = findViewById(indicatorId);
        GradientDrawable drawable = (GradientDrawable) indicator.getBackground();
        drawable.setColor(getStatusColor(value, ranges));
        return getStatusNum(value,ranges);
    }
    private int setupParameter(int valueId, //Nombre de la variable
                                int indicatorId,//Indicador
                                int value, //Valor de la variable
                                int[] ranges, //Rangos
                                String unit //Unidad de la variable
    ) {

        TextView tvValue = findViewById(valueId);
        View indicator = findViewById(indicatorId);

        tvValue.setText(value + unit);

        // Cambia color según el rango
        int colorRes = getStatusColor(value, ranges);
        int colorNum = getStatusNum(value,ranges);
        GradientDrawable indicatorBg = (GradientDrawable) indicator.getBackground();
        indicatorBg.setColor(colorRes);

        return colorNum;
    }
    //Logica de colores
    private int getStatusColor(int value, @NonNull int[] ranges) {
        if (value < ranges[0] || value > ranges[1]) {
            return ContextCompat.getColor(this, R.color.orange);
        }
        else if (value < ranges[2] || value > ranges[3]) {
            return ContextCompat.getColor(this, R.color.yellow);
        }
        else {
            return ContextCompat.getColor(this, R.color.green);
        }
    }
    private int getStatusNum(int value, @NonNull int[] ranges) {
        if (value < ranges[0] || value > ranges[1] || (value < ranges[2] || value > ranges[3])) {
            return 1;
        }
        else {
            return 0;
        }
    }
/**Intefaz**/

    private void topBar(String title){
        //Barra de navegación superior
        TextView titleTextView = findViewById(R.id.title_text_view); // Titulo
        titleTextView.setText(title);
        ImageView btnBack = findViewById(R.id.btn_back_bp);
        btnBack.setOnClickListener(v -> finish());
    }
    // Funcion para toggle
    private void configAllGroups() {
        int[][] groupPairs = {
                {R.id.cardiovascularBoton, R.id.cardioContenido},
                {R.id.circulatorioBoton, R.id.circulatorioContenido},
                {R.id.fragmentadoBoton, R.id.fragmentadoContenido},
                {R.id.metabolicoBoton, R.id.metabolicoContenido},
                {R.id.infeccionBoton, R.id.infeccionContenido}
        };

        for (int[] pair : groupPairs) {
            ImageView boton = findViewById(pair[0]);
            boton.setOnClickListener(v -> toggleIconAndLayout(pair[0], pair[1]));
            toggleIconAndLayout(pair[0], pair[1]);
        }
    }
    // Función que maneja el cambio
    private void toggleIconAndLayout(int imageViewId, int linearLayoutId) {
        ImageView imageView = findViewById(imageViewId);
        LinearLayout linearLayout = findViewById(linearLayoutId);
        // Verifica visibilidad actual del LinearLayout
        boolean isVisible = linearLayout.getVisibility() == View.VISIBLE;
        if (isVisible) {
            // Cambia a icono cerrado/colapsado
            imageView.setImageResource(R.drawable.circle_down);
            // Oculta el LinearLayout
            linearLayout.setVisibility(View.GONE);
        } else {
            // Cambia a icono abierto/expandido
            imageView.setImageResource(R.drawable.circle_up);
            // Muestra el LinearLayout
            linearLayout.setVisibility(View.VISIBLE);
        }
    }
    public Bitmap getBitmapFromScrollView(ScrollView scrollView) {

        // 1. Forzar el dibujado de todas las vistas
        scrollView.setDrawingCacheEnabled(true);
        scrollView.buildDrawingCache();

        // 2. Crear bitmap del ScrollView completo
        Bitmap bitmap = Bitmap.createBitmap(
                scrollView.getChildAt(0).getWidth(),
                scrollView.getChildAt(0).getHeight(),
                Bitmap.Config.ARGB_8888
        );

        // 3. Dibujar en un Canvas
        Canvas canvas = new Canvas(bitmap);
        scrollView.getChildAt(0).draw(canvas);

        // 4. Limpiar
        scrollView.setDrawingCacheEnabled(false);

        return bitmap;
    }
    public void generarPDFDesdeScrollView(ScrollView scrollView) {
        Bitmap bitmap = getBitmapFromScrollView(scrollView);

        PdfDocument pdfDocument = new PdfDocument();

        // Configurar tamaño de la página igual al bitmap
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(
                bitmap.getWidth(),
                bitmap.getHeight(),
                1
        ).create();

        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        canvas.drawBitmap(bitmap, 0, 0, null);
        pdfDocument.finishPage(page);

        // Generar nombre con fecha y hora
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "Diagnostico_" + timeStamp + ".pdf";

        // Guardar archivo en almacenamiento privado de la app
        File filePath = new File(getExternalFilesDir(null), fileName);
        try {
            pdfDocument.writeTo(new FileOutputStream(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        pdfDocument.close();
    }

}
