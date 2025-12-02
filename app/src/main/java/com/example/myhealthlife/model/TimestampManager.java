package com.example.myhealthlife.model;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase para administrar timestamps en SharedPreferences
 */
public class TimestampManager {

    private static final String PREFS_NAME = "MisTimestampsPrefs";
    private static final String KEY_TIMESTAMPS = "lista_timestamps";

    /**
     * Guarda una lista completa de timestamps
     */
    public static void guardarTimestamps(Context context, List<Long> timestamps) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson(timestamps);

        editor.putString(KEY_TIMESTAMPS, json);
        editor.apply();
    }

    /**
     * Añade un nuevo timestamp a la lista existente
     */
    public static void agregarTimestamp(Context context, long nuevoTimestamp) {
        List<Long> timestamps = obtenerTimestamps(context);
        timestamps.add(nuevoTimestamp);
        guardarTimestamps(context, timestamps);
    }

    /**
     * Obtiene todos los timestamps guardados
     */
    public static List<Long> obtenerTimestamps(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_TIMESTAMPS, null);

        if (json != null) {
            Gson gson = new Gson();
            Type tipoLista = new TypeToken<ArrayList<Long>>(){}.getType();
            return gson.fromJson(json, tipoLista);
        }

        return new ArrayList<>();
    }

    /**
     * Busca si existe un timestamp específico
     */
    public static boolean contieneTimestamp(Context context, long timestampBuscado) {
        List<Long> timestamps = obtenerTimestamps(context);
        return timestamps.contains(timestampBuscado);
    }

    /**
     * Elimina un timestamp específico
     */
    public static void eliminarTimestamp(Context context, long timestampEliminar) {
        List<Long> timestamps = obtenerTimestamps(context);
        timestamps.remove(timestampEliminar);
        guardarTimestamps(context, timestamps);
    }

    /**
     * Elimina todos los timestamps guardados
     */
    public static void limpiarTimestamps(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_TIMESTAMPS);
        editor.apply();
    }

    public static int encontrarIndiceTimestamp(Context context, long timestampBuscado) {
        List<Long> timestamps = obtenerTimestamps(context);

        for (int i = 0; i < timestamps.size(); i++) {
            if (timestamps.get(i) == timestampBuscado) {
                return i; // Retorna el índice cuando lo encuentra
            }
        }

        return -1; // Retorna -1 si no encontró el timestamp
    }

}
