package com.example.myhealthlife.fragments;
import static android.content.Context.MODE_PRIVATE;
import static com.example.myhealthlife.model.DeviceAdapter.setDeviceImage;
import static com.example.myhealthlife.model.HealthWorker.getUserId;
import static com.example.myhealthlife.model.HealthWorker.getValue;
import static com.example.myhealthlife.model.MetallicTint.applyMetallicGradient;
import static com.example.myhealthlife.model.PrefsHelper.agregarHistorial;
import static com.yucheng.ycbtsdk.Constants.DATATYPE.Health_DeleteAll;
import static com.yucheng.ycbtsdk.Constants.DATATYPE.Real_UploadHeart;
import static com.yucheng.ycbtsdk.Constants.DATATYPE.Real_UploadSport;
import static com.yucheng.ycbtsdk.YCBTClient.appRealAllDataFromDevice;
import static com.yucheng.ycbtsdk.YCBTClient.appRealSportFromDevice;
import static com.yucheng.ycbtsdk.YCBTClient.appRegisterRealDataCallBack;
import static com.yucheng.ycbtsdk.YCBTClient.appSengMessageToDevice;
import static com.yucheng.ycbtsdk.YCBTClient.appSleepWriteBack;
import static com.yucheng.ycbtsdk.YCBTClient.connectBle;
import static com.yucheng.ycbtsdk.YCBTClient.connectState;
import static com.yucheng.ycbtsdk.YCBTClient.deleteHealthHistoryData;
import static com.yucheng.ycbtsdk.YCBTClient.getBindDeviceName;
import static com.yucheng.ycbtsdk.YCBTClient.healthHistoryData;
import static com.yucheng.ycbtsdk.YCBTClient.initClient;
import static com.yucheng.ycbtsdk.YCBTClient.resetQueue;
import static com.yucheng.ycbtsdk.YCBTClient.setReconnect;
import static com.yucheng.ycbtsdk.YCBTClient.settingBloodOxygenModeMonitor;
import static com.yucheng.ycbtsdk.YCBTClient.settingHeartMonitor;
import static com.yucheng.ycbtsdk.YCBTClient.settingTemperatureMonitor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.compose.ui.unit.Constraints;
import androidx.fragment.app.Fragment;

import com.example.myhealthlife.R;
import com.example.myhealthlife.activities.BloodPressureActivity;
import com.example.myhealthlife.activities.HeartRateActivity;
import com.example.myhealthlife.activities.OxygenLogActivity;
import com.example.myhealthlife.activities.RespiratoryRateActivity;
import com.example.myhealthlife.activities.TemperatureLogActivity;
import com.example.myhealthlife.model.AnimatedCircularProgress;

import com.example.myhealthlife.model.HealthInfoCardView;
import com.example.myhealthlife.model.HealthViewModel;
import com.example.myhealthlife.model.HealthWorker;
import com.example.myhealthlife.model.HistoryData;
import com.example.myhealthlife.model.SportViewModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yucheng.ycbtsdk.Constants;
import com.yucheng.ycbtsdk.YCBTClient;
import com.yucheng.ycbtsdk.gatt.Reconnect;
import com.yucheng.ycbtsdk.response.BleConnectResponse;
import com.yucheng.ycbtsdk.response.BleDataResponse;
import com.yucheng.ycbtsdk.response.BleRealDataResponse;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class HomeFragment extends Fragment {
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView kcal_sport, goalSteps_sport, distance_sport, hora;
    private AnimatedCircularProgress circularProgress;
    private HealthInfoCardView blood_card, oxygen_card, heart_rate_card, tempeture_card, ecg_card, sleep_card, frec_resp_card,hr_hrv_card ;
    private LinearLayout cards_container, ble_icon;
    private boolean isLoading = false;
    private ImageView sync_button, iconRight;
    SharedPreferences prefs;
    private BluetoothDialogFragment dialog;
    private SportViewModel viewModel;
    private HealthViewModel viewModelH;
    private WorkManager workManager;
    private LinearLayout row1, row2, row3, row4;
    private final List<View.OnLayoutChangeListener> layoutChangeListeners = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d("HOME","Vista Creada");
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        initViews(view);                                        //Iniciar Vistas
        configureButtons();                                     //Configurar las acciones de los botones
        /*setDashboard(view);                                     //Configurar los datos de las vistas
        testWorker(view);
        updateFunctions();*/

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        /*healthMonitoringFun(1,false);
        updateFunctions();*/
        super.onResume();
    }
    @Override
    public void onStart() {
        super.onStart();
        /*healthMonitoringFun(1,false);*/
    }
    @Override
    public void onStop() {
        super.onStop();
        //healthMonitoringFun(0,false);
    }

    //------------------------------------------------------------------------------------------------
    /**
     * PRINCIPALES
     **/
    private void initViews(@NonNull View view) {
        // Inicializar SwipeRefreshLayout
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        // Configurar el listener para el gesto de deslizar
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Llamar al método de actualización
                //Toast.makeText(requireContext(), getString(R.string.home_actualizando), Toast.LENGTH_SHORT).show();
                if (isLoading){
                    Toast.makeText(requireContext(), getString(R.string.home_por_favor_espera), Toast.LENGTH_SHORT).show();
                }
                else {
                    if(updateFunctions()){
                        isLoading = true;
                        Toast.makeText(getContext(),getString(R.string.home_actualizando), Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(() -> {
                            isLoading = false;
                            swipeRefreshLayout.setRefreshing(false);

                        }, 10000); //  10 segundos
                    }
                    else{
                        Toast.makeText(requireContext(), getString(R.string.home_por_favor_conecte), Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                        //swipeRefreshLayout.setEnabled(false);
                    }
                }
            }
        });

        //swipeRefreshLayout.setSize(1);

        TextView tickerText = view.findViewById(R.id.tickerText);
        // "Marqueetador"
        tickerText.setSelected(true);

        circularProgress = view.findViewById(R.id.circularProgress);
        circularProgress.setProgressWithAnimation(40);

        blood_card = view.findViewById(R.id.blood_card);
        oxygen_card = view.findViewById(R.id.oxygen_card);

        ble_icon = view.findViewById(R.id.ble_icon);

        //Presión Arterial
        blood_card.applyStatus(2);
        blood_card.setIcon(R.drawable.stethoscope);
        blood_card.setTitle(getString(R.string.presion_arterial));
        blood_card.setValue("120/80");
        blood_card.setUnit("mmHg");
        blood_card.setLastUpdate("Hace 10 min");

        //Oxigeno
        oxygen_card.applyStatus(2);
        oxygen_card.setIcon(R.drawable.atom);
        oxygen_card.setTitle(getString(R.string.saturacion_oxigeno));
        oxygen_card.setValue("98");
        oxygen_card.setUnit("%");
        oxygen_card.setLastUpdate("Hace 5 min");


    }
    public void configureButtons(){
    // Sports
    dialog = new BluetoothDialogFragment();

    ble_icon.setOnClickListener(v -> {
        dialog.show(getChildFragmentManager(), "BluetoothDialogFragment");
    });
        // Health Items
    }/*
    private void setDashboard(View view){
        // Observar cambios en los datos

        //Historial de Salud
        viewModel.getSportSteps().observe(getViewLifecycleOwner(), steps -> {
            Integer goalSteps = viewModel.getSportGoalSteps().getValue();
            if (goalSteps != null && goalSteps > 0) {
                updateProgress(steps, goalSteps);
            }
        });
        viewModel.getSportGoalSteps().observe(getViewLifecycleOwner(), goalSteps -> {
            Integer steps = viewModel.getSportSteps().getValue();
            if (steps != null && steps > 0) {
                updateProgress(steps, goalSteps);
            }
        });
        viewModel.getSportCalories().observe(getViewLifecycleOwner(), calories -> {
            kcal_sport.setText(calories + " Kcal");
        });
        viewModel.getSportDistance().observe(getViewLifecycleOwner(), distance -> {
            distance_sport.setText((float)distance/1000 + " Km");
        });

        //Historial de Salud
        viewModelH.getHealthBloodPressure().observe(getViewLifecycleOwner(), v -> {
            if(v==null){
                blood_item.setMainValue("--");
            }else{
                blood_item.setMainValue(v+" ");
            }
        });
        viewModelH.getHealthHeart().observe(getViewLifecycleOwner(), v -> {
            if(v==null){
                heart_rate_item.setMainValue("--");
            }else{
                heart_rate_item.setMainValue(v+" ");
                heart_rate_item.setMainUnit(getString(R.string.rpm),true);
            }
        });
        viewModelH.getHealthRespRate().observe(getViewLifecycleOwner(), resp -> {
            if(resp==null){
                frec_resp_item.setMainValue("--");
            }else{
                frec_resp_item.setMainValue(resp+" ");
                frec_resp_item.setMainUnit(getString(R.string.rpm),true);
            }
        });
        viewModelH.getHealthTemp().observe(getViewLifecycleOwner(), v -> {
            if(v == null){
                tempeture_item.setMainValue("--");
            }else{
                tempeture_item.setMainValue(v+" ");
                tempeture_item.setMainUnit(getString(R.string.celsius),true);
            }
        });
        viewModelH.getHealthOxygen().observe(getViewLifecycleOwner(), v -> {
            if(v == null){
                oxygen_item.setMainValue("--");
            }else{
                oxygen_item.setMainValue(v+" ");
            }
            //oxygen_item.setMainUnit(getString(R.string.percent),true);
        });
        viewModelH.getSleepDuration().observe(getViewLifecycleOwner(), v -> {
            if(v == null ) {
                sleep_item.setMainValue("0");
                sleep_item.setMainUnit("h ",true);
                sleep_item.setSecondaryValue("0",true);
                sleep_item.setSecondaryUnit("m",true);
            }else{
                long duration = Long.parseLong(v);
                long hours = duration / 60;
                long minutes = duration % 60;
                sleep_item.setMainValue(hours+"");
                sleep_item.setMainUnit("h ",true);
                sleep_item.setSecondaryValue(minutes+"",true);
                sleep_item.setSecondaryUnit("m",true);
            }
        });
        viewModelH.getHealthStartTime().observe(getViewLifecycleOwner(), v -> {
            if(v != null && !v.isEmpty()) {
                String timeStr = getString(R.string.ultima_actualizacion_sin_texto)+": "+v;
                hora.setText(timeStr);
            }
        });
        viewModelH.getHealthHRVCVRR().observe(getViewLifecycleOwner(), v -> {
            if(v != null && !v.isEmpty()) {

                String hrv = viewModelH.getHealthHRV().toString();
                String cvrr = viewModelH.getHealthCVVRR().toString();

                hr_hrv_item.setMainValue(hrv+"/"+cvrr);

            }
        });

        getData();
    }
    private void getData(){

        //DEPORTES
        prefs = setPrefs("sport_prefs");
        int lastgoalSteps = prefs.getInt("sport_goal_steps", 0);
        int lastSteps = prefs.getInt("sport_steps", 0);
        int lastCalories = prefs.getInt("sport_calories", 0);
        int lastDistance = prefs.getInt("sport_distance", 0);

        //Configurar valores al cargar por primera vez la vista
        viewModel.setSportGoalStep(lastgoalSteps, getContext());
        viewModel.setSportStep(lastSteps, getContext());
        viewModel.setSportCalories(lastCalories, getContext());
        viewModel.setSportDistance(lastDistance, getContext());
        // Inicia animación del progreso
        circularProgress.setProgressText(lastSteps);
        float stepsPct = (lastSteps * 100f / lastgoalSteps);
        circularProgress.setProgressWithAnimation(stepsPct);

        //ITEMS
        prefs = setPrefs("health_prefs");
        String heart = prefs.getString("health_heart",null);
        String resp = prefs.getString("health_resp",null);
        String temp = prefs.getString("health_temp",null);
        String oxy = prefs.getString("health_ox",null);
        String blood = prefs.getString("health_blood",null);
        String start = prefs.getString("health_start",null);
        String sleep = prefs.getString("sleep_duration",null);

        viewModelH.sethealthRespRate(resp, getContext());
        viewModelH.sethealthHeart(heart, getContext());
        viewModelH.sethealthTemp(temp, getContext());
        viewModelH.sethealthOxygen(oxy, getContext());
        viewModelH.setHealthBloodPressure(blood, getContext());
        viewModelH.setHealthStartTime(start, getContext());
        viewModelH.setSleepDuration(sleep, getContext());
    }
    *//**
     *YCBT
     **/
    //Iniciar el Cliente
    private void initClientFun(){
        //Inicializa el YCBTClient
        initClient(
                this.getContext(),   //contexto,
                true,                //Reconectar el dispositivo
                false                //Modo Debug
        );
        //Fuerza la reconexión con el reloj
        Reconnect.getInstance().init(
                this.getContext(),
                true);
        setReconnect(true);
    }
    //Obtener Datos del Reloj
    private void getHealthData(){
        resetQueue();
        healthMonitoringFun(1,false);

        //Método para obtener el historial de sueño
        healthHistoryData(
                Constants.DATATYPE.Health_HistorySleep,
                new BleDataResponse() {
                    @Override
                    public void onDataResponse(int code, float v, HashMap hashMap) {
                        Log.d("Health_HistorySleep", "ACTUALIZANDO... 📥 Dato recibido -> code: " + code + " | v: " + v + " | data: " + hashMap);

                        try {
                            // Obtener el objeto data del hashMap
                            Object dataObj = hashMap.get("data");

                            if (dataObj == null) {
                                Log.e("SLEEP", "❌ No hay datos en la respuesta");
                                return;
                            }

                            // Convertir a String y luego parsear con Gson
                            String jsonData = new Gson().toJson(dataObj);
                            Log.d("SLEEP", "JSON Data: " + jsonData);

                            // Usar List<Map> en lugar de ArrayList<HashMap>
                            Type type = new TypeToken<List<Map<String, Object>>>() {}.getType();
                            List<Map<String, Object>> dataList = new Gson().fromJson(jsonData, type);

                            // Guardar datos crudos
                            prefs.edit().putString("sleep", dataList.toString()).apply();

                            if (dataList != null && !dataList.isEmpty()) {
                                // Tomar el último registro de sueño (más reciente)
                                Map<String, Object> lastSleepRecord = dataList.get(dataList.size() - 1);

                                // Extraer valores con conversión segura
                                long startTime = getLongValue(lastSleepRecord.get("startTime"));
                                long endTime = getLongValue(lastSleepRecord.get("endTime"));
                                int deepSleepTotal = getIntValue(lastSleepRecord.get("deepSleepTotal"));
                                int lightSleepTotal = getIntValue(lastSleepRecord.get("lightSleepTotal"));
                                int rapidEyeMovementTotal = getIntValue(lastSleepRecord.get("rapidEyeMovementTotal"));
                                int wakeDuration = getIntValue(lastSleepRecord.get("wakeDuration"));

                                // Convertir tiempos a formato legible
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                                String startTimeStr = sdf.format(new Date(startTime));
                                String endTimeStr = sdf.format(new Date(endTime));

                                // Calcular duración total del sueño en horas y minutos
                                long sleepDurationMs = endTime - startTime;
                                long sleepDurationMinutes = TimeUnit.MILLISECONDS.toMinutes(sleepDurationMs);
                                viewModelH.setSleepDuration(String.valueOf(sleepDurationMinutes), getContext());
                                long hours = sleepDurationMinutes / 60;
                                long minutes = sleepDurationMinutes % 60;

                                // Convertir tiempos de sueño de segundos a minutos
                                int deepSleepMinutes = deepSleepTotal / 60;
                                int lightSleepMinutes = lightSleepTotal / 60;
                                int remSleepMinutes = rapidEyeMovementTotal / 60;
                                int wakeMinutes = wakeDuration / 60;

                                // Procesar segmentos de sueño detallados
                                List<Map<String, Object>> sleepDataList = null;
                                Object sleepDataObj = lastSleepRecord.get("sleepData");

                                if (sleepDataObj != null) {
                                    // Convertir sleepData a lista
                                    String sleepDataJson = new Gson().toJson(sleepDataObj);
                                    Type sleepDataType = new TypeToken<List<Map<String, Object>>>() {}.getType();
                                    sleepDataList = new Gson().fromJson(sleepDataJson, sleepDataType);
                                }

                                StringBuilder sleepSegments = new StringBuilder();
                                if (sleepDataList != null && !sleepDataList.isEmpty()) {
                                    for (int i = 0; i < sleepDataList.size(); i++) {
                                        Map<String, Object> segment = sleepDataList.get(i);
                                        long segmentStart = getLongValue(segment.get("sleepStartTime"));
                                        int segmentDuration = getIntValue(segment.get("sleepLen"));
                                        int sleepType = getIntValue(segment.get("sleepType"));

                                        String segmentType = getSleepTypeString(sleepType);
                                        String segmentTime = sdf.format(new Date(segmentStart));

                                        sleepSegments.append("\n  Segmento ").append(i + 1)
                                                .append(": ").append(segmentType)
                                                .append(" | Inicio: ").append(segmentTime)
                                                .append(" | Duración: ").append(segmentDuration).append(" min");
                                    }
                                }

                                // Imprimir log detallado de la última lectura
                                Log.d("SLEEP", "══════════════════════════════════════════════════");
                                Log.d("SLEEP", "📊 ÚLTIMA LECTURA DE SUEÑO ANALIZADA");
                                Log.d("SLEEP", "══════════════════════════════════════════════════");
                                Log.d("SLEEP", "⏰ Período: " + startTimeStr + " - " + endTimeStr);
                                Log.d("SLEEP", "⏱️ Duración total: " + hours + "h " + minutes + "m");
                                Log.d("SLEEP", "──────────────────────────────────────────────────");
                                Log.d("SLEEP", "😴 Sueño profundo: " + deepSleepMinutes + " min (" + deepSleepTotal + " seg)");
                                Log.d("SLEEP", "😪 Sueño ligero: " + lightSleepMinutes + " min (" + lightSleepTotal + " seg)");
                                Log.d("SLEEP", "👁️ Sueño REM: " + remSleepMinutes + " min (" + rapidEyeMovementTotal + " seg)");
                                Log.d("SLEEP", "👁️ Despierto durante: " + wakeMinutes + " min (" + wakeDuration + " seg)");
                                Log.d("SLEEP", "──────────────────────────────────────────────────");
                                Log.d("SLEEP", "📈 Segmentos de sueño:" + sleepSegments.toString());
                                Log.d("SLEEP", "══════════════════════════════════════════════════");

                                // También guardar resumen estructurado
                                Map<String, Object> summary = new HashMap<>();
                                summary.put("fecha_inicio", startTimeStr);
                                summary.put("fecha_fin", endTimeStr);
                                summary.put("duracion_total", hours + "h " + minutes + "m");
                                summary.put("sueño_profundo", deepSleepMinutes + " min");
                                summary.put("sueño_ligero", lightSleepMinutes + " min");
                                summary.put("sueño_rem", remSleepMinutes + " min");
                                summary.put("tiempo_despierto", wakeMinutes + " min");

                                prefs.edit().putString("sleep_summary", new Gson().toJson(summary)).apply();

                            } else {
                                Log.d("SLEEP", "⚠️ No hay datos de sueño disponibles");
                            }

                        } catch (Exception e) {
                            Log.e("SLEEP", "❌ Error procesando datos de sueño: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }

                    // Método auxiliar para convertir códigos de tipo de sueño a texto
                    private String getSleepTypeString(int sleepType) {
                        switch (sleepType) {
                            case 241: return "Sueño ligero";
                            case 242: return "Sueño profundo";
                            case 243: return "Sueño REM";
                            case 244: return "Despierto";
                            default: return "Desconocido (" + sleepType + ")";
                        }
                    }

                    // Método para obtener valores long de forma segura
                    private long getLongValue(Object value) {
                        if (value == null) return 0L;
                        if (value instanceof Double) {
                            return ((Double) value).longValue();
                        } else if (value instanceof Long) {
                            return (Long) value;
                        } else if (value instanceof Integer) {
                            return ((Integer) value).longValue();
                        } else {
                            try {
                                return Long.parseLong(value.toString());
                            } catch (NumberFormatException e) {
                                return 0L;
                            }
                        }
                    }

                    // Método para obtener valores int de forma segura
                    private int getIntValue(Object value) {
                        if (value == null) return 0;
                        if (value instanceof Double) {
                            return ((Double) value).intValue();
                        } else if (value instanceof Integer) {
                            return (Integer) value;
                        } else if (value instanceof Long) {
                            return ((Long) value).intValue();
                        } else {
                            try {
                                return Integer.parseInt(value.toString());
                            } catch (NumberFormatException e) {
                                return 0;
                            }
                        }
                    }
                });
        //Método para obtener la presion arterial (por si el historial de salud no lo recupera)
        final int[] DBPVal = {0};
        final int[] SBPVal = {0};
        healthHistoryData(
                Constants.DATATYPE.Health_HistoryBlood,
                new BleDataResponse() {
                    @Override
                    public void onDataResponse(int code, float v, HashMap hashMap) {
                        Log.d("Health_HistoryBlood", "ACTUALIZANDO... 📥 Dato recibido -> code: " + code + " | v: "+v+" | data: " + hashMap);
                        if(!hashMap.isEmpty()){
                            Type type = new TypeToken<ArrayList<HashMap<String, Object>>>() {}.getType();
                            ArrayList<HashMap<String, Object>> dataList = new Gson().fromJson(new Gson().toJson(hashMap.get("data")), type);

                            if (dataList != null && !dataList.isEmpty()) {
                                int totalDatos = dataList.size();
                                int rangoDatos = totalDatos - 1; //En este caso solo tomaré el último arreglo

                                for (int i = totalDatos - 1; i >= rangoDatos; i--) {
                                    HashMap<String, Object> r = dataList.get(i);
                                    DBPVal[0] = (int) getValue("DBPValue",r);
                                    SBPVal[0] = (int) getValue("SBPValue",r);
                                }
                            }
                            else {
                                Log.d("HISTORIAL_BLOOD", "No hay presion arterial");
                            }
                        }
                    }
                });
        //Método para obtener el historial de salud
        healthHistoryData(
                Constants.DATATYPE.Health_HistoryAll,
                new BleDataResponse() {
                    @Override
                    public void onDataResponse(int code, float v, HashMap hashMap) {
                        Log.d("Health_HistoryAll", "ACTUALIZANDO... 📥 Dato recibido -> code: " + code + " | v: "+v+" | data: " + hashMap);

                        Type type = new TypeToken<ArrayList<HashMap<String, Object>>>() {}.getType();
                        ArrayList<HashMap<String, Object>> dataList = new Gson().fromJson(new Gson().toJson(hashMap.get("data")), type);

                        if (dataList != null && !dataList.isEmpty()) {
                            int totalDatos = dataList.size();
                            int datosATomar = Math.min(totalDatos, 3);
                            int rangoDatos = totalDatos - 1; //En este caso solo tomaré el último arreglo
                            ArrayList<HistoryData> registros = new ArrayList<>();

                            for (int i = totalDatos - 1; i >= rangoDatos; i--) {
                                HashMap<String, Object> r = dataList.get(i);
                                int heartValue =            (int) getValue("heartValue", r);
                                int hrvValue =              (int) getValue("hrvValue", r);
                                int cvrrValue =             (int) getValue("cvrrValue", r);
                                int OOValue =               (int) getValue("OOValue", r);
                                int stepValue =             (int) getValue("stepValue", r);
                                int DBPValue =              (int) getValue("DBPValue", r);
                                if(DBPValue == 0) DBPValue = DBPVal[0];
                                Log.d("HISTORIAL_BLOOD","DBPValue: "+DBPValue);
                                int SBPValue =              (int) getValue("SBPValue", r);
                                if(SBPValue == 0) SBPValue = SBPVal[0];
                                Log.d("HISTORIAL_BLOOD","SBPValue: "+SBPValue);
                                int rrrValue =              (int) getValue("respiratoryRateValue", r);
                                int bfiValue =              (int) getValue("bodyFatIntValue", r);
                                int bffValue =              (int) getValue("bodyFatIntValue", r);
                                int bloodsValue =           (int) getValue("bloodSugarValue", r);
                                int tempIntValue =          (int) getValue("tempIntValue", r);
                                    if(tempIntValue == 0) tempIntValue = 35;
                                int tempFloatValue =        (int) getValue("tempFloatValue", r);
                                    if(tempFloatValue == 0) tempFloatValue = 35;
                                long startTime =            (long) getValue("startTime", r);
                                String startTimeStr = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(startTime);

                                viewModelH.sethealthHeart(""+heartValue, getContext());
                                viewModelH.sethealthHRV(""+hrvValue, getContext());
                                viewModelH.sethealthCVVRR(""+cvrrValue, getContext());
                                viewModelH.sethealthStep(""+stepValue, getContext());
                                viewModelH.sethealthOxygen(""+ OOValue, getContext());

                                viewModelH.setHealthBloodPressure(SBPValue+"/"+DBPValue, getContext());


                                viewModelH.sethealthRespRate(""+DBPValue, getContext());
                                viewModelH.sethealthBody(bfiValue+"."+bffValue, getContext());
                                viewModelH.sethealthTemp(tempIntValue+"."+tempFloatValue, getContext());
                                viewModelH.setHealthStartTime(startTimeStr, getContext());

                                HistoryData reg = new HistoryData(
                                        heartValue      ,
                                        hrvValue        ,
                                        cvrrValue       ,
                                        OOValue         ,
                                        stepValue       ,
                                        DBPValue        ,
                                        SBPValue        ,
                                        rrrValue        ,
                                        bfiValue        ,
                                        bffValue        ,
                                        bloodsValue     ,
                                        tempIntValue    ,
                                        tempFloatValue  ,
                                        startTime

                                );

                                agregarHistorial(requireContext(),reg,false,getUserId(requireContext()));

                                Log.d("HISTORIAL_HEALTHDATA",
                                        "Registro #" + (i+1) + ":\n" +
                                                "Frecuencia cardíaca: " + r.get("heartValue") + "\n" +
                                                "HRV: " + r.get("hrvValue") + "\n" +
                                                "CVRR: " + r.get("cvrrValue") + "\n" +
                                                "PASOS: " + r.get("stepValue") + "\n" +
                                                "Oxígeno: " + r.get("OOValue") + "\n" +
                                                "Pasos: " + r.get("stepValue") + "\n" +
                                                "Presión diastólica: " + r.get("DBPValue") + "\n" +
                                                "Presión sistólica: " + r.get("SBPValue") + "\n" +
                                                "Frecuencia respiratoria: " + r.get("respiratoryRateValue") + "\n" +
                                                "Grasa corporal: " + r.get("bodyFatIntValue") + "." + r.get("bodyFatFloatValue") + "\n" +
                                                "Glucosa: " + r.get("bloodSugarValue") + "\n" +
                                                "Temperatura: " + r.get("tempIntValue") + "." + r.get("tempFloatValue") + "\n" +
                                                "Tiempo: " + startTimeStr + " " + "\n" +
                                                "----------------------------------");
                            }

                        }
                        else {
                            Log.d("HISTORIAL", "No hay datos disponibles");
                        }
                    }
                });
        //Método para obtener Datos de Deporte en tiempo real
        appRealSportFromDevice(
                0x01,   //activo
                new BleDataResponse() {
                    @Override
                    public void onDataResponse(int code, float value, HashMap data) {
                        //Log.d("HISTORIAL", "appRealSportFromDevice" +"| code: "+code+" | data: "+ data);
                        appRegisterRealDataCallBack(new BleRealDataResponse() {
                                @Override
                                public void onRealDataResponse(int i, HashMap hashMap) {
                                    Log.d("appRealSport", "hashMap: " + hashMap );
                                    if (hashMap != null && !hashMap.isEmpty()) {
                                        /*int sportStep = getSportValue(hashMap,"sportStep");                  //step count
                                        int sportDistance = getSportValue(hashMap,"sportDistance");     //distance
                                        int sportCalorie = getSportValue(hashMap,"sportCalorie");       //calories
                                        viewModel.setSportStep(sportStep, getContext());
                                        viewModel.setSportDistance(sportDistance, getContext());
                                        viewModel.setSportCalories(sportCalorie, getContext());*/
                                    }
                                }
                            }
                        );
                    }
                }
        );
        //resetQueue();
        //Método para obtener el resto de datos (heart, oxygen, bloodP) en tiempo real

        appRealAllDataFromDevice(
                0x01,
                1,
                new BleDataResponse() {
                    @Override
                    public void onDataResponse(int code, float value, HashMap data) {
                        Log.d("appRealAll", "appRealSportFromDevice" +"| code: "+code+" | data: "+ data);
                        appRegisterRealDataCallBack(new BleRealDataResponse() {
                            @Override
                            public void onRealDataResponse(int i, HashMap hashMap) {
                                Log.d("appRealAll", "hashMap: " + hashMap );
                                if (hashMap != null && !hashMap.isEmpty()) {
                                    Log.d("appRealAll", "hashMap NO ES NULL!: " + hashMap  );
                                }
                                if(i == Real_UploadHeart){
                                    if (hashMap != null && !hashMap.isEmpty()) {
                                        int heart = getHealthValue(hashMap,"heartValue");
                                        int oxygen = getHealthValue(hashMap,"bloodOxygenValue");
                                        int bloodSBP = getHealthValue(hashMap,"bloodSBP");
                                        int bloodDBP = getHealthValue(hashMap,"bloodDBP");
                                        //Log.d("HISTORIAL_Real_UploadHeart", "heart: " + heart);
                                        //HACE FALTA MANDAR A LA VISTA ESTOS VALORES
                                    }
                                }
                                if(code == Real_UploadSport){
                                    if (hashMap != null && !hashMap.isEmpty()) {

                                        //int sportStep = getAndUpdateSportValue(hashMap,"sportStep");                  //step count
                                        //int sportDistance = getAndUpdateSportValue(hashMap,"sportDistance");     //distance
                                        //int sportCalorie = getAndUpdateSportValue(hashMap,"sportCalorie");       //calories
                                        //Log.d("HISTORIAL_Real_UploadSport", "sportStep: " + sportStep);

                                        //viewModel.setSportStep(sportStep, getContext());
                                        //viewModel.setSportDistance(sportDistance, getContext());
                                        //viewModel.setSportCalories(sportCalorie, getContext());
                                    }
                                }
    }});
        }
        }
        );
        //resetQueue();
        //Los datos no se borran, hasta que se llene la memoria del disposivio

        deleteHealthHistoryData(
                Health_DeleteAll,
                new BleDataResponse() {
                    @Override
                    public void onDataResponse(int i, float v, HashMap hashMap) {
                        Log.d("HISTORIAL","Borrando Historial...  code: "+ i+ " | v: "+v+" | hashMap:"+hashMap);
                    }
                });
    }

    //Configurar Monitoreo
    private void healthMonitoringFun(int interval, Boolean showLog){

        // SI EL INTERVALO ES 0, SE USA EL INTERVALO GUARDADO EN SHARED_PREFERENCES

        prefs = setPrefs("monitoring");
        int min = interval;
        if(interval < 1) min= prefs.getInt("interval", 15);

        //El dispositivo medirá los datos correspondientes y los guardará
        Log.d("MONITEREO: ",min+" min configurados");
        settingHeartMonitor(
                0x01,            //Forma automática
                min,                //Minutos de monitoreo
                new BleDataResponse() {
                    @Override
                    public void onDataResponse(int i, float v, HashMap hashMap) {
                        if(showLog) Log.d("HISTORIAL","settingHeartMonitor: code: "+ i+ " |v: "+v + " |hashMap: " + hashMap );
                    }
                });
        settingTemperatureMonitor(
                true,          //Monitoreo activado
                min,                //Minutos de monitoreo
                new BleDataResponse() {
                    @Override
                    public void onDataResponse(int i, float v, HashMap hashMap) {
                        if(showLog) Log.d("HISTORIAL","settingTemperatureMonitor: code: "+ i+ " |v: "+v + " |hashMap: " + hashMap );

                    }
                });
        settingBloodOxygenModeMonitor(
                true,          //Monitoreo activado
                min,               //Minutos de monitoreo
                new BleDataResponse() {
                    @Override
                    public void onDataResponse(int i, float v, HashMap hashMap) {
                        if(showLog) Log.d("HISTORIAL","settingBloodOxygenModeMonitor: code: "+ i+ " |v: "+v + " |hashMap: " + hashMap );
                    }
                });
    }
    //** AUXILIARES*//
    //------------------------------------------------------------------------------------------------
    //** 🥅 ATAJOS */
    private void startActivityFun(Class activityClass){
        Intent intent = new Intent(requireActivity(), activityClass);
        startActivity(intent);
    }
    private boolean updateFunctions() {
        if(connectState() != Constants.BLEState.ReadWriteOK) return false;
        prefs = setPrefs("health_prefs");
        String start = prefs.getString("health_start","");

        initClientFun();
        getHealthData();
        return true;
    }
    private SharedPreferences setPrefs(String prefsName) {
        return requireContext().getSharedPreferences(prefsName, Context.MODE_PRIVATE);
    }
    private void mostarLog(String msg){
        Log.d("HISTORIAL", msg);
    }/*
    *//** 🖼️ UI *//*
    //Configurar Boton de actualizar

    //Configurar Items con los Valores de Salud
    private void setHealthValue(String healthName, String defaultString, HealthItemView healthItemView,String unit){
        prefs = setPrefs("healthValuesHome");
        String lastHealthValue = prefs.getString(healthName, "");
        Log.d("HISTORIAL", healthName+" (set): "+lastHealthValue);

        String healthText;
        //Si esta vacio, se establece el patrón
        if (lastHealthValue.isEmpty()) {
            healthText = defaultString +" "+unit;
        }
        //Sino, se establece el último valor
        else {
            healthText = lastHealthValue +" "+unit;
        }
        healthItemView.setTime(healthText);
    }
    *//** 🔒 VALIDACIONES *//*
    *//** 🌊 BLUETOOTH *//*
    *//*private void connectToDevice() {
        SharedPreferences prefs = setPrefs("BLE_PREFS");
        String mac = prefs.getString("LAST_MAC", null);
        String state = prefs.getString("LAST_STATE", null);
        connectBle(mac, new BleConnectResponse() {
            @Override
            public void onConnectResponse(int code) {
                Log.d("BLE_SCAN", "Conexión código: " + code);
            }
        });
    }*//*
    *//** 🎲 DATA */
    private void updateHistoryData(HistoryData reg){
        Date date = new Date(reg.timestamp);
        Log.d("HISTORIAL_FECHA", String.valueOf(date));
        String timeStr = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date);
        //hora.setText(getString(R.string.ultima_actualizacion_sin_texto)+": "+timeStr);
        /*blood_item.setTime(reg.systolicValue+"/"+reg.diastolicValue+" mmHg");
        heart_rate_item.setTime(""+reg.heartValue+" lpm");
        frec_resp_item.setTime(""+reg.respRateValue+" rpm");
        tempeture_item.setTime(reg.tempIntValue+"."+reg.tempFloatValue+" °C");
        oxygen_item.setTime(""+reg.oxygenValue+" %");*/
    }
    //Obtener datos del HashMap
    private int getHealthValue(HashMap hashMap, String healthName){
        int healthValue = 0;
        Object heartValueObj = hashMap.get(healthName);
        if (heartValueObj != null) {
            healthValue = (int) heartValueObj;
        }
        return healthValue;
    }
    private String getHealthHistoryValue(HashMap r, String paramName){
        int value =  0;
        Object param = r.get(paramName);
        if(param != null){
            value =  (int) ((double) param);
        }
        String strValue = String.valueOf(value);
        return strValue;
    }
    private int getSportValue(HashMap hashMap, String sportValueName){
        int sportValue = 0;
        Object heartValueObj = hashMap.get(sportValueName);
        if (heartValueObj != null) {
            sportValue = (int) heartValueObj;
        }
        return sportValue;
    }
    private void updateProgress(int steps, int goalSteps) {
        goalSteps_sport.setText(String.valueOf(goalSteps));
        float stepsPct = (steps * 100f / goalSteps);
        circularProgress.setProgressWithAnimation(stepsPct);
        circularProgress.setProgressText(steps);
    }/*
    //--------------------------------------------------------------------------
    *//** DESARROLLO
    * *//*
    // Método para probar desde un botón
    private void testWorker(View view) {
        workManager = WorkManager.getInstance(requireContext());
        TextView btnTestWorker = view.findViewById(R.id.test_worker);
        initClientFun();
        btnTestWorker.setOnClickListener(v -> iniciarWorker());
    }
    private void iniciarWorker() {
        // Crear la request del Worker
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(HealthWorker.class)
                .setInitialDelay(0, TimeUnit.SECONDS) // Retardo inicial
                .addTag("WORKER_SYNC")
                .build();

        // Encolar el Worker
        workManager.enqueue(workRequest);

        // Observar el estado (opcional)
        workManager.getWorkInfoByIdLiveData(workRequest.getId())
                .observe(getViewLifecycleOwner(), workInfo -> {
                    if (workInfo != null) {
                        switch (workInfo.getState()) {
                            case ENQUEUED:
                                Log.d("WORKER_SYNC", "Worker en cola");
                                break;
                            case RUNNING:
                                Log.d("WORKER_SYNC", "Worker ejecutándose");
                                break;
                            case SUCCEEDED:
                                Log.d("WORKER_SYNC", "Worker completado exitosamente");
                                break;
                            case FAILED:
                                Log.d("WORKER_SYNC", "Worker falló");
                                break;
                            case CANCELLED:
                                Log.d("WORKER_SYNC", "Worker cancelado");
                                break;
                        }
                    }
                });
    }*/
}