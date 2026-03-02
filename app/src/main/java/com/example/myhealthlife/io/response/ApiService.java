package com.example.myhealthlife.io.response;

import com.example.myhealthlife.data.remote.LoginResponse;
import com.example.myhealthlife.data.remote.LoginRequest;
import com.example.myhealthlife.data.remote.RegisterRequest;
import com.example.myhealthlife.data.remote.RegisterResponse;
import com.example.myhealthlife.domain.ResponseBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {
    @POST("auth/login") // Ruta de tu endpoint (ej: "auth/login")
    @Headers("Content-Type: application/json")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest); // @Body = JSON

    @POST("auth/signup")
    @Headers("Content-Type: application/json")
    Call<RegisterResponse> registerUser(@Body RegisterRequest registerRequest);

    @POST("datosmedicos")
    @Headers("Content-Type: application/json")
    Call<ResponseBody> agregarHistorial(@Body HistorySendData registro);

    @GET("users")
    Call<UserResponse> getPatientData(@Header("Authorization") String token);

}