package com.example.myhealthlife.data.local.ble;

import static com.yucheng.ycbtsdk.Constants.DATATYPE.Real_UploadHeart;
import static com.yucheng.ycbtsdk.Constants.DATATYPE.Real_UploadSport;
import static com.yucheng.ycbtsdk.YCBTClient.appRealAllDataFromDevice;
import static com.yucheng.ycbtsdk.YCBTClient.appRealSportFromDevice;
import static com.yucheng.ycbtsdk.YCBTClient.appRegisterRealDataCallBack;
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

import com.example.myhealthlife.data.local.ble.sleep.SleepHistoryMapper;
import com.example.myhealthlife.data.local.ble.sleep.SleepSession;
import com.example.myhealthlife.data.local.entity.HistoryBloodEntity;
import com.example.myhealthlife.data.local.entity.HistoryCompEntity;
import com.example.myhealthlife.data.local.entity.HistoryHealthEntity;
import com.example.myhealthlife.data.local.entity.HistorySleepEntity;
import com.example.myhealthlife.data.local.entity.HistorySportEntity;
import com.example.myhealthlife.repository.HistoryDataRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yucheng.ycbtsdk.Constants;
import com.yucheng.ycbtsdk.gatt.Reconnect;
import com.yucheng.ycbtsdk.response.BleDataResponse;
import com.yucheng.ycbtsdk.response.BleRealDataResponse;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class BleCallback {
    public static void getAllHealthData(Context context){

        //Primero obtenemos los datos
        initClientFun(context);
        resetQueue();
        //Sí fuerza el monitoreo y lo hace más rápido
        healthMonitoringFunS(1,false, context);
        /*
        ## Problema:
        - History Sleep devuevlve null
        ## Hipotesis actual: LA APP CHINA MODIFICA EL COMPORTAMIENTO DE SLEEP
        1) getRealSportData afecta la recueración del historial de sueño (Descartada)
        2) Monitoreo altera la recuperación del historial del sueño
        ## Experimentos
        -) No abrir la app china cuando se recupere un nuevo dato de sueño
        x) Desactivar la función getRealSportData (fallido)
        x) Mover el monitore despues de llamar a la función (fallido)
        x) Desactivar la función del monitoreo (fallido)
        x) Mandar a llamar a a la función hasta que recupere un dato
        (fallido, craseho por llamarla varias veces)

        */
        AtomicInteger pendingTasks = new AtomicInteger(5);
        //Método para obtener el historial de sueño
        getHistorySleepData(context);
        //Método para obtener la presion arterial (por si el historial de salud no lo recupera)
        getHistoryBloodData(context);
        //Método para obtener grasas
        getHistoryComprehensiveData(context);
        //Método para obtener el historial de salud
        getHistoryHealthData(context);
        //Método para obtener Datos de Deporte en tiempo real
        getRealSportData(context);
        //Método para obtener el resto de datos (heart, oxygen, bloodP) en tiempo real
        getRealAllData(context);
        /*
        resetQueue(); -> Esta función resetea la cola de instrucciones, y evita el proceso
        de recuperacion del historial. NO se recomienda al final

        deleteHealthHistoryData -> Puede ser importante para la velocidad
        deleteHealthHistoryData(
                Health_DeleteAll,
                new BleDataResponse() {
                    @Override
                    public void onDataResponse(int i, float v, HashMap hashMap) {
                        Log.d("HISTORIAL","Borrando Historial...  code: "+ i+ " | v: "+v+" | hashMap:"+hashMap);
                    }
                });*/

    }

    //------ Métodos para recuperar datos del disposito ------
    public static void getHistorySleepData(Context context) {
        healthHistoryData(
                Constants.DATATYPE.Health_HistorySleep,
                new BleDataResponse() {
                    @Override
                    public void onDataResponse(int code, float v, HashMap hashMap) {
                        Log.d("Health_HistorySleep",
                                "ACTUALIZANDO... 📥 Dato recibido -> code: " + code +
                                        " | v: " + v +
                                        " | data: " + hashMap);

                        List<SleepSession> sessions =
                                SleepHistoryMapper.map(hashMap);

                        for (int i = 0; i < sessions.size(); i++) {
                            SleepSession s = sessions.get(i);

                            HistorySleepEntity reg = new HistorySleepEntity(
                                    s.startTime,
                                    s.endTime,
                                    s.wakeCount,
                                    s.wakeDuration,
                                    s.deepSleepTotal,
                                    s.lightSleepTotal,
                                    s.remTotal,
                                    false
                            );

                            HistoryDataRepository repository =
                                    new HistoryDataRepository(context.getApplicationContext());
                            repository.insertSleep(reg);
                        }
                    }
            }
        );
    }
    public static void getHistoryHealthData(Context context) {
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
                            int datosATomar = Math.min(totalDatos, totalDatos);
                            int rangoDatos = totalDatos - datosATomar;

                            for (int i = rangoDatos; i <= totalDatos - 1; i++) {
                                HashMap<String, Object> r = dataList.get(i);
                                int heartValue =            (int) getValue("heartValue", r);
                                int hrvValue =              (int) getValue("hrvValue", r);
                                int cvrrValue =             (int) getValue("cvrrValue", r);
                                int OOValue =               (int) getValue("OOValue", r);
                                int stepValue =             (int) getValue("stepValue", r);
                                int DBPValue =              (int) getValue("DBPValue", r);
                                int SBPValue =              (int) getValue("SBPValue", r);
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

                                if(i == totalDatos-1) {
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

                                HistoryHealthEntity reg = new HistoryHealthEntity(
                                        startTime       ,
                                        heartValue      ,
                                        hrvValue        ,
                                        cvrrValue       ,
                                        stepValue       ,
                                        OOValue         ,
                                        DBPValue        ,
                                        SBPValue        ,
                                        rrrValue        ,
                                        bfiValue        ,
                                        bffValue        ,
                                        bloodsValue     ,
                                        tempIntValue    ,
                                        tempFloatValue,
                                        false
                                );

                                HistoryDataRepository repository = new HistoryDataRepository(context.getApplicationContext());
                                repository.insertHealth(reg);

                            }

                        }
                        else {
                            Log.d("Health_HistoryAll", "No hay datos disponibles");
                        }
                    }
                });
    }
    private static void getHistoryComprehensiveData(Context context) {
        healthHistoryData(
                Constants.DATATYPE.Health_HistoryComprehensiveMeasureData,
                new BleDataResponse() {
                    @Override
                    public void onDataResponse(int code, float v, HashMap hashMap) {

                        Log.d("Health_HistoryComprehensiveMeasureData",
                                "ACTUALIZANDO... 📥 Dato recibido -> code: " + code +
                                        " | v: " + v +
                                        " | data: " + hashMap);

                        Type type = new TypeToken<ArrayList<HashMap<String, Object>>>() {}.getType();
                        ArrayList<HashMap<String, Object>> dataList =
                                new Gson().fromJson(
                                        new Gson().toJson(hashMap.get("data")),
                                        type
                                );

                        if (dataList != null && !dataList.isEmpty()) {

                            int totalDatos = dataList.size();
                            int rangoDatos = totalDatos - 1; // solo el último

                            for (int i = totalDatos - 1; i >= rangoDatos; i--) {

                                HashMap<String, Object> r = dataList.get(i);

                                int triCholInt =
                                        (int) getValue("triglycerideCholesterolInteger", r);
                                int triCholFloat =
                                        (int) getValue("triglycerideCholesterolFloat", r);

                                int hdlInt =
                                        (int) getValue("highLipoproteinCholesterolInteger", r);
                                int hdlFloat =
                                        (int) getValue("highLipoproteinCholesterolFloat", r);

                                int ldlInt =
                                        (int) getValue("lowLipoproteinCholesterolInteger", r);
                                int ldlFloat =
                                        (int) getValue("lowLipoproteinCholesterolFloat", r);

                                int cholesterolInt =
                                        (int) getValue("cholesterolInteger", r);
                                int cholesterolFloat =
                                        (int) getValue("cholesterolFloat", r);

                                int uricAcid =
                                        (int) getValue("uricAcid", r);

                                // ⚠️ Timestamp
                                long timestamp;
                                if (r.containsKey("startTime")) {
                                    timestamp = (long) getValue("startTime", r);
                                } else {
                                    // fallback seguro si BLE no lo manda
                                    timestamp = System.currentTimeMillis();
                                }

                                String timeStr =
                                        new SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
                                                .format(timestamp);

                                // Crear modelo
                                HistoryCompEntity reg =
                                        new HistoryCompEntity(
                                                triCholInt,
                                                triCholFloat,
                                                hdlInt,
                                                hdlFloat,
                                                ldlInt,
                                                ldlFloat,
                                                cholesterolInt,
                                                cholesterolFloat,
                                                uricAcid,
                                                timestamp,
                                                false
                                        );

                                HistoryDataRepository repository =
                                        new HistoryDataRepository(context.getApplicationContext());
                                repository.insertComp(reg);

                                // Log equivalente
                                /*Log.d("HISTORIAL_COMPREHENSIVE",
                                        "Registro #" + (i + 1) + ":\n" +
                                                "Triglicéridos: " + triCholInt + "." + triCholFloat + "\n" +
                                                "HDL: " + hdlInt + "." + hdlFloat + "\n" +
                                                "LDL: " + ldlInt + "." + ldlFloat + "\n" +
                                                "Colesterol total: " + cholesterolInt + "." + cholesterolFloat + "\n" +
                                                "Ácido úrico: " + uricAcid + "\n" +
                                                "Tiempo: " + timeStr + "\n" +
                                                "----------------------------------");*/
                            }
                        }
                        else {
                            Log.d("Health_HistoryComprehensiveMeasureData","No hay datos disponibles");
                        }
                    }
                }
        );

    }
    private static void getHistoryBloodData(Context context) {
        healthHistoryData(
                Constants.DATATYPE.Health_HistoryBlood,
                new BleDataResponse() {
                    @Override
                    public void onDataResponse(int code, float v, HashMap hashMap) {

                        Log.d("Health_HistoryBlood",
                                "ACTUALIZANDO... 📥 Dato recibido -> code: " + code +
                                        " | v: " + v +
                                        " | data: " + hashMap);

                        Type type = new TypeToken<ArrayList<HashMap<String, Object>>>() {}.getType();
                        ArrayList<HashMap<String, Object>> dataList =
                                new Gson().fromJson(
                                        new Gson().toJson(hashMap.get("data")),
                                        type
                                );

                        if (dataList != null && !dataList.isEmpty()) {

                            int totalDatos = dataList.size();
                            int rangoDatos = totalDatos - 1; // solo el último registro

                            for (int i = totalDatos - 1; i >= rangoDatos; i--) {

                                HashMap<String, Object> r = dataList.get(i);

                                int DBPValue =
                                        (int) getValue("bloodDBP", r);
                                int SBPValue =
                                        (int) getValue("bloodSBP", r);
                                long startTime =
                                        (long) getValue("bloodStartTime", r);

                                // Crear modelo Blood
                                HistoryBloodEntity reg =
                                        new HistoryBloodEntity(
                                                DBPValue,
                                                SBPValue,
                                                startTime,
                                                false
                                        );

                                HistoryDataRepository repository =
                                        new HistoryDataRepository(context.getApplicationContext());
                                repository.insertBlood(reg);

                                // Log equivalente
                                if(i == totalDatos-1){
                                    Log.d("HISTORIAL_BLOODDATA",
                                            "Registro #" + (i + 1) + ":\n" +
                                                    "Presión diastólica (DBP): " + DBPValue + "\n" +
                                                    "Presión sistólica (SBP): " + SBPValue + "\n" +
                                                    "Tiempo: " + startTime + "\n" +
                                                    "----------------------------------");
                                }
                            }
                        }
                        else {
                            Log.d("Health_HistoryBlood","No hay datos disponibles");
                        }
                    }
                }
        );
    }
    private static void getRealSportData(Context context) {
        appRealSportFromDevice(
                0x01,   //activo
                new BleDataResponse() {
                    @Override
                    public void onDataResponse(int code, float value, HashMap data) {
                        //Log.d("HISTORIAL", "appRealSportFromDevice" +"| code: "+code+" | data: "+ data);
                        appRegisterRealDataCallBack(new BleRealDataResponse() {
                                @Override
                                public void onRealDataResponse(int i, HashMap hashMap) {
                                    Log.d("Health_HistorySport", "hashMap: " + hashMap );
                                    if (hashMap != null && !hashMap.isEmpty()) {
                                        int sportStep = getIntValue(hashMap,"sportStep");             //step count
                                        int sportDistance = getIntValue(hashMap,"sportDistance");     //distance
                                        int sportCalorie = getIntValue(hashMap,"sportCalorie");       //calories

                                        Calendar cal = Calendar.getInstance();
                                        cal.set(Calendar.HOUR_OF_DAY, 0);
                                        cal.set(Calendar.MINUTE, 0);
                                        cal.set(Calendar.SECOND, 0);
                                        cal.set(Calendar.MILLISECOND, 0);
                                        long startTime = cal.getTimeInMillis();

                                        // Para comparar con la hora actual
                                        long now = System.currentTimeMillis();
                                        long diff = now - startTime; // milisegundos transcurridos hoy

                                        Log.d("sport","Inicio del día: " + startTime);
                                        Log.d("sport","Ahora: " + now);
                                        Log.d("sport","Milisegundos hoy: " + diff);

                                        // Crear modelo
                                        HistorySportEntity reg =
                                                new HistorySportEntity(
                                                        sportStep,
                                                        sportDistance,
                                                        sportCalorie,
                                                        startTime,
                                                        false
                                                );

                                        HistoryDataRepository repository =
                                                new HistoryDataRepository(context.getApplicationContext());
                                        repository.updateSport(reg);

                                    }
                                }
                            }
                        );
                    }
                }
        );
    }
    private static void getRealAllData(Context context) {
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
                                        int heart = getIntValue(hashMap,"heartValue");
                                        int oxygen = getIntValue(hashMap,"bloodOxygenValue");
                                        int bloodSBP = getIntValue(hashMap,"bloodSBP");
                                        int bloodDBP = getIntValue(hashMap,"bloodDBP");
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
    }

    //----------------- Métodos Auxiliares  ----------------
    private static int getIntValue(HashMap hashMap, String healthName){
        int healthValue = 0;
        Object heartValueObj = hashMap.get(healthName);
        if (heartValueObj != null) {
            healthValue = (int) heartValueObj;
        }
        return healthValue;
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
            if (startTimeObj instanceof Double &&
                    (
                            !Objects.equals(valueClave, "startTime") &&
                            !Objects.equals(valueClave, "bloodStartTime")
                    )
            ) {
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
            if (startTimeObj instanceof Double && (
                    valueClave.equals("startTime") ||
                    valueClave.equals("sleepStartTime") ||
                    valueClave.equals("bloodStartTime")
            )
            ) {
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

    //Iniciar el Cliente
    private static void initClientFun(Context context){
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
        setReconnect(true);
    }
    //Configurar Monitoreo
    private static void healthMonitoringFunS(int interval, Boolean showLog, Context context){

        // SI EL INTERVALO ES 0, SE USA EL INTERVALO GUARDADO EN SHARED_PREFERENCES
        SharedPreferences prefs = context.getSharedPreferences("monitoring", Context.MODE_PRIVATE);
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

}
