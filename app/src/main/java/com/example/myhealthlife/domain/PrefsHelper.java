package com.example.myhealthlife.domain;

import static com.yucheng.ycbtsdk.YCBTClient.appSengMessageToDevice;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.myhealthlife.R;
import com.example.myhealthlife.io.response.ApiClient;
import com.example.myhealthlife.io.response.ApiService;
import com.example.myhealthlife.io.response.HistorySendData;
import com.example.myhealthlife.domain.util.NetworkUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yucheng.ycbtsdk.response.BleDataResponse;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrefsHelper {
    private static final String PREFS_NAME = "MisDatos";
    private static final String KEY_HISTORIAL = "historial_salud";

    public static void agregarHistorial(Context context, HistoryData historyData, Boolean enviarDatos, String usrId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();

        // Obtener historial actual
        String jsonActual = prefs.getString(KEY_HISTORIAL, null);
        Type tipoLista = new TypeToken<ArrayList<HistoryData>>() {
        }.getType();
        ArrayList<HistoryData> historialActual;

        if (jsonActual != null) {
            historialActual = gson.fromJson(jsonActual, tipoLista);
        } else {
            historialActual = new ArrayList<>();
        }

        //Obtener ultimo timestamp
        HistoryData lastSigns = historialActual.get( historialActual.size()-1 );
        long lastTimestamp = lastSigns.getTimestamp();

        //Obtener acutal timestamp
        long currentTimestamp = historyData.getTimestamp();

        //Solo agregará si es un nuevo timestamp
        if(currentTimestamp > lastTimestamp){
            Log.d("SYNC_WORKER",
                    "----- DATOS GUARDADOS ---- " +
                         "\n TIMESTAMP: "+ timeToString(currentTimestamp,"dd-MM-yy hh:mm a"));
            //Añadir al la lista del historial
            historialActual.add(historyData);
            // Guardar lista actualizada
            prefs.edit().putString(KEY_HISTORIAL, gson.toJson(historialActual)).apply();
            //Enviar los datos de la toma a la API

            //
            //
            // AQUI HACE FALTA AÑADIR UNA CONDICIÓN PARA QUE SOLO ENVIE LAS HORAS DEL MONITOREO,
            // Y EVITAR ASÍ LA SATURACIÓN DE DATOS
            //
            //

            String mins = timeToString(currentTimestamp,"mm");
            boolean validateTime = mins.equals("00") || mins.equals("15") || mins.equals("30") || mins.equals("45");

            if(enviarDatos && validateTime){
                Log.d("SYNC_WORKER","ENVIANDO ...");
                sendMsg(context);
            }
        }
    }

    private static String timeToString(long timestamp,String pattern) {
        Date date = new Date(timestamp);
        return new SimpleDateFormat(pattern, Locale.getDefault()).format(date);
    }

    public static ArrayList<HistoryData> obtenerHistorial(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_HISTORIAL, null);
        if (json != null) {
            Gson gson = new Gson();
            Type tipoLista = new TypeToken<ArrayList<HistoryData>>(){}.getType();
            return gson.fromJson(json, tipoLista);
        }
        return new ArrayList<>();
    }

    private static void sendMsg(Context context) {
        appSengMessageToDevice(1, "MHL", context.getString(R.string.msg_sincronizando), new BleDataResponse() {
            @Override
            public void onDataResponse(int i, float v, HashMap hashMap) {

            }
        });
    }

}

