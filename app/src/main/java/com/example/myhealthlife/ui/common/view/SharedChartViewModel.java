package com.example.myhealthlife.ui.common.view;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedChartViewModel extends ViewModel {

    private final MutableLiveData<String> unit = new MutableLiveData<>();

    public LiveData<String> getUnit() {
        return unit;
    }

    public void setUnit(String u) {
        unit.setValue(u);
    }
}

