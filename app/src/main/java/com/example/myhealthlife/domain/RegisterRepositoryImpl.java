package com.example.myhealthlife.domain;

import android.telephony.ims.RegistrationManager;
import android.util.Log;

import com.example.myhealthlife.data.remote.RegisterResponse;
import com.example.myhealthlife.data.remote.mapper.RegisterRequestMapper;
import com.example.myhealthlife.domain.common.ErrorMapper;
import com.example.myhealthlife.domain.common.register.DomainError;
import com.example.myhealthlife.domain.common.register.RegistrationResult;
import com.example.myhealthlife.domain.model.RegisterUserData;
import com.example.myhealthlife.domain.repository.register.RegisterRepository;
import com.example.myhealthlife.io.response.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterRepositoryImpl implements RegisterRepository {

    private final ApiService apiService;
    private final ErrorMapper errorMapper;
    private final RegisterRequestMapper requestMapper;


    public RegisterRepositoryImpl(
            ApiService apiService,
            ErrorMapper errorMapper,
            RegisterRequestMapper requestMapper
    ) {
        this.apiService = apiService;
        this.errorMapper = errorMapper;
        this.requestMapper = requestMapper;
    }

    @Override
    public void register(
            RegisterUserData data,
            ResultCallback callback
    ) {

        apiService.registerUser(
                requestMapper.toRequest(data)
        ).enqueue(new Callback<RegisterResponse>() {

            @Override
            public void onResponse(
                    Call<RegisterResponse> call,
                    Response<RegisterResponse> response
            ) {
                Log.d("REGISTER_REPO", "Llamando API");
                Log.e("REGISTER_REPO",
                        "HTTP " + response.code() +
                                " | errorBody=" + response.errorBody());
                if (response.isSuccessful()) {
                    callback.onResult(
                            new RegistrationResult.Success()
                    );
                } else {
                    DomainError error = errorMapper.map(response.code(), null);

                    if (error == null) {
                        error = DomainError.NETWORK_ERROR;
                    }

                    callback.onResult(new RegistrationResult.Error(error));
                }
            }

            @Override
            public void onFailure(
                    Call<RegisterResponse> call,
                    Throwable t
            ) {
                callback.onResult(
                        new RegistrationResult.Error(
                                errorMapper.map(t)
                        )
                );
            }
        });
    }
}