package com.example.myhealthlife.activities;
import static com.example.myhealthlife.ui.chart.ChartViewModel.chartHistoryHealth;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhealthlife.R;
import com.example.myhealthlife.ui.chart.ChartViewModel;
import com.example.myhealthlife.repository.ChartRepository;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class SleepActivity extends AppCompatActivity {

    private TextView txtTempValue;

    SimpleDateFormat hourFormat = new SimpleDateFormat("H", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);


    }
}