package com.example.myhealthlife.activities;

import static android.content.Context.MODE_PRIVATE;
import static com.realsil.sdk.core.preference.SharedPrefesHelper.getSharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MedicalRanges {
    private SharedPreferences prefs;
    public static String fechaString;
    static Integer edad;

    public MedicalRanges(Context context) {
        prefs = context.getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        fechaString = prefs.getString("user_date", "");
    }

    public static final int[] getGlucoseRange(int edad) {
        // [min_ayunas, max_ayunas, min_despues_comer, max_despues_comer]
        final int[] GLUCOSE_RANGE;

        if (edad >= 0 && edad <= 5) {
            // Bebés: 0-5 años - [100-180, máx 200, 100-200]
            GLUCOSE_RANGE = new int[]{100, 180, 0, 200};
        } else if (edad >= 6 && edad <= 12) {
            // Infantes: 6-12 años - [90-180, máx 200, 100-180]
            GLUCOSE_RANGE = new int[]{90, 180, 0, 200};
        } else if (edad >= 13 && edad <= 19) {
            // Adolescentes: 13-19 años - [90-130, máx 180, 90-150]
            GLUCOSE_RANGE = new int[]{90, 130, 0, 180};
        } else if (edad >= 20 && edad <= 35) {
            // Adultos jóvenes: 19-35 años - [90-130, máx 140, 90-150]
            GLUCOSE_RANGE = new int[]{90, 130, 0, 140};
        } else if (edad >= 36 && edad <= 60) {
            // Adultos: 35-60 años - [90-100, máx 150, 100-140]
            GLUCOSE_RANGE = new int[]{90, 100, 0, 150};
        } else if (edad > 60) {
            // Adultos mayores: más de 60 años - [80-110, máx 160, 100-140]
            GLUCOSE_RANGE = new int[]{80, 110, 0, 160};
        } else {
            // Edad inválida (negativa)
            GLUCOSE_RANGE = new int[]{0, 0, 0, 0};
        }

        return GLUCOSE_RANGE;
    }
    // Formato: [mín_normal, máx_normal, mín_alerta, máx_alerta]
    public static final int[] HEART_RATE = {60, 100, 50, 120};       // lpm
    public static final int[] HRV = {20, 100, 15, 150};               // ms
    public static final int[] CVRR = {2, 15, 1, 20};                  // %
    public static final int[] OXYGEN = {95, 100, 90, 101};            // %
    public static final int[] STEPS = {4000, 15000, 1000, 20000};     // pasos/día
    public static final int[] BLOOD_PRESSURE_SYSTOLIC = {90, 120, 70, 140};  // mmHg
    public static final int[] BLOOD_PRESSURE_DIASTOLIC = {60, 80, 50, 90};   // mmHg
    public static final int[] RESP_RATE = {12, 20, 8, 25};             // rpm
    public static final int[] BODY_FAT = {15, 25, 10, 30};            // % (hombres)
    public static final int[] BLOOD_SUGAR = getGlucoseRange(calcularEdad(fechaString));       // mg/dL (ayunas)
    public static final int[] TEMP = {36, 37, 35, 38};                // °C

    // Rangos para mujeres (ejemplo alternativo)
    public static final int[] BODY_FAT_FEMALE = {20, 30, 15, 35};

    public static int calcularEdad(String fechaNacimiento) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date nacimiento = sdf.parse(fechaNacimiento.substring(0, 10));

            Calendar calNac = Calendar.getInstance();
            calNac.setTime(nacimiento);
            Calendar calAhora = Calendar.getInstance();

            int edad = calAhora.get(Calendar.YEAR) - calNac.get(Calendar.YEAR);
            if (calAhora.get(Calendar.DAY_OF_YEAR) < calNac.get(Calendar.DAY_OF_YEAR)) {
                edad--;
            }
            return edad;
        } catch (Exception e) {
            return -1;
        }
    }

}
