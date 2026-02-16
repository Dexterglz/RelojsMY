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

public class UricAcidFragment extends ParamFragment {
    private HealthViewModel viewModel;
    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);
        viewModelShared.setUnit("µmol/L");
    }
    @Override
    protected void setupViews() {
    }
    @Override
    protected void observeViewModel() {
        //Crear instancia
        viewModel = new ViewModelProvider(this).get(HealthViewModel.class);
        //Establecer dato actual
        viewModel.acidValue().observe(getViewLifecycleOwner(), v -> {
            binding.datoActual.setText(v+"");
        });
        //Definir grafico
        chartHistoryHealth(
                getActivity(),
                R.id.chart_log,
                vm.getAcidChart()
        );
        //Definir datos relevantes
        minFromChart(vm.getAcidChart()).observe(getViewLifecycleOwner(), v -> {
            binding.lowest.setText(v+"");
        });
        maxFromChart(vm.getAcidChart()).observe(getViewLifecycleOwner(), v -> {
            binding.highest.setText(v+"");
        });
        avgFromChart(vm.getAcidChart()).observe(getViewLifecycleOwner(), v -> {
            binding.average.setText(v+"");
        });
    }
}
