package com.example.myhealthlife.model;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;

import com.example.myhealthlife.R;

public class NetworkModeDialog {

    public static void showNetworkModeDialog(final android.content.Context context) {
        // Obtener la preferencia actual
        SharedPreferences prefs = context.getSharedPreferences("MyApp", android.content.Context.MODE_PRIVATE);
        final String currentMode = prefs.getString("network_mode", "wifi_only");

        // Array de opciones
        final String[] modes = {context.getString(R.string.solo_wifi), context.getString(R.string.cualquier_red)};
        final String[] values = {"wifi_only", "any"};

        // Encontrar el índice seleccionado actualmente
        int selectedIndex = 0; // Por defecto wifi_only
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(currentMode)) {
                selectedIndex = i;
                break;
            }
        }

        // Crear el diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.seleccionar_modo_red));
        builder.setSingleChoiceItems(modes, selectedIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Guardar la preferencia inmediatamente al seleccionar
                SharedPreferences prefs = context.getSharedPreferences("MyApp", android.content.Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("network_mode", values[which]);
                editor.apply();

                // Cerrar el diálogo después de seleccionar
                dialog.dismiss();

                // Opcional: Mostrar un mensaje de confirmación
                //String message = (which == 0) ? "Modo WiFi solamente" : "Modo cualquier red";
                //android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show();
            }
        });

        // Botón cancelar (opcional)
        builder.setNegativeButton(context.getString(R.string.cancelar), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Mostrar el diálogo
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
