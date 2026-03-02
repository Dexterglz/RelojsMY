package com.example.myhealthlife.ui.common.view;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myhealthlife.R;
import com.example.myhealthlife.databinding.FragmentParamBinding;
import com.example.myhealthlife.data.local.repository.ChartRepository;
import com.example.myhealthlife.ui.chart.ChartViewModel;

import org.jspecify.annotations.NonNull;

public abstract class ParamFragment extends Fragment {
    //Este fragment NO se usa directamente, solo se hereda.
    protected FragmentParamBinding binding;
    private FragmentParamBinding _binding;
    protected SharedChartViewModel viewModelShared;
    protected ChartViewModel vm;


    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        _binding = FragmentParamBinding.inflate(inflater, container, false);
        binding = _binding;
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);
        //Crear instancia de modelo comun
        viewModelShared = new ViewModelProvider(requireActivity())
                .get(SharedChartViewModel.class);
        //Definir unidad en textos
        viewModelShared.getUnit().observe(getViewLifecycleOwner(), u -> {
            if (binding == null) return;
            binding.unidad.setText(u);
            binding.lowestUnit.setText(u);
            binding.avgUnit.setText(u);
            binding.highestUnit.setText(u);
        });
        //Definir nombres de tabs
        binding.tabDay.tabText.setText(getString(R.string.day));
        binding.tabWeek.tabText.setText(getString(R.string.week));
        binding.tabMonth.tabText.setText(getString(R.string.month));
        //Iniciar instancia de ChartModel
        vm = new ViewModelProvider(requireActivity())
                .get(ChartViewModel.class);
        //Definir el intervalo inicial
        //vm.setInterval(ChartRepository.TimeInterval.TODAY_REPEAT);
        //Cambiar intervalos
        selectTab(0);
        vm.setInterval(ChartRepository.TimeInterval.TODAY_REPEAT);

        binding.tabDay.cardTabContainer.setOnClickListener(v ->{
                    vm.setInterval(ChartRepository.TimeInterval.TODAY_REPEAT);
                    selectTab(0);
                }
        );
        binding.tabWeek.cardTabContainer.setOnClickListener(v ->{
                    vm.setInterval(ChartRepository.TimeInterval.LAST_7_DAYS);
                    selectTab(1);
                }
        );
        binding.tabMonth.cardTabContainer.setOnClickListener(v ->{
                    vm.setInterval(ChartRepository.TimeInterval.LAST_30_DAYS);
                    selectTab(2);
                }
        );


        /*vm.getInterval().observe(requireActivity(), interval -> {
            binding.tabDay.cardTabContainer.setChecked(interval == ChartRepository.TimeInterval.TODAY_REPEAT);
            binding.tabDay.tabText.setTextColor(
                    interval == ChartRepository.TimeInterval.TODAY_REPEAT?
                            getResources().getColor(R.color.white):
                            getResources().getColor(R.color.black)
            );
            binding.tabWeek.cardTabContainer.setChecked(interval == ChartRepository.TimeInterval.LAST_7_DAYS);
            binding.tabWeek.tabText.setTextColor(
                    interval == ChartRepository.TimeInterval.LAST_7_DAYS?
                            getResources().getColor(R.color.white):
                            getResources().getColor(R.color.black)
            );
            binding.tabMonth.cardTabContainer.setChecked(interval == ChartRepository.TimeInterval.LAST_30_DAYS);
            binding.tabMonth.tabText.setTextColor(
                    interval == ChartRepository.TimeInterval.LAST_30_DAYS?
                            getResources().getColor(R.color.white):
                            getResources().getColor(R.color.black)
            );
        });*/
        setupViews();
        observeViewModel();
    }

    private void selectTab(int tab) {
        binding.tabDay.tabText.setTextColor(tab == 0 ? Color.WHITE : Color.BLACK);
        binding.tabDay.cardTabContainer.setChecked(tab == 0);
        binding.tabWeek.tabText.setTextColor(tab == 1 ? Color.WHITE : Color.BLACK);
        binding.tabWeek.cardTabContainer.setChecked(tab == 1);
        binding.tabMonth.tabText.setTextColor(tab == 2 ? Color.WHITE : Color.BLACK);
        binding.tabMonth.cardTabContainer.setChecked(tab == 2);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        _binding = null; // MUY IMPORTANTE
    }

    // Métodos que los hijos implementan
    protected abstract void setupViews();
    protected abstract void observeViewModel();



}
