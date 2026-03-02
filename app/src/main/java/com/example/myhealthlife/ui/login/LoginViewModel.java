package com.example.myhealthlife.ui.login;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.myhealthlife.domain.util.ResourceWrapper;
import com.example.myhealthlife.data.local.repository.AuthRepository;

public class LoginViewModel extends AndroidViewModel {

    private AuthRepository repository;

    public LoginViewModel(@NonNull Application app) {
        super(app);
        repository = new AuthRepository(app);
    }

    public LiveData<ResourceWrapper<Boolean>> login(String email, String password) {
        return repository.login(email, password);
    }
}
