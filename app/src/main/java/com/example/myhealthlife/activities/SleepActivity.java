package com.example.myhealthlife.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myhealthlife.R;
import com.github.mikephil.charting.BuildConfig;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.yucheng.ycbtsdk.Constants;
import com.yucheng.ycbtsdk.YCBTClient;
import com.yucheng.ycbtsdk.gatt.Reconnect;
import com.yucheng.ycbtsdk.response.BleDataResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class SleepActivity extends AppCompatActivity {

    private TextView txtTempValue;

    SimpleDateFormat hourFormat = new SimpleDateFormat("H", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);

        txtTempValue = findViewById(R.id.txt_temp_value);
        
        //Barra de Superior de Navegación
        TextView titleTextView = findViewById(R.id.title_text_view); // Titulo
        titleTextView.setText("Dormir");
        ImageView btnBack = findViewById(R.id.btn_back_bp); //Boton Regresar
        btnBack.setOnClickListener(v -> finish());
        
        Log.d("SLEEP", "🌡️ Iniciando escucha de sueño...");
        YCBTClient.initClient(this,true, BuildConfig.DEBUG);
        Reconnect.getInstance().init(getApplicationContext(),true);
        YCBTClient.resetQueue();
        YCBTClient.appSleepWriteBack(3, 40, 2, 20, 6, 0, new BleDataResponse() {
            @Override
            public void onDataResponse(int i, float v, HashMap hashMap) {
                YCBTClient.healthHistoryData(
                        Constants.DATATYPE.Health_HistorySleep,
                        new BleDataResponse() {
                            @Override
                            public void onDataResponse(int dataType, float v, HashMap hashMap) {
                                Log.d("SLEEP", "📥 Dato recibido -> dataType: " + dataType + " | data: " + hashMap);
                            }
                        }
                );
            }
        });

    }
}