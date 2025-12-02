package com.example.myhealthlife.activities;

import static com.example.myhealthlife.model.AppUtils.filterShowTable;
import static com.example.myhealthlife.model.AppUtils.setParam;
import static com.example.myhealthlife.model.PrefsHelper.obtenerHistorial;
import static com.example.myhealthlife.model.PrefsHelper.obtenerHistorial;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.myhealthlife.R;
import com.example.myhealthlife.model.HistoryData;
import com.example.myhealthlife.model.TipoDato;
import com.github.mikephil.charting.BuildConfig;
import com.yucheng.ycbtsdk.Constants;
import com.yucheng.ycbtsdk.YCBTClient;
import com.yucheng.ycbtsdk.response.BleDataResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class RespiratoryRateActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parameter_log);
        setParam(this, TipoDato.RESP_RATE_VALUE,getString(R.string.frecuencia_respiratoria),getString(R.string.rpm));
    }
}
