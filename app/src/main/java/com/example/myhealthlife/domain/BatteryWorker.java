package com.example.myhealthlife.domain;

import android.content.Context;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.myhealthlife.data.local.repository.DeviceRepository;

import org.jspecify.annotations.NonNull;

public class BatteryWorker extends Worker {

    private final DeviceRepository repository;

    public BatteryWorker(@NonNull Context context,
                         @NonNull WorkerParameters params) {
        super(context, params);
        repository = new DeviceRepository(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        repository.checkBatteryAndNotify();
        return Result.success();
    }
}
