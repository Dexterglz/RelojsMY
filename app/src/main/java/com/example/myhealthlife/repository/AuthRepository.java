package com.example.myhealthlife.repository;
import com.example.myhealthlife.data.UserMapper;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.xr.scenecore.internal.Resource;

import com.example.myhealthlife.data.UserMapper;
import com.example.myhealthlife.data.local.dao.UserDao;
import com.example.myhealthlife.data.local.db.AppDatabase;
import com.example.myhealthlife.data.local.entity.UserEntity;
import com.example.myhealthlife.domain.LoginRequest;
import com.example.myhealthlife.domain.LoginResponse;
import com.example.myhealthlife.domain.util.ResourceWrapper;
import com.example.myhealthlife.domain.util.SessionManager;
import com.example.myhealthlife.io.response.ApiClient;
import com.example.myhealthlife.io.response.ApiService;
import com.example.myhealthlife.io.response.PatientResponse;
import com.example.myhealthlife.io.response.UserApiModel;
import com.example.myhealthlife.io.response.UserResponse;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    private final ApiService apiService;
    private final UserDao userDao;
    private final SessionManager sessionManager;

    public AuthRepository(Context context) {
        apiService = ApiClient.newClient().create(ApiService.class);
        userDao = AppDatabase.getInstance(context).userDao();
        sessionManager = new SessionManager(context);
    }

    public LiveData<ResourceWrapper<Boolean>> login(String email, String password) {

        MutableLiveData<ResourceWrapper<Boolean>> result = new MutableLiveData<>();
        result.setValue(ResourceWrapper.loading());

        apiService.loginUser(new LoginRequest(email, password))
                .enqueue(new Callback<LoginResponse>() {

                    @Override
                    public void onResponse(Call<LoginResponse> call,
                                           Response<LoginResponse> response) {

                        if (!response.isSuccessful() || response.body() == null) {
                            result.postValue(ResourceWrapper.error("Login inválido"));
                            return;
                        }

                        String token = response.body().getToken();
                        sessionManager.saveToken(token);

                        apiService.getPatientData("Bearer " + token)
                                .enqueue(new Callback<UserResponse>() {

                                    @Override
                                    public void onResponse(Call<UserResponse> call,
                                                           Response<UserResponse> response) {

                                        if (response.body() == null) {
                                            result.postValue(ResourceWrapper.error("Error usuario"));
                                            return;
                                        }

                                        UserEntity user = UserMapper.mapToEntity(response.body().user);


                                        Executors.newSingleThreadExecutor().execute(() -> {
                                            userDao.deleteUser();
                                            userDao.insertUser(user);
                                        });

                                        result.postValue(ResourceWrapper.success(true));
                                    }

                                    @Override
                                    public void onFailure(Call<UserResponse> call, Throwable t) {
                                        result.postValue(ResourceWrapper.error("Error conexión"));
                                    }
                                });
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        result.postValue(ResourceWrapper.error("Error conexión"));
                    }
                });

        return result;
    }
}
