package com.example.myhealthlife.ui;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import static com.example.myhealthlife.model.DeviceAdapter.setDeviceImage;
import static com.yucheng.ycbtsdk.YCBTClient.connectState;
import static com.yucheng.ycbtsdk.YCBTClient.getBindDeviceName;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myhealthlife.R;
import com.example.myhealthlife.activities.DeviceScanActivity;
import com.example.myhealthlife.fragments.CountriesDialogFragment;
import com.example.myhealthlife.fragments.DevicesFragment;
import com.example.myhealthlife.fragments.HealthFragment;
import com.example.myhealthlife.fragments.HomeFragment;
import com.example.myhealthlife.fragments.InboxFragment;
import com.example.myhealthlife.fragments.LanguageDialogFragment;
import com.example.myhealthlife.fragments.ProfileFragment;
import com.example.myhealthlife.fragments.SportFragment;
import com.example.myhealthlife.model.BleManager;
import com.example.myhealthlife.model.HealthWorker;
import com.example.myhealthlife.model.LocaleHelper;
import com.example.myhealthlife.model.NetworkOperationManager;
import com.example.myhealthlife.model.NetworkRestrictionManager;
import com.example.myhealthlife.model.NetworkUtils;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.jieli.jl_bt_ota.util.PreferencesHelper;
import com.yucheng.ycbtsdk.Constants;
import com.yucheng.ycbtsdk.YCBTClient;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
public class MainActivity extends AppCompatActivity {
    //Navegacion
    BottomNavigationView bottomNavigationView;
    ViewPager2 pager;
    ViewPagerFragmentAdapter adapter;
    ImageView connect_device;
    //Uso de Datos e Internet
    private NetworkOperationManager networkOperationManager;
    //UI
    private TextView title;
    private ImageView leftArrow, rightArrow;
    //SharedpPreferences
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
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        ble = BleManager.getInstance(this);


        // Inicializar SharedPreferences
        prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
        savedInterval = prefs.getInt("interval", 15); // 15 es valor por defecto

        setContentView(R.layout.activity_main);
        modeNightOff();                                         // Desactivar el modo oscuro
        initSDK();                                              //Inicializar el SDK
        configureNavigation();                                  //Configura las interfaces de navegación
        loginValidations();                                     //Validaciones del inicio de sesión
        requestNecessaryPermissions();                          //Solicitar permisos si es necesario
        setWorker();                                            //Configurar el Worker (Tarea del monitoreo)
        conectLastDevice();

        //Desactivar el Internet (TEMPORALMENTE)
        //networkOperationManager = new NetworkOperationManager(this);
        //networkOperationManager.getRestrictionManager().enableDataRestriction(120);
        //Toast.makeText(this, "Restricción activada por 30 minutos", Toast.LENGTH_SHORT).show();

    }

    private void conectLastDevice() {

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

    /**
     * PRINCIPALES
     **/
    private void configureNavigation() {
        // Configura BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        pager = findViewById(R.id.pager);
        //title = findViewById(R.id.TITLE);
        //leftArrow = findViewById(R.id.leftArrow);
        //rightArrow = findViewById(R.id.rightArrow);

        //connect_device = findViewById(R.id.connect_device);
        Intent intent = new Intent(this, DeviceScanActivity.class);
        //connect_device.setOnClickListener(v -> startActivity(intent));

        setupBottomNav();
        adapter = new ViewPagerFragmentAdapter(MainActivity.this, bottomNavigationView.getItemIconSize(), MainActivity.this);
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(1);
        pager.setUserInputEnabled(true);  // Permite deslizamiento
        pager.setCurrentItem(0);
        pager.setPadding(0,0,0,0);
        pager.setBottom(0);
        pager.setClipToPadding(true);

        /*//Icono de barra superior
        if (connectState() == Constants.BLEState.ReadWriteOK) {
            String name = getBindDeviceName();
            setDeviceImage(connect_device,name);
        }*/

        BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.nav_inbox);
        badge.setVisible(true);
        badge.clearNumber();      // elimina el número
        badge.setBackgroundColor(Color.RED);

    }
    private void loginValidations() {
        boolean logged = prefs.getBoolean("is_logged_in", false); // Valor por defecto: 0
        if (!logged) {
            // Limpiar el back stack de fragments si hay alguno
            FragmentManager fragmentManager = this.getSupportFragmentManager();
            if (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }

            // Limpiar preferencias o cualquier sesión si aplica
            prefs.edit().putInt("tryLogin", 0).apply();
            prefs.edit().putBoolean("is_logged_in", false);

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
    /**
     * PAGER
     **/
    public static class ViewPagerFragmentAdapter extends FragmentStateAdapter {

        private String[] titles;
        private Context context;
        int size;

        public ViewPagerFragmentAdapter(@NonNull FragmentActivity fragmentActivity, int size, Context context) {
            super(fragmentActivity);
            this.context = context;
            this.titles = new String[]{
                    context.getString(R.string.inbox),
                    context.getString(R.string.inicio),
                    context.getString(R.string.diagnosticos),
                    context.getString(R.string.ajustes)
            };
            this.size = size;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new HomeFragment();
                case 1:
                    return new InboxFragment();
                case 2:
                    return new HealthFragment();
                case 3:
                    return new ProfileFragment();
            }
            return new Fragment();
        }

        public String getTitle(int position) {
            return titles[position];
        }

        public void updateTitles(Context newContext) {
            this.context = newContext;
            this.titles = new String[]{
                    context.getString(R.string.inbox),
                    context.getString(R.string.inicio),
                    context.getString(R.string.diagnosticos),
                    context.getString(R.string.ajustes)
            };
        }

        @Override
        public int getItemCount() {
            return Math.min(size, 4);
        }
    }

    private void setupBottomNav() {

        /*leftArrow.setOnClickListener(item -> {

            if(pager.getCurrentItem() == 1); {
                pager.setCurrentItem(0);
            }
        });
*/
        /*rightArrow.setOnClickListener(item -> {

            if(pager.getCurrentItem() == 0); {
                pager.setCurrentItem(1);
            }
            if (pager.getCurrentItem() == 1) {
                pager.setCurrentItem(2);
            }
        });*/

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
             if (itemId == R.id.nav_home) {
                pager.setCurrentItem(0);  // (Inbox)
                return true;
            } else if (itemId == R.id.nav_inbox) {
                 pager.setCurrentItem(1);  // (Home)
                 return true;
             }else if (itemId == R.id.nav_health) {
                pager.setCurrentItem(2);  // (Health)
                return true;
            }else if (itemId == R.id.nav_profile) {
                pager.setCurrentItem(3);  // (Profile)
                return true;
            } else {
                return false;  // Item no reconocido
            }
        });


        // Sincroniza ViewPager2 con BottomNavigationView
        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                /*String newTitle = ((ViewPagerFragmentAdapter) Objects.requireNonNull(pager.getAdapter())).getTitle(position);
                title.setText(newTitle);*/
                /*if (position == 0) {
                    leftArrow.setVisibility(INVISIBLE);
                    rightArrow.setVisibility(VISIBLE);
                    rightArrow.setOnClickListener(item -> {
                        pager.setCurrentItem(1);
                    });
                }
                if (position == 1) {
                    leftArrow.setVisibility(VISIBLE);
                    rightArrow.setVisibility(VISIBLE);
                    leftArrow.setOnClickListener(item -> {
                        pager.setCurrentItem(0);
                    });
                    rightArrow.setOnClickListener(item -> {
                        pager.setCurrentItem(2);
                    });
                }
                if (position == 2) {
                    leftArrow.setVisibility(VISIBLE);
                    rightArrow.setVisibility(INVISIBLE);
                    leftArrow.setOnClickListener(item -> {
                        pager.setCurrentItem(1);
                    });
                }*/
            }
        });
    }
    /**
    *AUXILIARES
    **/
    //🔒 Permisos
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
    }
    // 🛜 Redes
    private void updateStatus() {
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
    }
    private void testNetworkConnection() {
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
    }
    //🖼️ UI
    private void modeNightOff(){
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }
    public void refreshTabs() {
        adapter.updateTitles(this); // usa 'this', no getApplicationContext()
    }


    //🤖 SDK
    private void initSDK(){
        // ✅ Inicializar el SDK
        YCBTClient.initClient(this, true, true);
        Log.d("INIT_CHECK", "✅ SDK inicializado");
    }

}



