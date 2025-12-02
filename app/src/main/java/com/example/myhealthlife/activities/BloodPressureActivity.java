package com.example.myhealthlife.activities;

import static com.example.myhealthlife.model.AppUtils.filterShowTable;
import static com.example.myhealthlife.model.AppUtils.graficarDatosMedicos;
import static com.example.myhealthlife.model.AppUtils.prepararDatosGrafico;
import static com.example.myhealthlife.model.AppUtils.setDashboardParam;
import static com.example.myhealthlife.model.AppUtils.setParam;
import static com.example.myhealthlife.model.PrefsHelper.obtenerHistorial;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.myhealthlife.R;
import com.example.myhealthlife.model.AppUtils;
import com.example.myhealthlife.model.HistoryData;
import com.example.myhealthlife.model.TabManager;
import com.example.myhealthlife.model.TipoDato;
import com.yucheng.ycbtsdk.YCBTClient;
import com.yucheng.ycbtsdk.response.BleDataResponse;
import com.yucheng.ycbtsdk.response.BleRealDataResponse;

import com.example.myhealthlife.model.TipoDato.*;
import com.example.myhealthlife.model.AppUtils.*;

import java.util.ArrayList;
import java.util.HashMap;

public class BloodPressureActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parameter_log);
        setParam(this,TipoDato.BLOOD_SUGAR_VALUE,getString(R.string.presion_arterial),getString(R.string.mmhg));
    }
    @Override
    //Puede modificarse el monitoreo, y asegurar que los datos se recuperen correctamente
    protected void onDestroy() {
        super.onDestroy();
    }

}
