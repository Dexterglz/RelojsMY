package com.example.myhealthlife.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myhealthlife.R;
import com.example.myhealthlife.activities.OxygenMainActivity;
import com.example.myhealthlife.views.CircleProgressView;
import com.yucheng.ycbtsdk.Constants;
import com.yucheng.ycbtsdk.YCBTClient;
import com.yucheng.ycbtsdk.response.BleDataResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class OxygenMainActivity extends AppCompatActivity {

    private CircleProgressView circleProgressView;
    private TextView dateTimeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oxygen_log);

        /*circleProgressView = findViewById(R.id.progress_spo2);
        dateTimeText = findViewById(R.id.date_time_text);

        Button btnMeasure = findViewById(R.id.btn_measure);
        Button btnHistory = findViewById(R.id.btn_history);
        Button btnBack = findViewById(R.id.btn_back);

        btnMeasure.setOnClickListener(v -> obtenerUltimaOxigenacion());
        btnHistory.setOnClickListener(v -> startActivity(new Intent(this, OxygenLevelActivity.class)));
        btnBack.setOnClickListener(v -> finish());

        obtenerUltimaOxigenacion();*/
    }

    private void obtenerUltimaOxigenacion() {
        YCBTClient.healthHistoryData(Constants.DATATYPE.Health_HistoryAll, new BleDataResponse() {
            @Override
            public void onDataResponse(int dataType, float v, HashMap hashMap) {
                runOnUiThread(() -> {
                    if (hashMap != null && hashMap.containsKey("data")) {
                        ArrayList<HashMap<String, Object>> dataList =
                                (ArrayList<HashMap<String, Object>>) hashMap.get("data");

                        int lastOxygen = -1;
                        long lastTime = 0;

                        for (HashMap<String, Object> entry : dataList) {
                            if (entry.containsKey("OOValue")) {
                                int oxygen = (int) entry.get("OOValue");
                                long time = (long) entry.get("startTime");

                                if (time > lastTime) {
                                    lastOxygen = oxygen;
                                    lastTime = time;
                                }
                            }
                        }

                        if (lastOxygen != -1) {
                            circleProgressView.setPercentage(lastOxygen);
                            String dateFormatted = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                                    .format(new Date(lastTime));
                            dateTimeText.setText(dateFormatted);
                        } else {
                            dateTimeText.setText("No hay datos");
                        }
                    }
                });
            }
        });
    }
}
