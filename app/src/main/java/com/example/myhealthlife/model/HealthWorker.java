package com.example.myhealthlife.model;

import static android.content.Context.MODE_PRIVATE;
import static com.example.myhealthlife.model.PrefsHelper.agregarHistorial;
import static com.example.myhealthlife.model.PrefsHelper.obtenerHistorial;
import static com.yucheng.ycbtsdk.YCBTClient.appSengMessageToDevice;
import static com.yucheng.ycbtsdk.YCBTClient.deleteHealthHistoryData;
import static com.yucheng.ycbtsdk.YCBTClient.healthHistoryData;
import static com.yucheng.ycbtsdk.YCBTClient.initClient;
import static com.yucheng.ycbtsdk.YCBTClient.resetQueue;
import static com.yucheng.ycbtsdk.YCBTClient.setReconnect;
import static com.yucheng.ycbtsdk.YCBTClient.settingBloodOxygenModeMonitor;
import static com.yucheng.ycbtsdk.YCBTClient.settingHeartMonitor;
import static com.yucheng.ycbtsdk.YCBTClient.settingTemperatureMonitor;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.ResourceManagerInternal;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.myhealthlife.R;
import com.example.myhealthlife.io.response.ApiClient;
import com.example.myhealthlife.io.response.ApiService;
import com.example.myhealthlife.io.response.HistorySendData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yucheng.ycbtsdk.Constants;
import com.yucheng.ycbtsdk.gatt.Reconnect;
import com.yucheng.ycbtsdk.response.BleDataResponse;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HealthWorker extends Worker {
    public Integer savedInterval;
    SharedPreferences prefs;
    String userId;
    public HealthWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }
    @NonNull
    @Override
    public Result doWork() {

        healthHistoryDataFun(getApplicationContext());                                    //Sincronizar datos
        checkSigns();                                                                     //Notificar si un signo vital es anormal

        // Si todo va bien
        return Result.success();
    }

    //----------------------------------------------------------------------------------------------

    /**
     * PRINCIPALES
     **/

    private void healthHistoryDataFun(Context context){
        //Recuperar los datos que se han recopilado de forma automática o de forma activa
        //initClientFun(context);
        resetQueue();
        healthMonitoringFun(0, false, context); //Primero probar así, y si tarda con un minuto
        //Método para obtener el historial de salud
        healthHistoryData(
                Constants.DATATYPE.Health_HistoryAll,
                new BleDataResponse() {
                    @Override
                    public void onDataResponse(int code, float v, HashMap hashMap) {
                        //Log.d("SYNC", "ACTUALIZANDO... 📥 Dato recibido -> code: " + code + " | v: "+v+" | data: " + hashMap);

                        if(hashMap == null) return;
                        Type type = new TypeToken<ArrayList<HashMap<String, Object>>>() {}.getType();
                        ArrayList<HashMap<String, Object>> dataList = new Gson().fromJson(new Gson().toJson(hashMap.get("data")), type);

                        if (dataList != null && !dataList.isEmpty()) {
                            int totalDatos = dataList.size();

                            for (int i = totalDatos - 1; i >= totalDatos-1; i--) {
                                HashMap<String, Object> signos = dataList.get(i);

                                //Log.d("SYNC","Fecha de Signos: "+ signos.get("startTime"));
                                HistoryData registros = new HistoryData(
                                    (int)   getValue("heartValue", signos),
                                    (int)   getValue("heartValue", signos),
                                    (int)   getValue("cvrrValue", signos),
                                    (int)   getValue("OOValue", signos),
                                    (int)   getValue("stepValue", signos),
                                    (int)   getValue("DBPValue", signos),
                                    (int)   getValue("SBPValue", signos),
                                    (int)   getValue("respiratoryRateValue", signos),
                                    (int)   getValue("bodyFatIntValue", signos),
                                    (int)   getValue("bodyFatFloatValue", signos),
                                    (int)   getValue("bloodSugarValue", signos),
                                    (int)   getValue("tempIntValue", signos) == 0 ? 35 : (int) getValue("tempIntValue", signos),
                                    (int)   getValue("tempFloatValue", signos),
                                    (long)  getValue("startTime", signos)

                                );
                                agregarHistorial(context,registros,false,getUserId(context));
                            }
                        }
                        else {
                            Log.d("SYNC", "No hay datos disponibles");
                        }
                    }
                });
    }

    private void checkSigns() {
        //Obtener los últimos datos
        ArrayList <HistoryData> lastHistory = obtenerHistorial(getApplicationContext());
        HistoryData lastData = lastHistory.get(lastHistory.size() - 1);
        //Noficar si algún signo es anormal
        HistoryData.checkAndNotify(lastData,getApplicationContext());
    }

    /** YCBT */
    private void healthMonitoringFun(int interval, Boolean showLog, Context context){

        // SI EL INTERVALO ES 0, SE USA EL INTERVALO GUARDADO EN SHARED_PREFERENCES

        prefs = setPrefs(context, "monitoring");
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
                        if(showLog) Log.d("SYNC","settingHeartMonitor: code: "+ i+ " |v: "+v + " |hashMap: " + hashMap );
                    }
                });
        settingTemperatureMonitor(
                true,          //Monitoreo activado
                min,                //Minutos de monitoreo
                new BleDataResponse() {
                    @Override
                    public void onDataResponse(int i, float v, HashMap hashMap) {
                        if(showLog) Log.d("SYNC","settingTemperatureMonitor: code: "+ i+ " |v: "+v + " |hashMap: " + hashMap );

                    }
                });
        settingBloodOxygenModeMonitor(
                true,          //Monitoreo activado
                min,               //Minutos de monitoreo
                new BleDataResponse() {
                    @Override
                    public void onDataResponse(int i, float v, HashMap hashMap) {
                        if(showLog) Log.d("SYNC","settingBloodOxygenModeMonitor: code: "+ i+ " |v: "+v + " |hashMap: " + hashMap );
                    }
                });
    }
    private void deleteHistoryFun(){
        deleteHealthHistoryData(Constants.DATATYPE.Health_HistoryAll, new BleDataResponse() {
            @Override
            public void onDataResponse(int code, float v, HashMap hashMap) {
                if (code == 0) {
                    Log.d("SYNC: ","Borrado existoramente");
                }
                else {
                    Log.d("SYNC: ","Borrado falló");
                }
            }});
    }

    //----------------------------------------------------------------------------------------------
    /**
     * AUXILIARES
     **/
    private SharedPreferences setPrefs(Context context, String prefsName) {
        return context.getSharedPreferences(prefsName, MODE_PRIVATE);
    }
    public static Number getValue(String valueClave, HashMap<String, Object> signos){
        Object startTimeObj = signos.get(valueClave);

        if (startTimeObj == null) {
            Log.e("SYNC", "⚠️ El valor '"+valueClave+"' es nulo. Se omite este registro.");
            return 0; // o 'continue;' si estás dentro de un bucle
        }

        try {
            if (startTimeObj instanceof Integer) {
                Log.d("SYNC", "⚠️ El valor '"+valueClave+"' es ENTERO");
                return ((Number) startTimeObj).intValue();
            }
            if (startTimeObj instanceof Long) {
                Log.d("SYNC", "⚠️ El valor '"+valueClave+"' es LONG");
                return ((Number) startTimeObj).longValue();
            }
            if (startTimeObj instanceof Double && !Objects.equals(valueClave, "startTime")) {
                Log.d("SYNC", "⚠️ El valor '"+valueClave+"' es DOUBLE");
                if(     Objects.equals(valueClave, "tempFloatValue")
                   ||   Objects.equals(valueClave, "bodyFatFloatValue") )
                {
                    double valor = (double) startTimeObj;
                    int parteDecimal = (int) (valor * 1);
                    Log.d("SYNC", "⚠️ VALOR '"+valueClave+"' CONVERTIDO A INT: "+parteDecimal);
                    return parteDecimal;
                }
                return ((Number) startTimeObj).intValue();
            }
            if (startTimeObj instanceof Double && valueClave.equals("startTime")) {
                Log.d("SYNC", "⚠️ El valor '"+valueClave+"' es DOUBLE pero es startTime");
                return ((Number) startTimeObj).longValue();
            }
            else {
                //Log.d("SYNC", "⚠️ Tipo inesperado para 'startTime': " + startTimeObj.getClass().getSimpleName());
                return 0;
            }
        } catch (Exception e) {
            Log.e("SYNC", "❌ Error al convertir 'startTime'", e);
            return 0;
        }

    }
    public static String getUserId(Context context) {
        //Obtener id del usuario
        SharedPreferences prefs = context.getSharedPreferences("MyApp", MODE_PRIVATE);
        String userId = prefs.getString("user_id", "1"); // 1 es valor por defecto si no existe
        //Log.d("SYNC","user_id: "+userId);
        return userId;
    }

}

