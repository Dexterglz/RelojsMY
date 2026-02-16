package com.example.myhealthlife.activities;

import static com.example.myhealthlife.domain.util.AppUtils.setParam;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myhealthlife.R;
import com.example.myhealthlife.domain.model.TipoDato;

public class TrygActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parameter_log);
        setParam(this,TipoDato.TRYG,getString(R.string.grasa),"?");
    }
    @Override
    //Puede modificarse el monitoreo, y asegurar que los datos se recuperen correctamente
    protected void onDestroy() {
        super.onDestroy();
    }

}
