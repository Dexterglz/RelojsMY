package com.example.myhealthlife.model;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.myhealthlife.R;
import com.example.myhealthlife.activities.BloodPressureActivity;
import com.example.myhealthlife.activities.CVRLogActivity;
import com.example.myhealthlife.activities.HRVLogActivity;
import com.example.myhealthlife.activities.HeartRateActivity;
import com.example.myhealthlife.activities.OxygenLogActivity;
import com.example.myhealthlife.activities.RespiratoryRateActivity;
import com.example.myhealthlife.activities.SleepActivity;
import com.example.myhealthlife.activities.TemperatureLogActivity;
import com.google.android.material.card.MaterialCardView;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.view.View;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import java.util.concurrent.TimeUnit;

public class HealthInfoCardView extends FrameLayout {

    private MaterialCardView rootCard, iconCard, statusDot;
    private LinearLayout container;
    private ImageView icon;
    private TextView title, value, unit, time;

    public HealthInfoCardView(Context context) {
        super(context);
        init(context);
    }
    public HealthInfoCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    public HealthInfoCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_monitoring_health_card, this);

        rootCard = findViewById(R.id.rootCard);
        container = findViewById(R.id.container);
        iconCard = findViewById(R.id.iconCard);
        //statusDot = findViewById(R.id.statusDot);

        icon = findViewById(R.id.icon);
        title = findViewById(R.id.title);
        value = findViewById(R.id.value);
        unit = findViewById(R.id.unit);
        time = findViewById(R.id.time);

    }

    /* ================= CONTENIDO ================= */

    public void setTitle(String text) {
        title.setText(text);
    }
    public void setValue(String text) {
        value.setText(text);
    }

    public void setUnit(String text) {
        unit.setText(text);
    }
    public void setLastUpdate(Context context, String timestampString) {
        try {
            long timestamp = Long.parseLong(timestampString);
            String timeStr = getSimpleRelativeTime(context, timestamp);
            time.setText(timeStr);
        } catch (NumberFormatException e) {
            // Si falla, mostrar el texto original
            if (time != null) {
                time.setText(timestampString);
                Log.d("fecha", String.valueOf(e));
            }
        }
    }
    public void setIcon(@DrawableRes int iconRes) {
        icon.setImageResource(iconRes);
    }

    /* ================= CONFIGURACIONES ESPECÍFICAS ================= */

    public void configureCard(Context context, TipoDato tipoDato) {
        setValue("--");
        if(tipoDato == TipoDato.BLOOD_PRESSURE) {
            setIcon(R.drawable.stethoscope);
            setTitle(context.getString(R.string.presion_arterial));
            setValue("--/--");
            setUnit("mmHg");
            setOnClickOpenActivity(BloodPressureActivity.class);
        }
        if(tipoDato == TipoDato.RESP_RATE_VALUE){
            setIcon(R.drawable.heart_pulse);
            setTitle(context.getString(R.string.frecuencia_respiratoria));
            setOnClickOpenActivity(RespiratoryRateActivity.class);
            setUnit("rpm");
        }
        if(tipoDato == TipoDato.OXYGEN_VALUE){
            setIcon(R.drawable.atom);
            setTitle(context.getString(R.string.saturacion_oxigeno));
            setOnClickOpenActivity(OxygenLogActivity.class);
            setUnit("%");
        }
        if(tipoDato == TipoDato.HEART_VALUE){
            setIcon(R.drawable.heartbeat);
            setTitle(context.getString(R.string.frecuencia_cardiaca));
            setOnClickOpenActivity(HeartRateActivity.class);
            setUnit("bpm");
        }
        if(tipoDato == TipoDato.TEMP){
            setIcon(R.drawable.thermometer);
            setTitle(context.getString(R.string.temperatura_corporal));
            setOnClickOpenActivity(TemperatureLogActivity.class);
            setUnit("°C");
        }
        if(tipoDato == TipoDato.HRV_VALUE){
            setIcon(R.drawable.heart_pulse);
            setTitle(context.getString(R.string.hrv));
            setOnClickOpenActivity(HRVLogActivity.class);
            setUnit("ms");
        }
        if(tipoDato == TipoDato.CVRR_VALUE){
            setIcon(R.drawable.heart_pulse);
            setTitle(context.getString(R.string.cvrr));
            setOnClickOpenActivity(CVRLogActivity.class);
            setUnit("%");
        }
        if(tipoDato == TipoDato.SLEEP){
            setIcon(R.drawable.bed);
            setTitle(context.getString(R.string.sleep));
            setOnClickOpenActivity(SleepActivity.class);
        }
        /*if(tipoDato == TipoDato.){}
        if(tipoDato == TipoDato.){}*/
    }

    /* ================= COLORES ================= */
    public void setCardBackground(@DrawableRes int backgroundRes) {
        container.setBackgroundResource(backgroundRes);
    }
    public void setStrokeColor(@ColorRes int color) {
        rootCard.setStrokeColor(ContextCompat.getColor(getContext(), color));
    }
    public void setIconBackground(@ColorRes int color) {
        iconCard.setCardBackgroundColor(
                ContextCompat.getColor(getContext(), color)
        );
    }
    public void setIconTint(@ColorRes int color) {
        icon.setColorFilter(
                ContextCompat.getColor(getContext(), color)
        );
    }
    public void setStatusDotColor(@ColorRes int color) {
        statusDot.setCardBackgroundColor(
                ContextCompat.getColor(getContext(), color)
        );
    }
    public void applyStatus(int status) {

        // Normalizamos el status (1–4)
        if (status < 1) status = 1;
        if (status > 4) status = 4;

        Context context = getContext();

        int backgroundRes = context.getResources()
                .getIdentifier("gradient_card_" + status, "drawable", context.getPackageName());

        int strokeColorRes = context.getResources()
                .getIdentifier("card_" + status + "_stroke", "color", context.getPackageName());

        int iconColorRes = context.getResources()
                .getIdentifier("card_" + status + "_icon", "color", context.getPackageName());

        if (backgroundRes != 0) {
            setCardBackground(backgroundRes);
        }

        if (strokeColorRes != 0) {
            setStrokeColor(strokeColorRes);
        }

        if (iconColorRes != 0) {
            setIconBackground(iconColorRes);
        }
    }
    /* ================= NAVEGACIÓN ================= */
    public void setOnClickOpenActivity(final Class<?> activity) {
        rootCard.setOnClickListener(v -> {
            Context context = getContext();
            Intent intent = new Intent(context, activity);
            context.startActivity(intent);
        });
    }

    /* ===== */

    /**
     * Versión simplificada que solo retorna "Hace x min" o "Hace >1 hr"
     */
    public static String getSimpleRelativeTime(Context context, long timestamp) {
        long now = System.currentTimeMillis();
        long diff = now - timestamp;

        if (diff < 0) {
            return context.getString(R.string.now);
        }

        // Menos de 1 hora
        if (diff < 3600000) {
            long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
            if (minutes == 0) {
                minutes = 1; // Mínimo 1 minuto
            }
            return context.getString(R.string.minutes_ago, minutes);
        }

        // Más de 1 hora
        return context.getString(R.string.more_than_one_hour_ago);
    }

}
