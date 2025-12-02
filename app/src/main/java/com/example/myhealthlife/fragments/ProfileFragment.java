package com.example.myhealthlife.fragments;
import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.example.myhealthlife.model.TimestampManager.obtenerTimestamps;
import static com.jieli.jl_bt_ota.util.PreferencesHelper.getSharedPreferences;
import static com.yucheng.ycbtsdk.YCBTClient.connectState;
import static com.yucheng.ycbtsdk.YCBTClient.getBindDeviceName;
import static com.yucheng.ycbtsdk.YCBTClient.getDeviceBatteryValue;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.myhealthlife.R;
import com.example.myhealthlife.activities.DeviceScanActivity;
import com.example.myhealthlife.model.ButtonPageView;
import com.example.myhealthlife.model.NetworkModeDialog;
import com.example.myhealthlife.ui.LoginActivity;
import com.yucheng.ycbtsdk.Constants;
import com.yucheng.ycbtsdk.YCBTClient;

import java.util.Date;
import java.util.Objects;

public class ProfileFragment extends Fragment {
    public ProfileFragment() {
        // Required empty public constructor
    }
    View logout;
    ButtonPageView devices, monitoring, body,language, consumoDatos;
    LinearLayout deviceInfo;
    TextView deviceName, batteryPct;
    ImageView batteryImage;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        logout = view.findViewById(R.id.LO);
        devices = view.findViewById(R.id.open_devices_button);
        monitoring = view.findViewById(R.id.change_monitoring);
        body = view.findViewById(R.id.hw_settings);
        language = view.findViewById(R.id.language);
        consumoDatos = view.findViewById(R.id.datos_moviles);

        MonitoringDialogFragment dialog = new MonitoringDialogFragment();
        QuestionnaireDialogFragment dialogQ = new QuestionnaireDialogFragment();
        LanguageDialogFragment dialogL = new LanguageDialogFragment();
        SharedPreferences prefs = requireActivity().getSharedPreferences("MyApp", Context.MODE_PRIVATE);


        logout.setOnClickListener(v -> {
            // Limpiar el back stack de fragments si hay alguno
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            if (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }

            // Limpiar preferencias o cualquier sesión si aplica
            prefs.edit().putInt("tryLogin", 0).apply();
            prefs.edit().putBoolean("is_logged_in", false).apply();


            // Iniciar LoginActivity y limpiar el stack de actividades
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            // Cerrar la actividad actual
            requireActivity().finishAffinity(); // Esto asegura que se cierren todas las actividades relacionadas
        });

        devices.setOnButtonClickListener(v -> startActivityFun(DeviceScanActivity.class));
        monitoring.setOnButtonClickListener(v -> dialog.show(getChildFragmentManager(), "MonitoringDialogFragment"));
        body.setOnButtonClickListener(v -> dialogQ.show(getChildFragmentManager(), "QuestionnaireDialog"));
        language.setOnButtonClickListener(v -> dialogL.show(getChildFragmentManager(), "LanguageDialogFragment"));
        consumoDatos.setOnButtonClickListener(v -> NetworkModeDialog.showNetworkModeDialog(requireContext()));

        deviceName = view.findViewById(R.id.device_name);
        batteryPct = view.findViewById(R.id.device_battery);
        deviceInfo = view.findViewById(R.id.my_equipment);
        batteryImage = view.findViewById(R.id.battery_image);

        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        //Conectar si esta desconectado
        if(connectState() == Constants.BLEState.ReadWriteOK) {
            showInfoDevice();
        }
    }
    private void startActivityFun(Class activityClass){
        Intent intent = new Intent(requireActivity(), activityClass);
        startActivity(intent);
    }

    private void showInfoDevice(){
        String name = getBindDeviceName();
        String battery = String.valueOf(getDeviceBatteryValue());

        if(connectState() == Constants.BLEState.ReadWriteOK){
            deviceInfo.setVisibility(VISIBLE);
            deviceName.setText(name);
            batteryPct.setText(battery+"%");
            Integer batteryPercent = Integer.valueOf(battery);
            if (batteryPercent <= 25) {
                batteryImage.setImageResource(R.drawable.battery_low);
                batteryImage.setColorFilter(ContextCompat.getColor(getContext(), R.color.red));
            } else if (batteryPercent <= 50) {
                batteryImage.setImageResource(R.drawable.battery_mid);
                batteryImage.setColorFilter(ContextCompat.getColor(getContext(), R.color.orange));
            } else if (batteryPercent <= 75) {
                batteryImage.setImageResource(R.drawable.battery_high);
                batteryImage.setColorFilter(ContextCompat.getColor(getContext(), R.color.green));
            }
        }
        else{
            deviceInfo.setVisibility(GONE);
        }
    }



}