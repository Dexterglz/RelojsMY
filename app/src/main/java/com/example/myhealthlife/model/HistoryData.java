package com.example.myhealthlife.model;

import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.myhealthlife.R;

public class HistoryData {

    public int heartValue;
    public int hrvValue;
    public int cvrrValue;
    public int oxygenValue;
    public int stepValue;
    public int diastolicValue;
    public int systolicValue;
    public int respRateValue;
    public int bodyFatValue;
    public int bodyFatFracValue;
    public int bloodSugarValue;
    public int tempIntValue;
    public int tempFloatValue;
    public long timestamp;

    public HistoryData(int heartValue, int hrvValue, int cvrrValue, int oxygenValue, int stepValue,
                         int diastolicValue, int systolicValue, int respRateValue, int bodyFatValue,
                         int bodyFatFracValue, int bloodSugarValue, int tempIntValue, int tempFloatValue,
                         long timestamp) {

        this.heartValue = heartValue;
        this.hrvValue = hrvValue;
        this.cvrrValue = cvrrValue;
        this.oxygenValue = oxygenValue;
        this.stepValue = stepValue;
        this.diastolicValue = diastolicValue;
        this.systolicValue = systolicValue;
        this.respRateValue = respRateValue;
        this.bodyFatValue = bodyFatValue;
        this.bodyFatFracValue = bodyFatFracValue;
        this.bloodSugarValue = bloodSugarValue;
        this.tempIntValue = tempIntValue;
        this.tempFloatValue = tempFloatValue;
        this.timestamp = timestamp;
    }

    public long getTimestamp(){
        return this.timestamp;
    }
    public int getOxygenValue() { return oxygenValue; }
    public int getTempIntValue() { return tempIntValue; }
    public int getTempFloatValue() { return tempFloatValue; }
    public int getHeartValue() { return heartValue; }
    public int getSystolicValue() { return systolicValue; }
    public int getDiastolicValue() { return diastolicValue; }



    public static void checkAndNotify(HistoryData data, Context context) {
        StringBuilder alertMessage = new StringBuilder();

        // Validaciones básicas (ajusta a tus rangos clínicos)
        if (data.getHeartValue() < 60 || data.getHeartValue() > 100) {
            alertMessage.append("⚠️ Ritmo cardiaco anormal: ").append(data.getHeartValue()).append(" lpm\n");
        }
        if (data.getOxygenValue() < 95) {
            alertMessage.append("⚠️ Oxígeno bajo: ").append(data.getOxygenValue()).append("%\n");
        }
        if (data.getSystolicValue() < 90 || data.getSystolicValue() > 140) {
            alertMessage.append("⚠️ Presión sistólica anormal: ").append(data.getSystolicValue()).append(" mmHg\n");
        }
        if (data.getDiastolicValue() < 60 || data.getDiastolicValue() > 90) {
            alertMessage.append("⚠️ Presión diastólica anormal: ").append(data.getDiastolicValue()).append(" mmHg\n");
        }
        double temp = data.getTempIntValue() + (data.getTempFloatValue() / 10.0);
        if (temp < 36.0 || temp > 37.5) {
            alertMessage.append("⚠️ Temperatura fuera de rango: ").append(temp).append(" °C\n");
        }

        if (alertMessage.length() > 0) {
            sendNotification("Alerta de signos vitales", alertMessage.toString(), context);
        }
    }

    private static void sendNotification(String title, String message, Context context) {
        String channelId = "health_alerts";

        // Crear canal de notificación (solo una vez en API >= 26)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Alertas de Salud",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        // Construir notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.watch) // pon tu ícono en drawable
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
