package com.example.myhealthlife.activities;

import static com.example.myhealthlife.model.AppUtils.setParam;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myhealthlife.R;
import com.example.myhealthlife.model.TipoDato;

public class DBPActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parameter_log);
        setParam(this,TipoDato.DIASTOLIC_VALUE,getString(R.string.presion_arterial_diastolica),getString(R.string.mmhg));
    }
    @Override
    //Puede modificarse el monitoreo, y asegurar que los datos se recuperen correctamente
    protected void onDestroy() {
        super.onDestroy();
    }

}
