package com.example.myhealthlife.model;

import static com.example.myhealthlife.activities.MedicalRanges.calcularEdad;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.TextView;

import com.example.myhealthlife.R;

public class TablaGlucemiaHelper {
    private static SharedPreferences prefs;
    public static String fechaString;

    public static void setGlucosaTabla(Context context, View view){
        prefs = context.getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        fechaString = prefs.getString("user_date", "");
        actualizarTablaGlucemia(calcularEdad(fechaString), view);       // mg/dL (ayunas)
    };


    // Clase para almacenar los datos de cada grupo etario
    public static class DatosGlucemia {
        public String grupoEtario;
        public String enAyunas;
        public String despuesComida;
        public String antesDormir;

        public DatosGlucemia(String grupo, String ayunas, String comida, String dormir) {
            this.grupoEtario = grupo;
            this.enAyunas = ayunas;
            this.despuesComida = comida;
            this.antesDormir = dormir;
        }
    }

    // Base de datos de valores por edad
    public static DatosGlucemia getDatosPorEdad(int edad) {
        if (edad >= 0 && edad <= 5) {
            return new DatosGlucemia(
                    "Bebés:\n0-5 años de edad",
                    "100-180 mg/dl",
                    "Máximo 200 mg/dl",
                    "100-200 mg/dl"
            );
        } else if (edad >= 6 && edad <= 12) {
            return new DatosGlucemia(
                    "Infantes:\n6-12 años de edad",
                    "90-150 mg/dl",
                    "Máximo 180 mg/dl",
                    "90-160 mg/dl"
            );
        } else if (edad >= 13 && edad <= 19) {
            return new DatosGlucemia(
                    "Adolescentes:\n13-19 años de edad",
                    "80-130 mg/dl",
                    "Máximo 160 mg/dl",
                    "80-150 mg/dl"
            );
        } else if (edad >= 20 && edad <= 35) {
            return new DatosGlucemia(
                    "Adultos jóvenes:\n20-35 años de edad",
                    "70-110 mg/dl",
                    "Máximo 140 mg/dl",
                    "70-120 mg/dl"
            );
        } else if (edad >= 36 && edad <= 60) {
            return new DatosGlucemia(
                    "Adultos:\n36-60 años de edad",
                    "70-110 mg/dl",
                    "Máximo 140 mg/dl",
                    "70-120 mg/dl"
            );
        } else {
            return new DatosGlucemia(
                    "Adultos mayores:\nMás de 60 años",
                    "80-130 mg/dl",
                    "Máximo 160 mg/dl",
                    "80-150 mg/dl"
            );
        }
    }

    private static void actualizarTablaGlucemia(int edadUsuario, View view) {
        // Obtener datos según la edad
        TablaGlucemiaHelper.DatosGlucemia datos = TablaGlucemiaHelper.getDatosPorEdad(edadUsuario);

        // Actualizar los TextViews usando getView() para encontrar las vistas
        TextView textViewGrupo = view.findViewById(R.id.textViewGrupo);
        TextView textViewAyunas = view.findViewById(R.id.textViewAyunas);
        TextView textViewDespuesComida = view.findViewById(R.id.textViewDespuesComida);
        TextView textViewAntesDormir = view.findViewById(R.id.textViewAntesDormir);

        if (textViewGrupo != null) {
            textViewGrupo.setText(datos.grupoEtario);
        }
        if (textViewAyunas != null) {
            textViewAyunas.setText(datos.enAyunas);
        }
        if (textViewDespuesComida != null) {
            textViewDespuesComida.setText(datos.despuesComida);
        }
        if (textViewAntesDormir != null) {
            textViewAntesDormir.setText(datos.antesDormir);
        }
    }
}
