package com.example.myhealthlife.activities;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.example.myhealthlife.domain.util.AppUtils.topBar;
import static com.example.myhealthlife.domain.DeviceAdapter.setDeviceImage;
import static com.yucheng.ycbtsdk.YCBTClient.connectBle;
import static com.yucheng.ycbtsdk.YCBTClient.connectState;
import static com.yucheng.ycbtsdk.YCBTClient.disconnectBle;
import static com.yucheng.ycbtsdk.YCBTClient.getBindDeviceName;
import static com.yucheng.ycbtsdk.YCBTClient.getDeviceBatteryValue;
import static com.yucheng.ycbtsdk.YCBTClient.initClient;
import static com.yucheng.ycbtsdk.YCBTClient.startScanBle;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhealthlife.R;
import com.example.myhealthlife.domain.AppBleManager;
import com.example.myhealthlife.domain.BleManager;
import com.example.myhealthlife.domain.BluetoothDevice;
import com.example.myhealthlife.domain.DeviceAdapter;
import com.example.myhealthlife.ui.common.HealthViewModel;
import com.example.myhealthlife.ui.common.SyncViewModel;
import com.yucheng.ycbtsdk.Constants;
import com.yucheng.ycbtsdk.bean.ScanDeviceBean;

import java.util.ArrayList;
import java.util.List;

public class DeviceScanActivity extends AppCompatActivity {

    private ListView listView;
    private DeviceAdapter adapter;
    private List<BluetoothDevice> devices;
    private List<ScanDeviceBean> scannedDevices = new ArrayList<>();
    private ImageView rightIcon, image_device;
    private TextView device_name, device_battery;
    private LinearLayout devices_list, my_equipment;
    private View desconectar;
    private HealthViewModel viewModel;
    BleManager bleCore = AppBleManager.getInstance(this).getBle();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_scan);
        bleCore = AppBleManager.getInstance(this).getBle();
        initViews();                                  //Inicializar vistas
        setContent();                                 //Definir que vista se mostrará
        viewModel =
                viewModel = new ViewModelProvider(this).get(HealthViewModel.class);

    }
    /**Principales**/
    private void initViews(){
        topBar(getString(R.string.dispositivos_disponibles), this);

        devices_list = findViewById(R.id.devices_list);
        my_equipment = findViewById(R.id.my_equipment);

        listView = findViewById(R.id.device_list);
        rightIcon = findViewById(R.id.connect_device);
        desconectar = findViewById(R.id.desconectar);

        device_name = findViewById(R.id.device_name);
        device_battery = findViewById(R.id.device_battery);
        image_device = findViewById(R.id.image_device);

        devices = new ArrayList<>();
        adapter = new DeviceAdapter(this, devices);
        listView.setAdapter(adapter);
    }
    private void setContent() {

        AppBleManager manager = AppBleManager.getInstance(this);

        /*String mac = getSharedPreferences("ble_prefs", MODE_PRIVATE)
                .getString("last_mac", null);

        if (mac != null) {
            manager.connect(mac, this);   // ¡Reconecta automáticamente!
        }*/

        switch (bleCore.getState()) {

            case Constants.BLEState.ReadWriteOK:
                setMyEquipmentContent();
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
                this,
                getRequiredPermissions(),
                REQ_BT
        );
    }

    private void startScan() {

        if (!bleCore.hasBlePermissions(this)) {
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
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
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

            runOnUiThread(() -> adapter.notifyDataSetChanged());
        });
    }

    private void connectDevices() {

        listView.setOnItemClickListener((parent, view, position, id) -> {

            if (position >= scannedDevices.size()) return;

            ScanDeviceBean selected = scannedDevices.get(position);

            Toast.makeText(this, getString(R.string.conectando), Toast.LENGTH_SHORT).show();

            /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED) return;*/

            bleCore.stopScan(); // detener antes de conectar


            bleCore.connectDevice(selected.device.getAddress(), code -> {

                switch (bleCore.getState()) {

                    case Constants.BLEState.ReadWriteOK:
                        Toast.makeText(this, getString(R.string.dispositivo_conectado), Toast.LENGTH_SHORT).show();
                        setMyEquipmentContent();
                        SyncViewModel viewModelSync =
                                new ViewModelProvider(this).get(SyncViewModel.class);
                        if(updateFunctions()){
                            Toast.makeText(this,getString(R.string.home_actualizando), Toast.LENGTH_SHORT).show();
                            new Handler().postDelayed(() -> {
                                viewModelSync.syncAll();
                            }, 15000); //  10 segundos
                        }
                        else{
                            Toast.makeText(this, getString(R.string.home_por_favor_conecte), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case Constants.BLEState.Disconnect:
                        Toast.makeText(this, getString(R.string.dispositivo_desconectado), Toast.LENGTH_SHORT).show();
                        break;

                    case Constants.BLEState.TimeOut:
                        Toast.makeText(this, getString(R.string.tiempo_conexion_agotado), Toast.LENGTH_SHORT).show();
                        break;
                }
            });
        });

        rightIcon.setImageResource(R.drawable.baseline_sync_24);
        rightIcon.setOnClickListener(v -> {
            bleCore.stopScan();
            startScan();
            Toast.makeText(this, getString(R.string.home_actualizando), Toast.LENGTH_SHORT).show();
        });
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
        topBar(getString(R.string.dispositivos_disponibles), this);
        rightIcon.setImageResource(R.drawable.baseline_sync_24);
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

        setDeviceImage(rightIcon,name);
        topBar(getString(R.string.mi_equipo),this);
        desconectar.setOnClickListener(v->{

            Toast.makeText(this, getString(R.string.dispositivo_desconectado), Toast.LENGTH_SHORT).show();
            disconnectBle();
        });
    }

    private boolean updateFunctions() {
        if(connectState() != Constants.BLEState.ReadWriteOK)
            return false;
        viewModel.syncAllFromBle();
        return true;
    }
}
