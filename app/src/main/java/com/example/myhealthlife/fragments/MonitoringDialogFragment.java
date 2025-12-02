package com.example.myhealthlife.fragments;

import static com.yucheng.ycbtsdk.YCBTClient.appSengMessageToDevice;
import static com.yucheng.ycbtsdk.YCBTClient.initClient;
import static com.yucheng.ycbtsdk.YCBTClient.setReconnect;
import static com.yucheng.ycbtsdk.YCBTClient.settingBloodOxygenModeMonitor;
import static com.yucheng.ycbtsdk.YCBTClient.settingHeartMonitor;
import static com.yucheng.ycbtsdk.YCBTClient.settingTemperatureMonitor;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.myhealthlife.R;
import com.example.myhealthlife.ui.MainActivity;
import com.yucheng.ycbtsdk.gatt.Reconnect;
import com.yucheng.ycbtsdk.response.BleDataResponse;

import java.util.HashMap;

public class MonitoringDialogFragment extends DialogFragment {

    public MonitoringDialogFragment() {}
    Button btnChangeMonitoring;
    SharedPreferences prefs;
    Spinner spinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monitoring_dialog, container, false);
        prefs = requireActivity().getSharedPreferences("monitoring", Context.MODE_PRIVATE);
        btnChangeMonitoring = view.findViewById(R.id.btnChangeMonitoring);

        int savedInterval = prefs.getInt("interval", 15); // 15 es valor por defecto si no existe
        // 2. Buscar la posición del valor en el array del Spinner
        String[] intervals = getResources().getStringArray(R.array.spinner_options); // Tu array de opciones
        int position = 0; // Posición por defecto (ej: 10)
        for (int i = 0; i < intervals.length; i++) {
            if (Integer.parseInt(intervals[i]) == savedInterval) {
                position = i;
                break;
            }
        }

        spinner = view.findViewById(R.id.mySpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.spinner_options, // Define este array en res/values/strings.xml
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(position);

        btnChangeMonitoring.setOnClickListener(v ->
        {
            saveOption();
            dismiss();
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog_background);
        }
    }
    public void saveOption(){
        // Guardar en SharedPreferences
        int[] values = {15, 30, 60}; // Array de valores numéricos
        SharedPreferences.Editor editor = prefs.edit();
        int selectedValue = values[spinner.getSelectedItemPosition()];
        editor.putInt("interval", selectedValue);
        editor.apply();
        initClientFun();
        healthMonitoringFun(selectedValue);
    }
    private void healthMonitoringFun(Integer min){
        //El dispositivo medirá los datos correspondientes y los guardará
        //Corazón
        Log.d("MONITEREO: ",min + " min configurados");
        appSengMessageToDevice(1, "MHL", min + " min", new BleDataResponse() {
            @Override
            public void onDataResponse(int i, float v, HashMap hashMap) {
            }
        });
        settingHeartMonitor(
                0x01,           //Forma automática
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
}