package com.example.myhealthlife.fragments;
import static android.content.Context.MODE_PRIVATE;
import static com.example.myhealthlife.model.MetallicTint.applyMetallicGradient;
import static com.example.myhealthlife.model.PrefsHelper.agregarHistorial;
import static com.example.myhealthlife.model.PrefsHelper.obtenerHistorial;
import static com.yucheng.ycbtsdk.Constants.DATATYPE.Health_DeleteAll;
import static com.yucheng.ycbtsdk.YCBTClient.appSengMessageToDevice;
import static com.yucheng.ycbtsdk.YCBTClient.connectBle;
import static com.yucheng.ycbtsdk.YCBTClient.connectState;
import static com.yucheng.ycbtsdk.YCBTClient.deleteHealthHistoryData;
import static com.yucheng.ycbtsdk.YCBTClient.healthHistoryData;
import static com.yucheng.ycbtsdk.YCBTClient.initClient;
import static com.yucheng.ycbtsdk.YCBTClient.setReconnect;
import static com.yucheng.ycbtsdk.YCBTClient.settingBloodOxygenModeMonitor;
import static com.yucheng.ycbtsdk.YCBTClient.settingHeartMonitor;
import static com.yucheng.ycbtsdk.YCBTClient.settingTemperatureMonitor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.example.myhealthlife.R;
import com.example.myhealthlife.activities.BloodPressureActivity;
import com.example.myhealthlife.activities.HeartRateActivity;
import com.example.myhealthlife.activities.OxygenLogActivity;
import com.example.myhealthlife.activities.RespiratoryRateActivity;
import com.example.myhealthlife.activities.TemperatureLogActivity;
import com.example.myhealthlife.activities.TestActivity;
import com.example.myhealthlife.model.AnimatedCircularProgress;
import com.example.myhealthlife.model.HealthItemView;
import com.example.myhealthlife.model.HistoryData;
import com.yucheng.ycbtsdk.Constants;
import com.yucheng.ycbtsdk.gatt.Reconnect;
import com.yucheng.ycbtsdk.response.BleConnectResponse;
import com.yucheng.ycbtsdk.response.BleDataResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class HomeFragmentCopia extends Fragment {
    private View actualizar;
    private HealthItemView heart_rate_item, frec_resp_item, tempeture_item, oxygen_item, blood_item;
    private AppCompatButton btnActualizar;
    private ProgressBar progressActualizar;
    private boolean isLoading = false;
    SharedPreferences prefs;
    public Integer savedInterval;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d("HOME","Vista Creada");
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //Inicializar SharedPreferences
        prefs = requireActivity().getSharedPreferences("monitoring", MODE_PRIVATE);
        savedInterval = prefs.getInt("interval", 15);

        initViews(view);                                        //Iniciar Vistas
        configureData();                                        //Configurar los datos de las vistas
        connectDevice();                                        //Conectar el disposito si está desconectado
        updateDashboard(view);

        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        healthMonitoringFun(1);
        super.onResume();
    }
    @Override
    public void onStart() {
        super.onStart();
        healthMonitoringFun(1);
    }
    @Override
    public void onStop() {
        super.onStop();
        healthMonitoringFun(savedInterval);
    }

    /*----------------------------------------------------*/
    /**
     * PRINCIPALES
     **/
    private void initViews(@NonNull View view) {
        // Botones
        heart_rate_item = view.findViewById(R.id.heart_rate_item);
        frec_resp_item = view.findViewById(R.id.frec_resp_item);
        tempeture_item = view.findViewById(R.id.tempeture_item);
        oxygen_item = view.findViewById(R.id.oxygen_item);
        blood_item = view.findViewById(R.id.blood_item);
        // Actualizar Datos
        btnActualizar = view.findViewById(R.id.btnActualizar);
        progressActualizar = view.findViewById(R.id.progressActualizar);
        //Animaciones
        AnimatedCircularProgress circularProgress = view.findViewById(R.id.circularProgress);
        // Inicia animación del progreso
        circularProgress.setProgressWithAnimation(75f); // por ejemplo, 75%
        TextView tickerText = view.findViewById(R.id.tickerText);
        // Necesario para que el marquee funcione incluso sin focus
        tickerText.setSelected(true);
    }
    private void connectDevice(){
        if(connectState() == Constants.BLEState.Disconnect) {
            connectToDevice();
        }
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
    //Obtener Historial
    private void healthHistoryDataFun(){
        //Recuperar los datos que se han recopilado de forma automática o de forma activa
        //Los datos no se borran, hasta que se llene la memoria del disposivio
        //resetQueue();
        healthHistoryData(
                Constants.DATATYPE.Health_HistoryAll,
                new BleDataResponse() {
                    @Override
                    public void onDataResponse(int code, float v, HashMap hashMap) {
                        Log.d("HISTORIAL", "ACTUALIZANDO... 📥 Dato recibido -> code: " + code + "| v: "+v+" | data: " + hashMap);
                        @SuppressWarnings("unchecked")
                        ArrayList<HashMap<String, Object>> dataList = (ArrayList<HashMap<String, Object>>) hashMap.get("data");
                        if (dataList != null && !dataList.isEmpty()) {
                            int totalRegistros = dataList.size();
                            int registrosATomar = Math.min(totalRegistros, 300);
                            ArrayList<HistoryData> registros = new ArrayList<>();

                            for (int i = totalRegistros - 1; i >= totalRegistros - registrosATomar; i--) {
                                HashMap<String, Object> r = dataList.get(i);
                                Log.d("REG","reg Date: "+ r.get("startTime"));
                                registros.add(new HistoryData(
                                        (int) r.get("heartValue"),
                                        (int) r.get("hrvValue"),
                                        (int) r.get("cvrrValue"),
                                        (int) r.get("OOValue"),
                                        (int) r.get("stepValue"),
                                        (int) r.get("DBPValue"),
                                        (int) r.get("SBPValue"),
                                        (int) r.get("respiratoryRateValue"),
                                        (int) r.get("bodyFatIntValue"),
                                        (int) r.get("bodyFatFloatValue"),
                                        (int) r.get("bloodSugarValue"),
                                        (int) r.get("tempIntValue"),
                                        (int) r.get("tempFloatValue"),
                                        (long) r.get("startTime")
                                ));

                            }
                            healthMonitoringFun(savedInterval);
                            //agregarHistorial(requireContext(),registros, false,"1");
                            updateHistoryData(registros.get(0));

                            Log.d("HISTORIAL", "--- ÚLTIMOS " + registrosATomar + " REGISTROS ---");
                            for (int i = 0; i < registrosATomar; i++) {
                                HistoryData reg = registros.get(i);
                                Date date = new Date(reg.timestamp);
                                String dateStr = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(date);
                                String timeStr = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date);

                                Log.d("HISTORIAL",
                                        "Registro #" + (i+1) + ":\n" +
                                                "Frecuencia cardíaca: " + reg.heartValue + "\n" +
                                                "HRV: " + reg.hrvValue + "\n" +
                                                "CVRR: " + reg.cvrrValue + "\n" +
                                                "Oxígeno: " + reg.oxygenValue + "\n" +
                                                "Pasos: " + reg.stepValue + "\n" +
                                                "Presión diastólica: " + reg.diastolicValue + "\n" +
                                                "Presión sistólica: " + reg.systolicValue + "\n" +
                                                "Frecuencia respiratoria: " + reg.respRateValue + "\n" +
                                                "Grasa corporal: " + reg.bodyFatValue + "." + reg.bodyFatFracValue + "\n" +
                                                "Glucosa: " + reg.bloodSugarValue + "\n" +
                                                "Temperatura: " + reg.tempIntValue + "." + reg.tempFloatValue + "\n" +
                                                "Tiempo: " + dateStr + " " + timeStr + "\n" +
                                                "----------------------------------");
                            }
                            Log.d("HISTORIAL", "Total de registros mostrados: " + registrosATomar);
                        }
                        else {
                            Log.d("HISTORIAL", "No hay datos disponibles");
                        }
                    }
                }
        );
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
    private void healthMonitoringFun(Integer min){
        //El dispositivo medirá los datos correspondientes y los guardará
        //Corazón
        Log.d("MONITEREO: ",min+" min configurados");
        settingHeartMonitor(
                0x01,           //Forma automática
                min,                //Minutos de monitoreo
                new BleDataResponse() {
                    @Override
                    public void onDataResponse(int i, float v, HashMap hashMap) {
                        Log.d("HISTORIAL","settingHeartMonitor: code: "+ i+ " |v: "+v + " |hashMap: " + hashMap );
                    }
                });
        settingTemperatureMonitor(
                true,          //Monitoreo activado
                min,                //Minutos de monitoreo
                new BleDataResponse() {
                    @Override
                    public void onDataResponse(int i, float v, HashMap hashMap) {
                        Log.d("HISTORIAL","settingTemperatureMonitor: code: "+ i+ " |v: "+v + " |hashMap: " + hashMap );

                    }
                });
        settingBloodOxygenModeMonitor(
                true,          //Monitoreo activado
                min,               //Minutos de monitoreo
                new BleDataResponse() {
                    @Override
                    public void onDataResponse(int i, float v, HashMap hashMap) {
                        Log.d("HISTORIAL","settingBloodOxygenModeMonitor: code: "+ i+ " |v: "+v + " |hashMap: " + hashMap );
                    }
                });
    }
    /**
     * AUXILIARES
     **/
    //🥅 ATAJOS
    private void startActivityFun(Class activityClass){
        Intent intent = new Intent(requireActivity(), activityClass);
        startActivity(intent);
    }
    //🖼️ UI
    private void startActualizar() {
        isLoading = true;
        btnActualizar.setEnabled(false);
        btnActualizar.setText("");                      // quita texto
        progressActualizar.setVisibility(View.VISIBLE); // muestra loader

        // Lógica de sincronización (tu código)
        updateFunctions();
        Toast.makeText(getContext(),getString(R.string.home_actualizando), Toast.LENGTH_SHORT).show();
        appSengMessageToDevice(1, "MHL", getString(R.string.msg_sincronizando), new BleDataResponse() {
            @Override
            public void onDataResponse(int i, float v, HashMap hashMap) {
                // si recibes confirmación desde el dispositivo, puedes llamar stopActualizar()
            }
        });

    }
    public void configureData(){
        heart_rate_item.setOnItemClickListener(v ->{buttonsValidation(HeartRateActivity.class);});
        heart_rate_item.setTime("-- lpm");
        frec_resp_item.setOnItemClickListener(v ->{buttonsValidation(RespiratoryRateActivity.class);});
        frec_resp_item.setTime("-- rpm");
        tempeture_item.setOnItemClickListener(v ->{buttonsValidation(TemperatureLogActivity.class);});
        tempeture_item.setTime("--.- °C");
        oxygen_item.setOnItemClickListener(v -> buttonsValidation(OxygenLogActivity.class));
        oxygen_item.setTime("-- %");
        blood_item.setOnItemClickListener(v -> buttonsValidation(BloodPressureActivity.class));
        blood_item.setTime("--/-- mmHg");
        btnActualizar.setOnClickListener(v -> {
            if (connectState() == Constants.BLEState.ReadWriteOK) {
                if(!isLoading){
                    startActualizar();
                }
                else{
                    Toast.makeText(requireContext(), getString(R.string.home_por_favor_espera), Toast.LENGTH_SHORT).show();
                }
                return;
            }
            if (connectState() == Constants.BLEState.Disconnect) {
                Toast.makeText(requireContext(), getString(R.string.home_por_favor_conecte), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void buttonsValidation(Class activity){
        if(obtenerHistorial(getContext()).isEmpty()){
            Toast.makeText(requireContext(), getString(R.string.no_hay_datos), Toast.LENGTH_SHORT).show();
        }
        else{
            startActivityFun(activity);
        }
    }
    private void updateFunctions() {
        initClientFun();
        healthHistoryDataFun();
    }
    private void updateHistoryData(HistoryData reg){
        Date date = new Date(reg.timestamp);
        Log.d("HISTORIAL_FECHA", String.valueOf(date));
        String timeStr = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date);
        //hora.setText(getString(R.string.ultima_actualizacion_sin_texto)+": "+timeStr);
        blood_item.setTime(reg.systolicValue+"/"+reg.diastolicValue+" mmHg");
        heart_rate_item.setTime(""+reg.heartValue+" lpm");
        frec_resp_item.setTime(""+reg.respRateValue+" rpm");
        tempeture_item.setTime(reg.tempIntValue+"."+reg.tempFloatValue+" °C");
        oxygen_item.setTime(""+reg.oxygenValue+" %");
    }
    private void updateDashboard(View view){
        if(!obtenerHistorial(view.getContext()).isEmpty())
        {updateHistoryData(obtenerHistorial(view.getContext()).get(
                obtenerHistorial(view.getContext()).size()-1
        ));}
    }
    //🌊 Bluetooth
    private void connectToDevice() {
        SharedPreferences prefs = requireContext().getSharedPreferences("BLE_PREFS", Context.MODE_PRIVATE);
        String mac = prefs.getString("LAST_MAC", null);
        String state = prefs.getString("LAST_STATE", null);
        connectBle(mac, new BleConnectResponse() {
            @Override
            public void onConnectResponse(int code) {
                Log.d("BLE_SCAN", "Conexión código: " + code);
            }
        });
    }
    public void testAPI(){
        Intent intent = new Intent(requireActivity(), TestActivity.class);
        startActivity(intent);
    }
}