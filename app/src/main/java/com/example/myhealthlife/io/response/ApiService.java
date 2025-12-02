package com.example.myhealthlife.io.response;

import com.example.myhealthlife.model.HistoryData;
import com.example.myhealthlife.model.LoginResponse;
import com.example.myhealthlife.model.LoginRequest;
import com.example.myhealthlife.model.RegisterRequest;
import com.example.myhealthlife.model.RegisterResponse;
import com.example.myhealthlife.model.ResponseBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {
    @POST("auth/login") // Ruta de tu endpoint (ej: "auth/login")
    @Headers("Content-Type: application/json") // Fuerza el encabezado JSON
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest); // @Body = JSON

    @POST("auth/signup")
    @Headers("Content-Type: application/json") // Fuerza el encabezado JSON
    Call<RegisterResponse> registerUser(@Body RegisterRequest registerRequest);

    @POST("datosmedicos")
    @Headers("Content-Type: application/json") // Fuerza el encabezado JSON
    Call<ResponseBody> agregarHistorial(@Body HistorySendData registro);

    @GET("users")
    Call<PatientContainer> getPatientData(@Header("Authorization") String token);

}