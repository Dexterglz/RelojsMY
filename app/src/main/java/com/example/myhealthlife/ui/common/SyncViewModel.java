package com.example.myhealthlife.ui.common;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhealthlife.data.local.db.AppDatabase;
import com.example.myhealthlife.data.local.repository.SyncRepository;

import org.jspecify.annotations.NonNull;

public class SyncViewModel extends AndroidViewModel {

    private final SyncRepository repository;

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public SyncViewModel(@NonNull Application application) {
        super(application);
        repository = new SyncRepository(
                AppDatabase.getInstance(application)
        );
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void syncAll() {

        loading.postValue(true);

        repository.syncAll(
                () -> loading.postValue(false),
                err -> {
                    error.postValue(err);
                    loading.postValue(false);
                }
        );
    }
}

