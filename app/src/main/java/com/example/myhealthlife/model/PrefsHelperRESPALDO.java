package com.example.myhealthlife.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.myhealthlife.io.response.ApiClient;
import com.example.myhealthlife.io.response.ApiService;
import com.example.myhealthlife.io.response.HistorySendData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrefsHelperRESPALDO {
    private static final String PREFS_NAME = "MisDatos";
    private static final String KEY_HISTORIAL = "historial_salud";

    public static void guardarHistorial(Context context, ArrayList<HistoryData> registros) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        editor.putString(KEY_HISTORIAL, gson.toJson(registros));
        editor.apply();
    }

    public static void agregarHistorial(Context context, ArrayList<HistoryData> nuevosRegistros) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();

        // 1️⃣ Leer lista actual
        String jsonActual = prefs.getString(KEY_HISTORIAL, null);
        Type tipoLista = new TypeToken<ArrayList<HistoryData>>() {
        }.getType();
        ArrayList<HistoryData> listaActual;

        if (jsonActual != null) {
            listaActual = gson.fromJson(jsonActual, tipoLista);
        } else {
            listaActual = new ArrayList<>();
        }

        // 2️⃣ Agregar nuevos registros
        listaActual.addAll(nuevosRegistros);

        // 3️⃣ Guardar lista actualizada
        prefs.edit().putString(KEY_HISTORIAL, gson.toJson(listaActual)).apply();
    }

    public static void agregarSiNoExiste(Context context, ArrayList<HistoryData> nuevosRegistros, Boolean enviarDatos, String usrId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();

        // Leer historial actual
        String jsonActual = prefs.getString(KEY_HISTORIAL, null);
        Type tipoLista = new TypeToken<ArrayList<HistoryData>>() {
        }.getType();
        ArrayList<HistoryData> historialActual;

        if (jsonActual != null) {
            historialActual = gson.fromJson(jsonActual, tipoLista);
        } else {
            historialActual = new ArrayList<>();
        }

        //
        //
        //  EN ESTA PARTE HACE FALTA MODIFICAR PARA QUE EVALUE :
        //  SI EL TIEMPO DEL MONITOREO ACTUAL ES MAYOR AL ACTUAL
        //  SOLO ENTONCES LO AGREGUE
        //



        // Guardar todos los timestamps existentes para búsqueda rápida
        Set<Long> timestampsExistentes = new HashSet<>();
        for (HistoryData registro : historialActual) {
            timestampsExistentes.add(registro.getTimestamp()); // <-- usa tu getter real
        }

        // Agregar solo si el timestamp no existe
        for (HistoryData nuevo : nuevosRegistros) {
            if (!timestampsExistentes.contains(nuevo.getTimestamp())) {
                historialActual.add(nuevo);
                timestampsExistentes.add(nuevo.getTimestamp()); // evitar duplicados en la misma ejecución
                if(enviarDatos){
                    sendData(
                            usrId,
                            nuevo.heartValue,
                            nuevo.oxygenValue,
                            nuevo.diastolicValue,
                            nuevo.systolicValue,
                            nuevo.respRateValue,
                            nuevo.bloodSugarValue,
                            nuevo.tempIntValue,
                            nuevo.tempFloatValue,
                            nuevo.timestamp
                    );
                }
            }
        }

        // Guardar lista actualizada
        prefs.edit().putString(KEY_HISTORIAL, gson.toJson(historialActual)).apply();
    }

    public static ArrayList<HistoryData> leerHistorial(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_HISTORIAL, null);
        if (json != null) {
            Gson gson = new Gson();
            Type tipoLista = new TypeToken<ArrayList<HistoryData>>(){}.getType();
            return gson.fromJson(json, tipoLista);
        }
        return new ArrayList<>();
    }

    public static int encontrarTimestamp(Context context, long targetTimestamp) {
        ArrayList<HistoryData> history = leerHistorial(context);

        for (int i = 0; i < history.size(); i++) {
            if (history.get(i).getTimestamp() == targetTimestamp) {
                return i; // Found matching timestamp
            }
        }
        return -1; // Not found
    }

    public static void sendData(
            String userId,
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
                        //Toast.makeText(, "Error: " + response.errorBody().string(), Toast.LENGTH_SHORT).show();
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

