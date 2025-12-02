package com.example.myhealthlife.activities;

import static com.example.myhealthlife.model.PrefsHelper.obtenerHistorial;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myhealthlife.R;
import com.example.myhealthlife.io.response.ApiClient;
import com.example.myhealthlife.io.response.ApiService;
import com.example.myhealthlife.io.response.HistorySendData;
import com.example.myhealthlife.model.HistoryData;
import com.example.myhealthlife.model.LoginRequest;
import com.example.myhealthlife.model.LoginResponse;
import com.example.myhealthlife.model.ResponseBody;
import com.example.myhealthlife.ui.MainActivity;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestActivity extends AppCompatActivity {
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Obtenemos los últimos datos
        HistoryData h = obtenerHistorial(this).get( obtenerHistorial(this).size() - 1 );
        sendData(
                h.heartValue,
                h.oxygenValue,
                h.diastolicValue,
                h.systolicValue,
                h.respRateValue,
                h.bloodSugarValue,
                h.tempIntValue,
                h.tempFloatValue,
                h.timestamp
        );
    }

    public void sendData(
            int heartValue,
            int oxygenValue,
            int diastolccValue,
            int systolicValue,
            int respRateValue,
            int bloodSugarValue,
            int tempIntValue,
            int tempFloatValue,
            long timestamp
    ){
        prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
        String userId = prefs.getString("user_id", "1"); // 1 es valor por defecto si no existe
        HistorySendData nuevoRegistro = new HistorySendData(
                userId,             // usuarioId
                heartValue,         // heartValue
                oxygenValue,        // oxygenValue
                diastolccValue,     // diastolicValue
                systolicValue,      // systolicValue
                respRateValue,      // respRateValue
                bloodSugarValue,    // bloodSugarValue
                tempIntValue,       // tempIntValue
                tempFloatValue,     // tempFloatValue
                timestamp           // timestampValue
        );

        // Realiza la llamada

        ApiService apiService = ApiClient.newClient().create(ApiService.class);
        Call<ResponseBody> call = apiService.agregarHistorial(nuevoRegistro);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("API", "Registro agregado exitosamente");
                    Toast.makeText(getApplicationContext(), "Datos guardados", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        Log.e("API", "Error: " + response.errorBody().string());
                        Toast.makeText(getApplicationContext(), "Error: " + response.errorBody().string(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("API", "Fallo en la conexión: " + t.getMessage());
            }
        });
    }

}

