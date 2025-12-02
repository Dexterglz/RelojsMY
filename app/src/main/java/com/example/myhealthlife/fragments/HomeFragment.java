package com.example.myhealthlife.fragments;
import static android.content.Context.MODE_PRIVATE;
import static com.example.myhealthlife.model.DeviceAdapter.setDeviceImage;
import static com.example.myhealthlife.model.HealthWorker.getUserId;
import static com.example.myhealthlife.model.HealthWorker.getValue;
import static com.example.myhealthlife.model.MetallicTint.applyMetallicGradient;
import static com.example.myhealthlife.model.PrefsHelper.agregarHistorial;
import static com.yucheng.ycbtsdk.Constants.DATATYPE.Real_UploadHeart;
import static com.yucheng.ycbtsdk.YCBTClient.appRealAllDataFromDevice;
import static com.yucheng.ycbtsdk.YCBTClient.appRealSportFromDevice;
import static com.yucheng.ycbtsdk.YCBTClient.appRegisterRealDataCallBack;
import static com.yucheng.ycbtsdk.YCBTClient.appSengMessageToDevice;
import static com.yucheng.ycbtsdk.YCBTClient.appSleepWriteBack;
import static com.yucheng.ycbtsdk.YCBTClient.connectBle;
import static com.yucheng.ycbtsdk.YCBTClient.connectState;
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
import android.widget.ImageView;
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
import com.example.myhealthlife.model.HealthItemView;
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
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import androidx.lifecycle.ViewModelProvider;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class HomeFragment extends Fragment {
    private TextView kcal_sport, goalSteps_sport, distance_sport, hora;
    private AnimatedCircularProgress circularProgress;
    private HealthItemView heart_rate_item, frec_resp_item, tempeture_item, oxygen_item, blood_item;
    private AppCompatButton btnActualizar;
    private ProgressBar progressActualizar;
    private boolean isLoading = false;
    private ImageView sync_button, iconRight;
    SharedPreferences prefs;
    public Integer savedInterval;
    private boolean firstStartTime;
    private StepsDialogFragment dialog;
    private SportViewModel viewModel;
    private HealthViewModel viewModelH;
    private WorkManager workManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d("HOME","Vista Creada");
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        connectDevice();                                        //Conectar el disposito si está desconectado
        initViews(view);                                        //Iniciar Vistas
        configureButtons();                                     //Configurar las acciones de los botones
        setDashboard(view);                                     //Configurar los datos de las vistas
        setupUI(view);
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        healthMonitoringFun(1,false);
        super.onResume();
    }
    @Override
    public void onStart() {
        super.onStart();
        healthMonitoringFun(1,false);
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
        // Deporte
        viewModel = new ViewModelProvider(requireActivity()).get(SportViewModel.class);
        viewModelH = new ViewModelProvider(requireActivity()).get(HealthViewModel.class);
        kcal_sport = view.findViewById(R.id.kcal_sport);
        goalSteps_sport = view.findViewById(R.id.goalSteps_sport);
        distance_sport = view.findViewById(R.id.distance_sport);
        // Items
        heart_rate_item = view.findViewById(R.id.heart_rate_item);
        frec_resp_item = view.findViewById(R.id.frec_resp_item);
        tempeture_item = view.findViewById(R.id.tempeture_item);
        oxygen_item = view.findViewById(R.id.oxygen_item);
        blood_item = view.findViewById(R.id.blood_item);
        // Actualizar
        btnActualizar = view.findViewById(R.id.btnActualizar);
        sync_button = view.findViewById(R.id.sync_button);
        progressActualizar = view.findViewById(R.id.progressActualizar);
        hora = view.findViewById(R.id.txtHora);
        // Animaciones
        circularProgress = view.findViewById(R.id.circularProgress);
        TextView tickerText = view.findViewById(R.id.tickerText);
        // "Marqueetador"
        tickerText.setSelected(true);
        dialog = new StepsDialogFragment();
    }
    private void connectDevice(){
        if(connectState() == Constants.BLEState.Disconnect) {
            connectToDevice();
        }
    }
    public void configureButtons(){
        // Sports
        goalSteps_sport.setOnClickListener(v -> {
            dialog.show(getChildFragmentManager(), "StepsDialogFragment");
        });
        // Health Items
        heart_rate_item.setOnItemClickListener(v ->{buttonsValidation(HeartRateActivity.class);});
        frec_resp_item.setOnItemClickListener(v ->{buttonsValidation(RespiratoryRateActivity.class);});
        tempeture_item.setOnItemClickListener(v ->{buttonsValidation(TemperatureLogActivity.class);});
        oxygen_item.setOnItemClickListener(v -> {buttonsValidation(OxygenLogActivity.class);});
        blood_item.setOnItemClickListener(v -> {buttonsValidation(BloodPressureActivity.class);});
        btnActualizar.setOnClickListener(v -> {
            if (connectState() == Constants.BLEState.ReadWriteOK) {
                if(!isLoading){
                    startActualizar();
                }
                else{
                    Toast.makeText(requireContext(), getString(R.string.home_por_favor_espera), Toast.LENGTH_SHORT).show();
                }
                //startActualizar();
                return;
            }
            if (connectState() == Constants.BLEState.Disconnect) {
                Toast.makeText(requireContext(), getString(R.string.home_por_favor_conecte), Toast.LENGTH_SHORT).show();
            }
        });
        sync_button.setOnClickListener(v->{
            if (connectState() == Constants.BLEState.ReadWriteOK) {
                if(!isLoading){
                    startActualizar();
                }
                else{
                    Toast.makeText(requireContext(), getString(R.string.home_por_favor_espera), Toast.LENGTH_SHORT).show();
                }
                //startActualizar();
                return;
            }
            if (connectState() == Constants.BLEState.Disconnect) {
                Toast.makeText(requireContext(), getString(R.string.home_por_favor_conecte), Toast.LENGTH_SHORT).show();
            }
        });
    }
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
            blood_item.setTime(v+" "+getString(R.string.mmhg));
        });
        viewModelH.getHealthHeart().observe(getViewLifecycleOwner(), v -> {
            heart_rate_item.setTime(v+" "+getString(R.string.rpm));
        });
        viewModelH.getHealthRespRate().observe(getViewLifecycleOwner(), resp -> {
            frec_resp_item.setTime(resp+" "+getString(R.string.rpm));
        });
        viewModelH.getHealthTemp().observe(getViewLifecycleOwner(), v -> {
            tempeture_item.setTime(v+" "+getString(R.string.celsius));
        });
        viewModelH.getHealthOxygen().observe(getViewLifecycleOwner(), v -> {
            oxygen_item.setTime(v+" "+getString(R.string.percent));
        });
        viewModelH.getHealthStartTime().observe(getViewLifecycleOwner(), v -> {

            if(v != null && !v.isEmpty()) {
                String timeStr = getString(R.string.ultima_actualizacion_sin_texto)+": "+v;
                hora.setText(timeStr);
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
        String heart = prefs.getString("health_heart","");
        String resp = prefs.getString("health_resp","");
        String temp = prefs.getString("health_temp","");
        String oxy = prefs.getString("health_ox","");
        String blood = prefs.getString("health_blood","");
        String start = prefs.getString("health_start","");
        firstStartTime = true;
        viewModelH.sethealthRespRate(resp, getContext());
        viewModelH.sethealthHeart(heart, getContext());
        viewModelH.sethealthTemp(temp, getContext());
        viewModelH.sethealthOxygen(oxy, getContext());
        viewModelH.setHealthBloodPressure(blood, getContext());
        viewModelH.setHealthStartTime(start, getContext());
    }
    /**
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
                        Log.d("SLEEP", "ACTUALIZANDO... 📥 Dato recibido -> code: " + code + " | v: "+v+" | data: " + hashMap);
                        if(hashMap.isEmpty())
                        {
                            //Toast.makeText(requireContext(),"El historial de sueño es nulo", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            //Toast.makeText(requireContext(),"El historial de sueño NO ES NULO", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        //Método para obtener la presion arterial (si el método anterior falla)
        final int[] DBPVal = {0};
        final int[] SBPVal = {0};
        healthHistoryData(
                Constants.DATATYPE.Health_HistoryBlood,
                new BleDataResponse() {
                    @Override
                    public void onDataResponse(int code, float v, HashMap hashMap) {
                        Log.d("HISTORIAL_BLOOD", "ACTUALIZANDO... 📥 Dato recibido -> code: " + code + " | v: "+v+" | data: " + hashMap);

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
                });
        //Método para obtener el historial de salud
        healthHistoryData(
                Constants.DATATYPE.Health_HistoryAll,
                new BleDataResponse() {
                    @Override
                    public void onDataResponse(int code, float v, HashMap hashMap) {
                        Log.d("HISTORIAL", "ACTUALIZANDO... 📥 Dato recibido -> code: " + code + " | v: "+v+" | data: " + hashMap);

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
                                                            Log.d("HISTORIAL_SPORTS", "hashMap: " + hashMap );
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
                        //Log.d("HISTORIAL", "appRealAllDataFromDevice");
                        Log.d("REALDATA", "appRealAllDataFromDevice" +"| code: "+code+" | data: "+ data);
                        appRegisterRealDataCallBack(new BleRealDataResponse() {
                            @Override
                            public void onRealDataResponse(int i, HashMap hashMap) {
                                Log.d("HISTORIAL_HEALTH", "hashMap: " + hashMap );
                                if(i == Real_UploadHeart){
                                    if (hashMap != null && !hashMap.isEmpty()) {
                                        int heart = getHealthValue(hashMap,"heartValue");
                                        int oxygen = getHealthValue(hashMap,"bloodOxygenValue");
                                        int bloodSBP = getHealthValue(hashMap,"bloodSBP");
                                        int bloodDBP = getHealthValue(hashMap,"bloodDBP");
                                        //Log.d("HISTORIAL_Real_UploadHeart", "heart: " + heart);
                                        updateHealth("heartValue",heart,""+heart,heart_rate_item,"rpm");
                                        updateHealth("bloodOxygenValue",oxygen,""+oxygen,oxygen_item,"%");
                                        updateHealth("bloodSBP",bloodSBP,""+bloodSBP+"/"+bloodDBP,blood_item,"mmHg");
                                    }
                                }
                                /*if(code == Real_UploadSport){
                                    if (hashMap != null && !hashMap.isEmpty()) {

                                        sportStep = getAndUpdateSportValue(hashMap,"sportStep");                  //step count
                                        int sportDistance = getAndUpdateSportValue(hashMap,"sportDistance");     //distance
                                        int sportCalorie = getAndUpdateSportValue(hashMap,"sportCalorie");       //calories
                                        Log.d("HISTORIAL_Real_UploadSport", "sportStep: " + sportStep);

                                        viewModel.setSportStep(sportStep, getContext());
                                        viewModel.setSportDistance(sportDistance, getContext());
                                        viewModel.setSportCalories(sportCalorie, getContext());
                                    }
                                }*/
                            }});
                    }
                }
        );
        //resetQueue();
        //Los datos no se borran, hasta que se llene la memoria del disposivio
        /*deleteHealthHistoryData(
                Health_DeleteAll,
                new BleDataResponse() {
                    @Override
                    public void onDataResponse(int i, float v, HashMap hashMap) {
                        Log.d("HISTORIAL","Borrando Historial...  code: "+ i+ " | v: "+v+" | hashMap:"+hashMap);
                    }
                });*/
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
    /** AUXILIARES*/
    //------------------------------------------------------------------------------------------------
    /** 🥅 ATAJOS */
    private void startActivityFun(Class activityClass){
        Intent intent = new Intent(requireActivity(), activityClass);
        startActivity(intent);
    }
    private void updateFunctions() {
        initClientFun();
        getHealthData();
    }
    private SharedPreferences setPrefs(String prefsName) {
        return requireContext().getSharedPreferences(prefsName, Context.MODE_PRIVATE);
    }
    private void mostarLog(String msg){
        Log.d("HISTORIAL", msg);
    }
    /** 🖼️ UI */
    //Configurar Boton de actualizar
    private void startActualizar() {

        isLoading = true;
        btnActualizar.setEnabled(false);
        btnActualizar.setText("");                      // quita texto
        progressActualizar.setVisibility(View.VISIBLE); // muestra loader
        prefs = setPrefs("health_prefs");
        String start = prefs.getString("health_start","");
        viewModelH.setHealthStartTime(String.valueOf(start), getContext());

        // Lógica de sincronización (tu código)
        updateFunctions();
        Toast.makeText(getContext(),getString(R.string.home_actualizando), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> {
            isLoading = false;
            btnActualizar.setEnabled(true);
            btnActualizar.setText("Actualizar"); // Tu texto original
            progressActualizar.setVisibility(View.GONE);
        }, 10000); // 2 segundos
    }
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
    private void updateHealthValue(String healthName, String healthValueString, HealthItemView healthItemView, String unit){
        prefs = setPrefs("healthValuesHome");
        prefs.edit().putString(healthName, healthValueString).apply();
        //Log.d("HISTORIAL", healthName+" (put): " + healthValueString + " en "+ healthName) ;
        String healthText = healthValueString +" "+unit;
        healthItemView.setTime(healthText);
    }
    /** 🔒 VALIDACIONES */
    public void buttonsValidation(Class activity){
        /*if(obtenerHistorial(getContext()).isEmpty()){
            Toast.makeText(requireContext(), getString(R.string.no_hay_datos), Toast.LENGTH_SHORT).show();
        }
        else{*/
            startActivityFun(activity);
        //}
    }
    /** 🌊 BLUETOOTH */
    private void connectToDevice() {
        SharedPreferences prefs = setPrefs("BLE_PREFS");
        String mac = prefs.getString("LAST_MAC", null);
        String state = prefs.getString("LAST_STATE", null);
        connectBle(mac, new BleConnectResponse() {
            @Override
            public void onConnectResponse(int code) {
                Log.d("BLE_SCAN", "Conexión código: " + code);
            }
        });
    }
    /** 🎲 DATA */
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
    private void updateHealth(String healthName, int healthValue,String healthValueString,HealthItemView healthItemView, String unit){
        if(healthValue > 0){
            updateHealthValue(healthName, healthValueString, healthItemView, unit);
        }
    }
    private void updateProgress(int steps, int goalSteps) {
        goalSteps_sport.setText(String.valueOf(goalSteps));
        float stepsPct = (steps * 100f / goalSteps);
        circularProgress.setProgressWithAnimation(stepsPct);
        circularProgress.setProgressText(steps);
    }
    //--------------------------------------------------------------------------
    /** DESARROLLO
    * */
    // Método para probar desde un botón
    private void setupUI(View view) {
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


}