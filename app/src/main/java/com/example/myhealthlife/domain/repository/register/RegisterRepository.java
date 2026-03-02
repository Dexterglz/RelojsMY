package com.example.myhealthlife.domain.repository.register;


import com.example.myhealthlife.domain.common.register.RegistrationResult;
import com.example.myhealthlife.domain.model.RegisterUserData;

public interface RegisterRepository {

    interface ResultCallback {
        void onResult(RegistrationResult result);
    }

    void register(RegisterUserData data, ResultCallback callback);
}
