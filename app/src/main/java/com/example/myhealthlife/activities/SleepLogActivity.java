package com.example.myhealthlife.activities;

import static com.example.myhealthlife.model.AppUtils.setParam;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myhealthlife.R;
import com.example.myhealthlife.model.TipoDato;

public class SleepLogActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parameter_log);
        setParam(this, TipoDato.SLEEP,getString(R.string.hrv),"hrs");
    }
}
