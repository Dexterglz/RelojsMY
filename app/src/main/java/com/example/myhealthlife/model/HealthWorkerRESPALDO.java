package com.example.myhealthlife.model;

import static android.content.Context.MODE_PRIVATE;
import static com.example.myhealthlife.model.PrefsHelper.agregarHistorial;
import static com.yucheng.ycbtsdk.YCBTClient.appSengMessageToDevice;
import static com.yucheng.ycbtsdk.YCBTClient.deleteHealthHistoryData;
import static com.yucheng.ycbtsdk.YCBTClient.healthHistoryData;
import static com.yucheng.ycbtsdk.YCBTClient.initClient;
import static com.yucheng.ycbtsdk.YCBTClient.settingBloodOxygenModeMonitor;
import static com.yucheng.ycbtsdk.YCBTClient.settingHeartMonitor;
import static com.yucheng.ycbtsdk.YCBTClient.settingTemperatureMonitor;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.myhealthlife.R;
import com.example.myhealthlife.io.response.ApiClient;
import com.example.myhealthlife.io.response.ApiService;
import com.example.myhealthlife.io.response.HistorySendData;
import com.yucheng.ycbtsdk.Constants;
import com.yucheng.ycbtsdk.gatt.Reconnect;
import com.yucheng.ycbtsdk.response.BleDataResponse;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HealthWorkerRESPALDO extends Worker {
    public Integer savedInterval;
    SharedPreferences prefs;
    String userId;
    public HealthWorkerRESPALDO(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        //Obtener minutos del monitoreo
        prefs = getApplicationContext().getSharedPreferences("monitoring", MODE_PRIVATE);
        savedInterval = prefs.getInt("interval", 15);

        //Obtener id del usuario
        prefs = getApplicationContext().getSharedPreferences("MyApp", MODE_PRIVATE);
        userId = prefs.getString("user_id", "1"); // 1 es valor por defecto si no existe


        Log.d("WORKER","Ejecutando... Interval: "+savedInterval + " | user_id: "+userId);

        /*healthHistoryDataFun(getApplicationContext());
        //Obtenemos los últimos datos
        HistoryData h = leerHistorial(getApplicationContext()).get( leerHistorial(getApplicationContext()).size() - 1 );
        HistoryData.checkAndNotify(h,getApplicationContext());
        if(encontrarTimestamp(getApplicationContext(),h.timestamp) != -1){
            sendData(
                    h.heartValue,
                    h.oxygenValue,
                    h.diastolicValue,
                    h.systolicValue,
                    h.respRateValue,
                    h.bloodSugarValue,
                    h.tempIntValue,
                    h.tempFloatValue,
                    h.timestamp
            );
        }
        //deleteHistoryFun();
        appSengMessageToDevice(1, "MHL", getApplicationContext().getString(R.string.msg_sincronizando), new BleDataResponse() {
            @Override
            public void onDataResponse(int i, float v, HashMap hashMap) {

            }
        });*/
        // Si todo va bien
        return Result.success();
    }

    private void initClientFun(Context context){
        //Inicializa el YCBTClient
        initClient(
                context,   //contexto,
                true,                //Reconectar el dispositivo
                false                //Modo Debug
        );
        //Fuerza la reconexión con el reloj
        Reconnect.getInstance().init(
                context,
                true);
    }
    private void healthHistoryDataFun(Context context){
        //Recuperar los datos que se han recopilado de forma automática o de forma activa
        //Los datos no se borran, hasta que se llene la memoria del disposivio
        //resetQueue();
        healthHistoryData(
                Constants.DATATYPE.Health_HistoryAll,
                new BleDataResponse() {
                    @Override
                    public void onDataResponse(int dataType, float v, HashMap hashMap) {
                        Log.d("HOME", "ACTUALIZANDO... 📥 Dato recibido -> dataType: " + dataType + " | data: " + hashMap);
                        @SuppressWarnings("unchecked")
                        ArrayList<HashMap<String, Object>> dataList = (ArrayList<HashMap<String, Object>>) hashMap.get("data");
                        prefs = context.getSharedPreferences("monitoring", MODE_PRIVATE);
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
                            //healthMonitoringFun(savedInterval);
                            //agregarHistorial(context,registros, true,userId);


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
    }
    private void healthMonitoringFun(Integer min)
    {
        //El dispositivo medirá los datos correspondientes y los guardará
        //Corazón
        Log.d("MONITEREO: ",min+" min configurados");
        settingHeartMonitor(
                0x01,             //Forma automática
                min,                //Minutos de monitoreo
                new BleDataResponse() {
                    @Override
                    public void onDataResponse(int i, float v, HashMap hashMap) {
                        Log.d("HISTORIAL","settingHeartMonitor: "+min);
                    }
                });
        settingTemperatureMonitor(
                true,          //Monitoreo activado
                min,                //Minutos de monitoreo
                new BleDataResponse() {
                    @Override
                    public void onDataResponse(int i, float v, HashMap hashMap) {
                        Log.d("HISTORIAL","settingTemperatureMonitor: "+min);
                    }
                });
        settingBloodOxygenModeMonitor(
                true,          //Monitoreo activado
                min,               //Minutos de monitoreo
                new BleDataResponse() {
                    @Override
                    public void onDataResponse(int i, float v, HashMap hashMap) {
                        Log.d("HISTORIAL","settingBloodOxygenModeMonitor: "+min);
                    }
                });
    }
    private void deleteHistoryFun(){
        deleteHealthHistoryData(Constants.DATATYPE.Health_HistoryAll, new BleDataResponse() {
            @Override
            public void onDataResponse(int code, float v, HashMap hashMap) {
                if (code == 0) {
                    Log.d("HISTORIAL: ","Borrado existoramente");
                }
                else {
                    Log.d("HISTORIAL: ","Borrado falló");
                }
            }});
    }

    public void sendData(
            int heartValue,
            int oxygenValue,
            int diastolccValue,
            int systolicValue,
            int respRateValue,
            int bloodSugarValue,
            int tempIntValue,
            int tempFloatValue,
            long timestamp
    ){

        HistorySendData nuevoRegistro = new HistorySendData(
                userId,             // usuarioId
                heartValue,         // heartValue
                oxygenValue,        // oxygenValue
                diastolccValue,     // diastolicValue
                systolicValue,      // systolicValue
                respRateValue,      // respRateValue
                bloodSugarValue,    // bloodSugarValue
                tempIntValue,       // tempIntValue
                tempFloatValue,     // tempFloatValue
                timestamp           // timestampValue
        );

        // Realiza la llamada

        ApiService apiService = ApiClient.newClient().create(ApiService.class);
        Call<ResponseBody> call = apiService.agregarHistorial(nuevoRegistro);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("API", "Registro agregado exitosamente");
                    //Toast.makeText(getApplicationContext(), "Datos guardados", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        Log.e("API", "Error: " + response.errorBody().string());
                        Toast.makeText(getApplicationContext(), "Error: " + response.errorBody().string(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("API", "Fallo en la conexión: " + t.getMessage());
            }
        });
    }

}

