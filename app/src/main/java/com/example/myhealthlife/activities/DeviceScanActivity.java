package com.example.myhealthlife.activities;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.example.myhealthlife.model.DeviceAdapter.setDeviceImage;
import static com.yucheng.ycbtsdk.YCBTClient.connectBle;
import static com.yucheng.ycbtsdk.YCBTClient.connectState;
import static com.yucheng.ycbtsdk.YCBTClient.disconnectBle;
import static com.yucheng.ycbtsdk.YCBTClient.getBindDeviceMac;
import static com.yucheng.ycbtsdk.YCBTClient.getBindDeviceName;
import static com.yucheng.ycbtsdk.YCBTClient.getDeviceBatteryValue;
import static com.yucheng.ycbtsdk.YCBTClient.initClient;
import static com.yucheng.ycbtsdk.YCBTClient.registerBleStateChange;
import static com.yucheng.ycbtsdk.YCBTClient.startScanBle;
import static com.yucheng.ycbtsdk.YCBTClient.stopScanBle;
import static com.yucheng.ycbtsdk.YCBTClient.unRegisterBleStateChange;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myhealthlife.R;
import com.example.myhealthlife.model.BluetoothDevice;
import com.example.myhealthlife.model.DeviceAdapter;
import com.example.myhealthlife.model.EventBusMessageEvent;
import com.example.myhealthlife.ui.MainActivity;
import com.yucheng.ycbtsdk.Constants;
import com.yucheng.ycbtsdk.YCBTClient;
import com.yucheng.ycbtsdk.bean.ScanDeviceBean;
import com.yucheng.ycbtsdk.gatt.Reconnect;
import com.yucheng.ycbtsdk.response.BleConnectResponse;
import com.yucheng.ycbtsdk.response.BleScanResponse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class DeviceScanActivity extends Activity {

    private ListView listView;
    private DeviceAdapter adapter;
    private List<BluetoothDevice> devices;
    private List<ScanDeviceBean> scannedDevices = new ArrayList<>();
    private EventBusMessageEvent eventBusMessageEvent = new EventBusMessageEvent();
    private String lastConnectedMac;
    private ImageView rightIcon, image_device;
    private TextView device_name, device_battery;
    private LinearLayout devices_list, my_equipment;
    private View desconectar;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_scan);
        initViews();                                        //Inicializar vistas
        setContent();                                //Definir que vista se mostrará
    }
    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }
    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
    /**Principales**/
    private void initViews(){
        devices_list = findViewById(R.id.devices_list);
        my_equipment = findViewById(R.id.my_equipment);

        listView = findViewById(R.id.device_list);
        rightIcon = findViewById(R.id.connect_device);
        desconectar = findViewById(R.id.desconectar);
        topBar(getString(R.string.dispositivos_disponibles));

        device_name = findViewById(R.id.device_name);
        device_battery = findViewById(R.id.device_battery);
        image_device = findViewById(R.id.image_device);

        devices = new ArrayList<>();
        adapter = new DeviceAdapter(this, devices);
        listView.setAdapter(adapter);
    }
    private void setContent(){
        if(connectState() == Constants.BLEState.ReadWriteOK){
            setMyEquipmentContent();
        }
        else{
            setDevicesListContent();
        }
    }
    /**Escaneo**/
    private void startScanBleFun(){
        // 1. Verifica permisos
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT},
                    1001);
            return;
        }
        //2. Inicia escaneo
        startScanBle(new BleScanResponse() {
            @Override
            public void onScanResponse(int i, ScanDeviceBean scanDeviceBean) {
                if (scanDeviceBean != null && scanDeviceBean.device != null) {
                    if (ActivityCompat.checkSelfPermission(DeviceScanActivity.this.getBaseContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    String mac = String.valueOf(scanDeviceBean.device.getAddress());
                    String name = String.valueOf(scanDeviceBean.device.getName());
                    String rssi = String.valueOf(scanDeviceBean.getDeviceRssi());

                    // Evita duplicados
                    for (ScanDeviceBean d : scannedDevices) {
                        if (d.device.getAddress().equals(mac)) return;
                    }
                    scannedDevices.add(scanDeviceBean);

                    devices.add(new BluetoothDevice(name, mac,rssi));

                    adapter.notifyDataSetChanged();
                }
            }
        }, 6);
    }
    private void connectDevices(){
        listView.setOnItemClickListener((parent, view, position, id) -> {
            ScanDeviceBean selectedDevice = scannedDevices.get(position);
            if (selectedDevice != null && selectedDevice.device != null) {
                String macAd = selectedDevice.device.getAddress();
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                stopScanBle();
                String mensaje = getString(R.string.conectando) + " ⏳";
                Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
                connectToDevice(macAd);
            }
        });

        rightIcon.setImageResource(R.drawable.baseline_sync_24);
        rightIcon.setOnClickListener(v -> {
            stopScanBle();
            //devices = new ArrayList<>();
            //adapter = new DeviceAdapter(this, devices);
            if (devices != null) {
                devices.clear();
                scannedDevices.clear();
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                //Toast.makeText(this, "Lista limpiada", Toast.LENGTH_SHORT).show();
                startScanBleFun();
                Toast.makeText(this, getString(R.string.home_actualizando), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void saveBleStateToPrefs(String mac, String state) {
        SharedPreferences prefs = getSharedPreferences("BLE_PREFS", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("LAST_MAC", mac);
        editor.putString("LAST_STATE", state);
        editor.apply();
    }
    private void connectToDevice(String mac) {
        lastConnectedMac = mac; // Guardamos para usar luego en el evento
        connectBle(mac, new BleConnectResponse() {
            @Override
            public void onConnectResponse(int code) {
                Log.d("BLE_SCAN", "Conexión código: " + code);
                // Registrar cambios de estado
                switch (connectState()) {
                    case Constants.BLEState.Disconnect:
                        eventBusMessageEvent.belState = EventBusMessageEvent.DISCONNECT;
                        break;
                    case Constants.BLEState.ReadWriteOK:
                        eventBusMessageEvent.belState = EventBusMessageEvent.CONNECTED;
                        break;
                    case Constants.BLEState.TimeOut:
                        eventBusMessageEvent.belState = EventBusMessageEvent.TIMEOUT;
                        break;
                    case Constants.BLEState.Disconnecting:
                        eventBusMessageEvent.belState = EventBusMessageEvent.DISCONNECTING;
                        break;
                    case Constants.BLEState.Connecting:
                        eventBusMessageEvent.belState = EventBusMessageEvent.CONNECTING;
                        break;
                    default:
                        eventBusMessageEvent.belState = EventBusMessageEvent.CONNECTING;
                }
                EventBus.getDefault().post(eventBusMessageEvent);
            }
        });
    }
    /**Auxiliares**/
    private void setDevicesListContent(){
        my_equipment.setVisibility(GONE);
        devices_list.setVisibility(VISIBLE);
        rightIcon.setImageResource(R.drawable.baseline_sync_24);
        topBar(getString(R.string.dispositivos_disponibles));

        startScanBleFun();                                  //Iniciar escaneo de dispositivos
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
        topBar(getString(R.string.mi_equipo));
        desconectar.setOnClickListener(v->{
            disconnectBle();
            setDevicesListContent();
        });
    }
    private void topBar(String title){
        //Barra de navegación superior
        TextView titleTextView = findViewById(R.id.TITLE); // Titulo
        titleTextView.setText(title);
        ImageView btnBack = findViewById(R.id.backTop);
        btnBack.setImageResource(R.drawable.keyboard_arrow_left);
        btnBack.setOnClickListener(v -> finish());
    }
    private void saveLastConnectedDevice(String mac) {
        SharedPreferences prefs = getSharedPreferences("YCBT_PREFS", Context.MODE_PRIVATE);
        prefs.edit().putString("LAST_CONNECTED_MAC", mac).apply();
    }
    @Subscribe(threadMode = ThreadMode.MAIN) // MAIN para poder mostrar Toast
    public void onBleStateChange(EventBusMessageEvent event) {
        String mensaje = "";
        String estado = "";

        switch (event.belState) {
            case EventBusMessageEvent.CONNECTED:
                mensaje = getString(R.string.dispositivo_conectado) + " ✅ ";
                estado = "CONNECTED";
                setMyEquipmentContent();
                break;
            case EventBusMessageEvent.DISCONNECT:
                mensaje = getString(R.string.dispositivo_desconectado) + " ❌";
                estado = "DISCONNECTED";
                setDevicesListContent();
                break;
            case EventBusMessageEvent.TIMEOUT:
                mensaje = getString(R.string.tiempo_conexion_agotado) + " ⏳";
                estado = "TIMEOUT";
                break;
            case EventBusMessageEvent.CONNECTING:
                mensaje = getString(R.string.conectando);
                estado = "CONNECTING";
                break;
        }

        if (!mensaje.isEmpty()) {
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
        }

        // Guardar en SharedPreferences. Si event.deviceMac no existe, usa una variable global que guardes al conectar
        String macToSave = lastConnectedMac != null ? lastConnectedMac : null;
        if (macToSave != null) {
            saveBleStateToPrefs(macToSave, estado);
        }
    }
}
