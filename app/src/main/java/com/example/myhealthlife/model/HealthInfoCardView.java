package com.example.myhealthlife.model;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.myhealthlife.R;
import com.google.android.material.card.MaterialCardView;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.view.View;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

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

    public void setLastUpdate(String text) {
        time.setText(text);
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
}
