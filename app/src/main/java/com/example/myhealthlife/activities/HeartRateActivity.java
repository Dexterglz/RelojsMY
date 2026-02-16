package com.example.myhealthlife.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myhealthlife.R;

public class HeartRateActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parameter_log);
        //setParam(this, TipoDato.HEART_VALUE,getString(R.string.frecuencia_cardiaca),getString(R.string.bpm));

    }
}