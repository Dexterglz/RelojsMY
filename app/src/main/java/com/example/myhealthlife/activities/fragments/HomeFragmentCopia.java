/* package com.example.myhealthlife.fragments;
import static android.content.Context.MODE_PRIVATE;
import static com.example.myhealthlife.model.DeviceAdapter.setDeviceImage;
import static com.example.myhealthlife.model.GetHistorial.getValue;
import static com.example.myhealthlife.model.HealthViewModel.setHealthInitialParams;
import static com.example.myhealthlife.model.HealthWorker.getUserId;
import static com.example.myhealthlife.model.MetallicTint.applyMetallicGradient;
import static com.example.myhealthlife.model.PrefsHelper.agregarHistorial;
import static com.example.myhealthlife.model.SportViewModel.setSportInitialParams;
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
import com.example.myhealthlife.activities.SleepActivity;
import com.example.myhealthlife.activities.TemperatureLogActivity;
import com.example.myhealthlife.model.common.AnimatedCircularProgress;

import com.example.myhealthlife.model.HealthInfoCardView;
import com.example.myhealthlife.model.HealthViewModel;
import com.example.myhealthlife.model.HealthWorker;
import com.example.myhealthlife.model.HistoryData;
import com.example.myhealthlife.model.SportViewModel;
import com.example.myhealthlife.model.TipoDato;
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
    private TextView steps_sport, kcal_sport, goalSteps_sport, distance_sport, last_update;
    private AnimatedCircularProgress circularProgress;
    private HealthInfoCardView blood_card, oxygen_card, heart_rate_card, tempeture_card, ecg_card, sleep_card, frec_resp_card,hr_hrv_card, hr_cvrr_card, sugar_card, tryg_card, acid_card ;
    private LinearLayout ble_icon;
    private boolean isLoading = false;
    SharedPreferences prefs;
    private BluetoothDialogFragment dialog;
    private SportViewModel viewModel;
    private HealthViewModel viewModelH;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d("HOME","Vista Creada");
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initViews(view);                                        //Iniciar Vistas
        healthGetters(view);                                     //Configurar los datos de las vistas
        /*testWorker(view);
        updateFunctions();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        healthMonitoringFun(1,false);
        getInitialData();
        updateFunctions();
    }
    @Override
    public void onStart() {
        super.onStart();
        healthMonitoringFun(1,false);
    }
    @Override
    public void onStop() {
        super.onStop();
        healthMonitoringFun(0,false);
    }

    //------------------------------------------------------------------------------------------------
    /**
     * PRINCIPALES
    
    private void initViews(@NonNull View view) {
        // SwipeRefreshLayout
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        int density = (int) getResources().getDisplayMetrics().density;
        swipeRefreshLayout.setSlingshotDistance(10 * density);
        swipeRefreshLayout.setProgressViewOffset(
                false,
                0,
                40 * density
        );
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

        // Marqueetador
        TextView tickerText = view.findViewById(R.id.tickerText);
        tickerText.setSelected(true);

        //last update
        last_update = view.findViewById(R.id.last_update);

        // Sport Views
        circularProgress = view.findViewById(R.id.circularProgress);
        steps_sport = view.findViewById(R.id.steps_sport);
        goalSteps_sport = view.findViewById(R.id.goalSteps_sport);
        kcal_sport = view.findViewById(R.id.kcal_sport);
        distance_sport = view.findViewById(R.id.distance_sport);

        // Steps Progreso
        SharedPreferences prefs = getContext().getSharedPreferences("sport_prefs", Context.MODE_PRIVATE);
        int lastgoalSteps = prefs.getInt("sport_goal_steps", 0);
        int lastSteps = prefs.getInt("sport_steps", 0);
        float stepsPct = (lastSteps * 100f / lastgoalSteps);
        circularProgress.setProgressWithAnimation(lastSteps);

        // Health Views --
        blood_card = view.findViewById(R.id.blood_card);
        oxygen_card = view.findViewById(R.id.oxygen_card);
        heart_rate_card = view.findViewById(R.id.heart_rate_card);
        tempeture_card = view.findViewById(R.id.tempeture_card);
        frec_resp_card = view.findViewById(R.id.frec_resp_card);
        hr_hrv_card = view.findViewById(R.id.hr_hrv_card);
        hr_cvrr_card = view.findViewById(R.id.hr_cvrr_card);
        sleep_card = view.findViewById(R.id.sleep_card);
        sugar_card = view.findViewById(R.id.sugar_card);
        tryg_card = view.findViewById(R.id.tryg_card);
        acid_card = view.findViewById(R.id.acid_card);
        ecg_card = view.findViewById(R.id.ecg_card);

        healthCards();

        //Bluetooth
        ble_icon = view.findViewById(R.id.ble_icon);
        dialog = new BluetoothDialogFragment();
        ble_icon.setOnClickListener(v -> {
            dialog.show(getChildFragmentManager(), "BluetoothDialogFragment");
        });

        //
        viewModel = new ViewModelProvider(requireActivity()).get(SportViewModel.class);
        viewModelH = new ViewModelProvider(requireActivity()).get(HealthViewModel.class);

    }

    /** HEALTH 
    private void healthCards() {

        ecg_card.configureCard(getContext(),TipoDato.ECG);
        blood_card.configureCard(getContext(),TipoDato.BLOOD_PRESSURE);
        frec_resp_card.configureCard(getContext(),TipoDato.RESP_RATE_VALUE);
        oxygen_card.configureCard(getContext(),TipoDato.OXYGEN_VALUE);
        heart_rate_card.configureCard(getContext(),TipoDato.HEART_VALUE);
        tempeture_card.configureCard(getContext(),TipoDato.TEMP);
        hr_hrv_card.configureCard(getContext(),TipoDato.HRV_VALUE);
        hr_cvrr_card.configureCard(getContext(),TipoDato.CVRR_VALUE);
        sleep_card.configureCard(getContext(),TipoDato.SLEEP);
        sugar_card.configureCard(getContext(),TipoDato.BLOOD_SUGAR_VALUE);
        tryg_card.configureCard(getContext(),TipoDato.TRYG);
        acid_card.configureCard(getContext(),TipoDato.URIC_ACIDE);

    }
    private void healthGetters(View view){
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
            kcal_sport.setText(calories+"");
        });
        viewModel.getSportDistance().observe(getViewLifecycleOwner(), distance -> {
            distance_sport.setText((float)distance/1000 + "");
        });

        //Health --
        viewModelH.getHealthBloodPressure().observe(getViewLifecycleOwner(), v -> {
            if(v==null){
                blood_card.setValue("--");
            }else{
                blood_card.setValue(v);
            }
        });
        viewModelH.getHealthHeart().observe(getViewLifecycleOwner(), v -> {
            if(v==null){
                heart_rate_card.setValue("--");
            }else{
                heart_rate_card.setValue(v+" ");
            }
        });
        viewModelH.getHealthRespRate().observe(getViewLifecycleOwner(), resp -> {
            if(resp==null){
                frec_resp_card.setValue("--");
            }else{
                frec_resp_card.setValue(resp+" ");
            }
        });
        viewModelH.getHealthTemp().observe(getViewLifecycleOwner(), v -> {
            if(v == null){
                tempeture_card.setValue("--");
            }else{
                tempeture_card.setValue(v+" ");
            }
        });
        viewModelH.getHealthOxygen().observe(getViewLifecycleOwner(), v -> {
            if(v == null){
                oxygen_card.setValue("--");
            }else{
                oxygen_card.setValue(v+" ");
            }
            //oxygen_item.setMainUnit(getString(R.string.percent),true);
        });
        viewModelH.getSleepDuration().observe(getViewLifecycleOwner(), v -> {
            if (v == null || v.trim().isEmpty()) {
                sleep_card.setValue("--");
                return;
            }

            try {
                String[] parts = v.split("\\.");

                if (parts.length != 2) {
                    sleep_card.setValue("--");
                    return;
                }

                int hours = Integer.parseInt(parts[0]);
                int minutes = Integer.parseInt(parts[1]);

                // Validación básica
                if (hours < 0 || minutes < 0 || minutes >= 60) {
                    sleep_card.setValue("--");
                    return;
                }

                sleep_card.setValue(hours + "hr " + minutes + "m");

            } catch (NumberFormatException e) {
                sleep_card.setValue("--");
            }
        });

        viewModelH.getHealthBloodSugar().observe(getViewLifecycleOwner(), v -> {
            if(v == null ) {
                sugar_card.setValue("--");
            }else{
                sugar_card.setValue(v);
            }
        });
        viewModelH.getCompTrigliceryd().observe(getViewLifecycleOwner(), v -> {
            if(v == null ) {
                tryg_card.setValue("--");
            }else{
                tryg_card.setValue(v);
            }
        });
        viewModelH.geyCompUricAcid().observe(getViewLifecycleOwner(), v -> {
            if(v == null ) {
                acid_card.setValue("--");
            }else{
                acid_card.setValue(v);
            }
        });
        viewModelH.getHealthHRV().observe(getViewLifecycleOwner(), v -> {
            if(v != null && !v.isEmpty()) {
                hr_hrv_card.setValue(v);
            }
        });
        viewModelH.getHealthCVVRR().observe(getViewLifecycleOwner(), v -> {
            if(v != null && !v.isEmpty()) {
                hr_cvrr_card.setValue(v);
            }
        });

        viewModelH.getHealthStartTime().observe(getViewLifecycleOwner(), v -> {
            if(v != null && !v.isEmpty()) {
                blood_card.setLastUpdate(getContext(),v);
                oxygen_card.setLastUpdate(getContext(),v);
                heart_rate_card.setLastUpdate(getContext(),v);
                tempeture_card.setLastUpdate(getContext(),v);
                frec_resp_card.setLastUpdate(getContext(),v);
                hr_hrv_card.setLastUpdate(getContext(),v);
                hr_cvrr_card.setLastUpdate(getContext(),v);
                sleep_card.setLastUpdate(getContext(),v);
                sugar_card.setLastUpdate(getContext(),v);
                tryg_card.setLastUpdate(getContext(),v);
                acid_card.setLastUpdate(getContext(),v);
                String timeStr = setLastUpdateFormatted(v);
                last_update.setText(getString(R.string.ultima_actualizacion_sin_texto)+" : "+timeStr);

            }
        });

        getInitialData();
    }
    private void getInitialData(){
        setSportInitialParams(getContext());
        setHealthInitialParams(getContext());
    }
    public String setLastUpdateFormatted(String timestamp) {
        try {
            // Intentar parsear como timestamp long
            long timestampLong = Long.parseLong(timestamp);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mma", Locale.getDefault());
            return sdf.format(new Date(timestampLong));
        }
        catch (NumberFormatException e) {
            return timestamp; // Devolver el string original
        }
    }

    /**
     *YCBT
    
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
        //healthMonitoringFun(1,false);

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

                                long hours = sleepDurationMinutes / 60;
                                long minutes = sleepDurationMinutes % 60;

                                // minutos como flotante (2 dígitos)
                                String horaStr = String.format(Locale.US, "%d.%02d", hours, minutes);
                                viewModelH.setSleepDuration(horaStr, getContext());

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

        healthHistoryData(
                Constants.DATATYPE.Health_HistoryComprehensiveMeasureData,
                new BleDataResponse() {
                    @Override
                    public void onDataResponse(int code, float v, HashMap hashMap) {
                        Log.d("Health_HistoryComprehensiveMeasureData", "ACTUALIZANDO... 📥 Dato recibido -> code: " + code + " | v: "+v+" | data: " + hashMap);
                        Type type = new TypeToken<ArrayList<HashMap<String, Object>>>() {}.getType();
                        ArrayList<HashMap<String, Object>> dataList = new Gson().fromJson(new Gson().toJson(hashMap.get("data")), type);
                        if (dataList != null && !dataList.isEmpty()){
                            int totalDatos = dataList.size();
                            int rangoDatos = totalDatos - 1; //En este caso solo tomaré el último arreglo

                            for (int i = totalDatos - 1; i >= rangoDatos; i--) {
                                HashMap<String, Object> r = dataList.get(i);
                                int triChorInt =            (int) getValue("triglycerideCholesterolInteger", r);
                                int triChorFloat =              (int) getValue("triglycerideCholesterolFloat", r);
                                int hdlInt =             (int) getValue("highLipoproteinCholesterolInteger", r);
                                int hdlFloat =               (int) getValue("highLipoproteinCholesterolFloat", r);
                                int ldlInt =              (int) getValue("lowLipoproteinCholesterolInteger", r);
                                int ldlFloat =              (int) getValue("lowLipoproteinCholesterolFloat", r);
                                int cholesterolInteger =             (int) getValue("cholesterolInteger", r);
                                int cholesterolFloat =              (int) getValue("cholesterolFloat", r);
                                int uricAcid =              (int) getValue("uricAcid", r);
                                //String startTimeStr = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(startTime);

                                //Actualizar Health --
                                if(triChorInt > 0) viewModelH.setComprehesive_triglycerideCholesterol(triChorInt+"."+triChorFloat, getContext());
                                if(cholesterolInteger > 0) viewModelH.setComprehesive_cholesterol(cholesterolInteger+"."+cholesterolFloat, getContext());
                                if(hdlInt > 0) viewModelH.setComprehesive_highLipoproteinCholesterol(hdlInt+"."+hdlFloat, getContext());
                                if(ldlInt > 0) viewModelH.setComprehesive_lowLipoproteinCholesterol(ldlInt+"."+ldlFloat, getContext());
                                if(uricAcid > 0) viewModelH.setComprehesive_uricAcid(uricAcid+"", getContext());

                                //*

                                //agregarHistorial(requireContext(),reg,false,getUserId(requireContext()));
                                /*Log.d("HISTORIAL_HEALTHDATA",
                                        "Registro #" + (i+1) + ":\n" +
                                                "Frecuencia cardíaca: " + r.get("heartValue") + "\n" +
                                                "HRV: " + r.get("hrvValue") + "\n" +
                                                "CVRR: " + r.get("cvrrValue") + "\n" +
                                                "PASOS: " + r.get("stepValue") + "\n" +
                                                "Oxígeno: " + r.get("OOValue") + "\n" +
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
                        else{
                            Log.d("HISTORIAL", "No hay datos disponibles");
                        }
                    }
                }
        );

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


                                //Actualizar Health --
                                if(heartValue > 0) viewModelH.sethealthHeart(""+heartValue, getContext());//*
                                if(hrvValue > 0)   viewModelH.sethealthHRV(""+hrvValue, getContext());//*
                                if(cvrrValue > 0)  viewModelH.sethealthCVVRR(""+cvrrValue, getContext());//*
                                if(stepValue > 0)  viewModelH.sethealthStep(""+stepValue, getContext());//*
                                if(OOValue > 0)    viewModelH.sethealthOxygen(""+ OOValue, getContext());//*
                                if(tempIntValue > 0)   viewModelH.sethealthTemp(tempIntValue+"."+tempFloatValue, getContext());//*
                                if(DBPValue > 0)   viewModelH.sethealthDBP(""+DBPValue, getContext());
                                if(SBPValue > 0)   viewModelH.sethealthSBP(""+SBPValue, getContext());
                                if(rrrValue > 0)   viewModelH.sethealthRespRate(""+rrrValue, getContext());
                                if(bloodsValue > 0)   viewModelH.setHealthBloodSugar(""+bloodsValue, getContext());

                                if(bfiValue > 0)   viewModelH.sethealthBody(bfiValue+"."+bffValue, getContext());
                                viewModelH.setHealthStartTime(String.valueOf(startTime), getContext());

                                /*HistoryData reg = new HistoryData(
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
                                        0,
                                        0,
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
                                                                int sportStep = getSportValue(hashMap,"sportStep");                  //step count
                                                                int sportDistance = getSportValue(hashMap,"sportDistance");     //distance
                                                                int sportCalorie = getSportValue(hashMap,"sportCalorie");       //calories
                                                                viewModel.setSportStep(sportStep, getContext());
                                                                viewModel.setSportDistance(sportDistance, getContext());
                                                                viewModel.setSportCalories(sportCalorie, getContext());
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
                                        //Nota: solo si el método anterior no funciona
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
        //NO AGREGAR ESTA FUNCION RESETEA LA COLA DE INSTRUCCIONES
        // resetQueue();
        //Los datos no se borran, hasta que se llene la memoria del disposivio

        /*
        Puede ser importante para la velocidad
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
        goalSteps_sport.setText(getString(R.string.de)+" " + goalSteps +" "+ getString(R.string.pasos));
        steps_sport.setText(""+steps);
        float stepsPct = (steps * 100f / goalSteps);
        circularProgress.setProgressWithAnimation(stepsPct);
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
    }
} */