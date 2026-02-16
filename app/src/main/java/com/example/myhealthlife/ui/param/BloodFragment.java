package com.example.myhealthlife.ui.param;
import static com.example.myhealthlife.ui.chart.ChartViewModel.avgFromChart;
import static com.example.myhealthlife.ui.chart.ChartViewModel.chartDoubleIntHealthBar;
import static com.example.myhealthlife.ui.chart.ChartViewModel.chartHistoryHealth;
import static com.example.myhealthlife.ui.chart.ChartViewModel.maxFromChart;
import static com.example.myhealthlife.ui.chart.ChartViewModel.minFromChart;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhealthlife.R;
import com.example.myhealthlife.repository.ChartRepository;
import com.example.myhealthlife.ui.common.HealthViewModel;
import com.example.myhealthlife.ui.common.view.ParamFragment;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;

import org.jspecify.annotations.NonNull;

public class BloodFragment extends ParamFragment {
    private HealthViewModel viewModel;
    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);
        viewModelShared.setUnit("sb/dp");
        LineChart lineChart = view.findViewById(R.id.chart_log);
        BarChart barChart = view.findViewById(R.id.chart_log_bar);
        lineChart.setVisibility(View.GONE);
        barChart.setVisibility(View.VISIBLE);
    }
    @Override
    protected void setupViews() {
    }
    @Override
    protected void observeViewModel() {
        //Crear instancia
        viewModel = new ViewModelProvider(this).get(HealthViewModel.class);
        //Establecer dato actual
        viewModel.bloodValue().observe(getViewLifecycleOwner(), v -> {
            binding.datoActual.setText(v+"");
        });

        //Definir grafico
        chartDoubleIntHealthBar(
                getActivity(),
                R.id.chart_log_bar,
                vm.getBloodChart()
        );
        //Definir datos relevantes
        minFromChart(vm.getBloodChart()).observe(getViewLifecycleOwner(), v -> {
            binding.lowest.setText(v+"");
        });
        maxFromChart(vm.getBloodChart()).observe(getViewLifecycleOwner(), v -> {
            binding.highest.setText(v+"");
        });
        avgFromChart(vm.getBloodChart()).observe(getViewLifecycleOwner(), v -> {
            binding.average.setText(v+"");
        });
    }
}
