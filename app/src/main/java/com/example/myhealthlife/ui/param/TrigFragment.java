package com.example.myhealthlife.ui.param;

import static com.example.myhealthlife.ui.chart.ChartViewModel.avgFromChart;
import static com.example.myhealthlife.ui.chart.ChartViewModel.chartHistoryHealth;
import static com.example.myhealthlife.ui.chart.ChartViewModel.maxFromChart;
import static com.example.myhealthlife.ui.chart.ChartViewModel.minFromChart;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhealthlife.R;
import com.example.myhealthlife.ui.common.HealthViewModel;
import com.example.myhealthlife.ui.common.view.ParamFragment;

import org.jspecify.annotations.NonNull;

public class TrigFragment extends ParamFragment {
    private HealthViewModel viewModel;
    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);
        viewModelShared.setUnit("mmol/L");
    }
    @Override
    protected void setupViews() {
    }
    @Override
    protected void observeViewModel() {
        //Crear instancia
        viewModel = new ViewModelProvider(this).get(HealthViewModel.class);
        //Establecer dato actual
        viewModel.trigValue().observe(getViewLifecycleOwner(), v -> {
            binding.datoActual.setText(v+"");
        });
        //Definir grafico
        chartHistoryHealth(
                getActivity(),
                R.id.chart_log,
                vm.getTrigChart()
        );
        //Definir datos relevantes
        minFromChart(vm.getTrigChart()).observe(getViewLifecycleOwner(), v -> {
            binding.lowest.setText(v+"");
        });
        maxFromChart(vm.getTrigChart()).observe(getViewLifecycleOwner(), v -> {
            binding.highest.setText(v+"");
        });
        avgFromChart(vm.getTrigChart()).observe(getViewLifecycleOwner(), v -> {
            binding.average.setText(v+"");
        });
    }
}
