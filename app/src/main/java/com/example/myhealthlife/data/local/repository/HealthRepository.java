package com.example.myhealthlife.data.local.repository;

import android.util.Log;
import android.widget.Toast;

import com.example.myhealthlife.domain.ResponseBody;
import com.example.myhealthlife.io.response.ApiClient;
import com.example.myhealthlife.io.response.ApiService;
import com.example.myhealthlife.io.response.HistorySendData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HealthRepository {

    private final ApiService apiService;

    public HealthRepository() {
        apiService = ApiClient.newClient().create(ApiService.class);
    }

    public void enviarHistorial(String token, HistorySendData data) {

        apiService.agregarHistorial( data)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Log.d("API", "Historial enviado correctamente");
                        } else {
                            Log.e("API", "Error HTTP: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("API", "Fallo conexión", t);
                    }
                });
    }
}

