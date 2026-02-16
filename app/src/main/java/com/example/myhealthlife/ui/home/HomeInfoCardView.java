package com.example.myhealthlife.ui.home;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.myhealthlife.R;
import com.google.android.material.card.MaterialCardView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import java.util.concurrent.TimeUnit;

public class HomeInfoCardView extends FrameLayout {

    private MaterialCardView rootCard, iconCard, statusDot;
    private LinearLayout container, last_card_update;
    private ImageView icon;
    private TextView title, value, unit, time;


    public void render(HomeCardState state) {
        setIcon(state.iconRes);
        setValue(state.value);
        setTitle(getContext().getString(state.titleRes));
        setUnit(state.unit);
        if(state.unit == "none"){
            statusDot.setVisibility(GONE);
            title.setTextSize(22f);
            unit.setVisibility(GONE);
            last_card_update.setVisibility(GONE);
        }
        //de acuerdo al valor applyStatus(value)
        //Nota: crear intervalos
    }

    public HomeInfoCardView(Context context) {
        super(context);
        init(context);
    }
    public HomeInfoCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    public HomeInfoCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_monitoring_health_card, this);
        rootCard = findViewById(R.id.rootCard);
        container = findViewById(R.id.container);
        last_card_update = findViewById(R.id.last_card_update);
        iconCard = findViewById(R.id.iconCard);
        statusDot = findViewById(R.id.statusDot);
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
    public void setLastUpdate(Context context, Long timestamp) {
        try {
            String timeStr = getSimpleRelativeTime(context, timestamp);
            time.setText(timeStr);
        } catch (NumberFormatException e) {
            // Si falla, mostrar el texto original
            if (time != null) {
                time.setText("");
                Log.d("fecha", String.valueOf(e));
            }
        }
    }
    public void setIcon(@DrawableRes int iconRes) {
        icon.setImageResource(iconRes);
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
