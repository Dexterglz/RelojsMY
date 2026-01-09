package com.example.myhealthlife.fragments;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import static com.example.myhealthlife.model.DeviceAdapter.setDeviceImage;
import static com.yucheng.ycbtsdk.YCBTClient.disconnectBle;
import static com.yucheng.ycbtsdk.YCBTClient.getBindDeviceName;
import static com.yucheng.ycbtsdk.YCBTClient.getDeviceBatteryValue;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhealthlife.R;
import com.example.myhealthlife.model.AppBleManager;
import com.example.myhealthlife.model.BleManager;
import com.example.myhealthlife.model.BluetoothDevice;
import com.example.myhealthlife.model.DeviceAdapter;
import com.example.myhealthlife.model.SportViewModel;
import com.example.myhealthlife.util.ToastUtil;
import com.yucheng.ycbtsdk.Constants;
import com.yucheng.ycbtsdk.bean.ScanDeviceBean;

import java.util.ArrayList;
import java.util.List;

public class BluetoothDialogFragment extends DialogFragment {

    private EditText inputGoalSteps;
    private RadioGroup radioGroupGender;
    private SportViewModel viewModel;
    private ListView listView;
    private DeviceAdapter adapter;
    private List<BluetoothDevice> devices;
    private List<ScanDeviceBean> scannedDevices = new ArrayList<>();
    private ImageView rightIcon, image_device;
    private TextView device_name, device_battery;
    private LinearLayout devices_list, my_equipment;
    private View desconectar;
    BleManager bleCore = AppBleManager.getInstance(getContext()).getBle();
    View view;

    public BluetoothDialogFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_ble, container, false);
        ImageView checkIcon = view.findViewById(R.id.checkIcon);
        View ripple = view.findViewById(R.id.ripple);

        startSuccessAnimation(checkIcon, ripple);

        initViews();                                  //Inicializar vistas
        setContent();                                 //Definir que vista se mostrará

        return view;
    }

    private void initViews(){

        devices_list = view.findViewById(R.id.devices_list);
        my_equipment = view.findViewById(R.id.my_equipment);

        listView = view.findViewById(R.id.device_list);

        desconectar = view.findViewById(R.id.desconectar);

        device_name = view.findViewById(R.id.device_name);
        device_battery = view.findViewById(R.id.device_battery);
        image_device = view.findViewById(R.id.image_device);

        devices = new ArrayList<>();
        adapter = new DeviceAdapter(view.getContext(), devices);
        listView.setAdapter(adapter);
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

    private void startSuccessAnimation(View check, View ripple) {

        // Rotación del check
        ObjectAnimator rotate = ObjectAnimator.ofFloat(
                check,
                View.ROTATION,
                0f,
                360f
        );
        rotate.setDuration(1500);

        // Ola - escala
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(
                ripple,
                View.SCALE_X,
                1f,
                1.6f
        );

        ObjectAnimator scaleY = ObjectAnimator.ofFloat(
                ripple,
                View.SCALE_Y,
                1f,
                1.6f
        );

        // Ola - desvanecer
        ObjectAnimator fade = ObjectAnimator.ofFloat(
                ripple,
                View.ALPHA,
                1f,
                0f
        );

        AnimatorSet rippleSet = new AnimatorSet();
        rippleSet.playTogether(scaleX, scaleY, fade);
        rippleSet.setDuration(2500);

        // Animación total
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(rotate, rippleSet);
        animatorSet.start();
    }

    private void setContent() {

        AppBleManager manager = AppBleManager.getInstance(view.getContext());

        /*String mac = getSharedPreferences("ble_prefs", MODE_PRIVATE)
                .getString("last_mac", null);

        if (mac != null) {
            manager.connect(mac, this);   // ¡Reconecta automáticamente!
        }*/

        switch (bleCore.getState()) {

            case Constants.BLEState.ReadWriteOK:
                //setMyEquipmentContent();
                Toast.makeText(getContext(), "00 this is 00", Toast.LENGTH_SHORT).show();
                break;

            case Constants.BLEState.Disconnect:
                setDevicesListContent();
                break;

            case Constants.BLEState.TimeOut:
                setDevicesListContent();
                break;
            default:
                setDevicesListContent();
                break;
        }
    }


    private static final int REQ_PERMISSIONS = 1001;
    private String[] getRequiredPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return new String[]{
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT
            };
        } else {
            return new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            };
        }
    }
    private static final int REQ_BT = 2001;

    private void requestBlePermissions() {
        ActivityCompat.requestPermissions(
                getActivity(),
                getRequiredPermissions(),
                REQ_BT
        );
    }

    private void startScan() {

        if (!bleCore.hasBlePermissions(getContext())) {
            requestBlePermissions();
            return;
        }

        // Limpieza
        devices.clear();
        scannedDevices.clear();
        adapter.notifyDataSetChanged();

        bleCore.stopScan(); // evita múltiples escaneos

        bleCore.startScan((code, bean) -> {
            if (bean == null || bean.device == null){
                return;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                    ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_CONNECT)
                            != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            String name = bean.device.getName();
            if (name == null) name = getString(R.string.dispositivo_desconocido);

            String mac = bean.device.getAddress();
            String rssi = String.valueOf(bean.getDeviceRssi());

            // Evitar duplicados
            for (BluetoothDevice d : devices) {
                if (d.getMac().equals(mac)) return;
            }

            // Guardar bean real
            scannedDevices.add(bean);

            // Guardar item visible en lista
            devices.add(new BluetoothDevice(name, mac, rssi));

            getActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());
        });
    }

    private void connectDevices() {

        listView.setOnItemClickListener((parent, view, position, id) -> {

            if (position >= scannedDevices.size()) return;

            ScanDeviceBean selected = scannedDevices.get(position);

            Toast.makeText(getContext(), getString(R.string.conectando), Toast.LENGTH_SHORT).show();

            /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED) return;*/

            bleCore.stopScan(); // detener antes de conectar

            bleCore.connectDevice(selected.device.getAddress(), code -> {

                switch (bleCore.getState()) {

                    case Constants.BLEState.ReadWriteOK:
                        Toast.makeText(getContext(), getString(R.string.dispositivo_conectado), Toast.LENGTH_SHORT).show();
                        setMyEquipmentContent();
                        break;

                    case Constants.BLEState.Disconnect:
                        Toast.makeText(getContext(), getString(R.string.dispositivo_desconectado), Toast.LENGTH_SHORT).show();
                        break;

                    case Constants.BLEState.TimeOut:
                        Toast.makeText(getContext(), getString(R.string.tiempo_conexion_agotado), Toast.LENGTH_SHORT).show();
                        break;
                }
            });
        });

        /*rightIcon.setImageResource(R.drawable.baseline_sync_24);
        rightIcon.setOnClickListener(v -> {
            bleCore.stopScan();
            startScan();
            Toast.makeText(getContext(), getString(R.string.home_actualizando), Toast.LENGTH_SHORT).show();
        });*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQ_BT) {
            boolean granted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    granted = false;
                    break;
                }
            }

            if (granted) {
                startScan(); // ← REINTENTAR AUTOMÁTICAMENTE
            } else {
                //Toast.makeText(this, "Se requieren permisos Bluetooth", Toast.LENGTH_LONG).show();
            }
        }
    }



    /**Auxiliares**/

    private void setDevicesListContent() {
        my_equipment.setVisibility(GONE);
        devices_list.setVisibility(VISIBLE);

        //rightIcon.setImageResource(R.drawable.baseline_sync_24);
        disconnectBle();
        startScan();
        connectDevices();
    }

    private void setMyEquipmentContent(){
        String name = getBindDeviceName();
        String battery = String.valueOf(getDeviceBatteryValue());

        DeviceAdapter.setDeviceImage(image_device,name);

        device_name.setText(name);
        device_battery.setText(battery+"%");
        devices_list.setVisibility(GONE);
        my_equipment.setVisibility(VISIBLE);

        //setDeviceImage(rightIcon,name);

        desconectar.setOnClickListener(v->{

            Toast.makeText(getContext(), getString(R.string.dispositivo_desconectado), Toast.LENGTH_SHORT).show();
            disconnectBle();
        });
    }




}
