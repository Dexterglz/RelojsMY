package com.example.myhealthlife.ui;

import static androidx.core.content.ContentProviderCompat.requireContext;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myhealthlife.R;
import com.example.myhealthlife.activities.DeviceScanActivity;
import com.example.myhealthlife.domain.util.SessionManager;
import com.example.myhealthlife.fragments.HealthFragment;
import com.example.myhealthlife.ui.common.view.TopBarView;
import com.example.myhealthlife.ui.home.HomeFragment;
import com.example.myhealthlife.fragments.ProfileFragment;
import com.example.myhealthlife.fragments.SportFragment;
import com.example.myhealthlife.domain.BleManager;
import com.example.myhealthlife.domain.HealthWorker;
import com.example.myhealthlife.domain.LocaleHelper;
import com.example.myhealthlife.domain.NetworkOperationManager;
import com.example.myhealthlife.domain.NetworkRestrictionManager;
import com.example.myhealthlife.domain.util.NetworkUtils;
import com.example.myhealthlife.ui.login.LoginActivity;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yucheng.ycbtsdk.Constants;
import com.yucheng.ycbtsdk.YCBTClient;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.WindowCompat;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    public Integer savedInterval;
    BleManager ble;
    SharedPreferences prefs;
    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences prefs = newBase.getSharedPreferences("app", MODE_PRIVATE);
        int languageIndex = prefs.getInt("language", 0); // 0=EN, 1=ES por defecto
        String[] languages = {"en", "es"};
        if (languageIndex < 0 || languageIndex >= languages.length) languageIndex = 1;
        String selectedLang = languages[languageIndex];
        Context context = LocaleHelper.setLocale(newBase, selectedLang);
        super.attachBaseContext(context);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Definir Layout
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_main);

        //Navegacion General
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        TopBarView topBar = findViewById(R.id.top_bar);
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host);
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(bottomNav, navController);
        navController.addOnDestinationChangedListener(
                (controller, destination, arguments) -> {
                    boolean show = HomeNav.showsTopBar(destination.getId());
                    topBar.setVisibility(show ? View.VISIBLE : View.GONE);
                    bottomNav.setVisibility(show ? View.GONE : View.VISIBLE);
                }
        );


        //Conectar ultimo dispositivo
        initSDK();
        ble = BleManager.getInstance(this);
        conectLastDevice();

        //Desactivar el tema oscuro (tal vez luego)
        modeNightOff();

        //Solicitar permisos
        requestNecessaryPermissions();

        //Validaciones de Inicio de Sesion
        loginValidations();

        //Tarea del monitoreo
        //setWorker();
    }
    /*@Override
    protected void onResume(){
        //Icono de barra superior
        super.onResume();
        if (connectState() == Constants.BLEState.ReadWriteOK) {
            String name = getBindDeviceName();
            setDeviceImage(connect_device,name);
        }
    }*/

    /*--------------------------------------------------------*/
    private void conectLastDevice() {
        // Inicializar SharedPreferences
        prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
        savedInterval = prefs.getInt("interval", 15);

        String mac = getSharedPreferences("ble_prefs", MODE_PRIVATE)
                .getString("last_mac", null);

        if(mac != null){
            ble.connectDevice(mac, response -> {
                if(ble.getState() == Constants.BLEState.ReadWriteOK){
                    Toast.makeText(this, "Reconectado", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private void loginValidations() {
        SessionManager sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            // Limpiar el back stack de fragments si hay alguno
            FragmentManager fragmentManager = this.getSupportFragmentManager();
            if (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }

            // Iniciar LoginActivity y limpiar el stack de actividades
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            // Cerrar la actividad actual
            this.finishAffinity(); // Esto asegura que se cierren todas las actividades relacionadas
        } else {
            Log.d("INIT_CHECK", "✅ Sesión Iniciada");
        }
    }
    private void setWorker() {
        // Crear tarea periódica
        PeriodicWorkRequest healthWorkRequest =
                new PeriodicWorkRequest.Builder(HealthWorker.class, savedInterval, TimeUnit.MINUTES)
                        .build();

        // Encolar el trabajo único (para que no se duplique)
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "HealthWork",
                ExistingPeriodicWorkPolicy.KEEP,
                healthWorkRequest
        );
    }
    private void requestNecessaryPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            String[] permissions = {
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_FINE_LOCATION
            };
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, permissions, 1);
                    break;
                }
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        1001
                );
            }
        }
    }
    private void modeNightOff(){
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }
    private void initSDK(){
        // ✅ Inicializar el SDK
        YCBTClient.initClient(this, true, true);
        Log.d("INIT_CHECK", "✅ SDK inicializado");
    }
    // 🛜 Redes
    /*private void updateStatus() {
        NetworkRestrictionManager restrictionManager = networkOperationManager.getRestrictionManager();
        NetworkUtils networkUtils = networkOperationManager.getNetworkUtils();

        StringBuilder status = new StringBuilder();

        status.append("Estado de red:\n");
        status.append("WiFi: ").append(networkUtils.isWifiAvailable() ? "Conectado" : "Desconectado").append("\n");
        status.append("Datos móviles: ").append(networkUtils.isMobileDataAvailable() ? "Disponible" : "No disponible").append("\n");
        status.append("Restricción activa: ").append(restrictionManager.isDataRestricted() ? "Sí" : "No").append("\n");

        if (restrictionManager.isDataRestricted()) {
            long timeLeft = restrictionManager.getRestrictionTimeLeft();
            status.append("Tiempo restante: ").append(timeLeft / 60).append(" minutos");
        }
    }*/
    /*private void testNetworkConnection() {
        networkOperationManager.executeWithRestrictions(
                true, // Permitir datos móviles
                new NetworkOperationManager.NetworkOperation<String>() {
                    @Override
                    public String execute() throws Exception {
                        // Simular una operación de red
                        Thread.sleep(1000);
                        return "Conexión exitosa";
                    }
                },
                new NetworkOperationManager.NetworkOperationCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        runOnUiThread(() -> {
                            Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
                            updateStatus();
                        });
                    }

                    @Override
                    public void onError(String errorMessage) {
                        runOnUiThread(() -> {
                            Toast.makeText(MainActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                            updateStatus();
                        });
                    }
                }
        );
    }*/
}



