package com.example.myhealthlife.domain;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhealthlife.repository.DeviceRepository;

import org.jspecify.annotations.NonNull;

public class DeviceViewModel extends AndroidViewModel {

    private final DeviceRepository repository;
    private final MutableLiveData<Integer> battery = new MutableLiveData<>();

    public DeviceViewModel(@NonNull Application app) {
        super(app);
        repository = new DeviceRepository(app);
    }

    public LiveData<Integer> getBattery() {
        return battery;
    }

    public void refreshBattery() {
        int value = repository.checkBatteryAndNotify();
        battery.postValue(value);
    }
}


