package com.example.myhealthlife.activities;

import static com.example.myhealthlife.model.AppUtils.filterShowTable;
import static com.example.myhealthlife.model.AppUtils.setParam;
import static com.example.myhealthlife.model.PrefsHelper.obtenerHistorial;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myhealthlife.R;
import com.example.myhealthlife.model.HistoryData;
import com.example.myhealthlife.model.TipoDato;
import com.yucheng.ycbtsdk.YCBTClient;
import com.yucheng.ycbtsdk.response.BleDataResponse;
import com.yucheng.ycbtsdk.response.BleRealDataResponse;

import java.util.ArrayList;
import java.util.HashMap;

public class HeartRateActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parameter_log);
        setParam(this, TipoDato.HEART_VALUE,getString(R.string.frecuencia_cardiaca),getString(R.string.bpm));
    }
}