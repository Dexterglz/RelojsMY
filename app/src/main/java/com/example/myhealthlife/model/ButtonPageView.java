package com.example.myhealthlife.model;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.example.myhealthlife.R;

import java.util.Objects;

public class ButtonPageView extends LinearLayout {

    // Views
    private TextView team_name_text,optional_text;
    private ImageView left_icon, optional_icon, right_icon;
    private CardView cardContainer;
    private View timeContainer;

    // Atributos
    private String buttonText = "";
    private String buttonTextOptional = "";
    private int iconButtonSrc = 0;
    private int buttonIconOptional = 0;

    public ButtonPageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ButtonPageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public ButtonPageView(Context context) {
        super(context);
    }

    private void init(Context context, AttributeSet attrs) {
        // Inflar el layout
        LayoutInflater.from(context).inflate(R.layout.layout_button_page, this, true);

        // Binding de vistas
        bindViews();

        // Procesar atributos personalizados
        if (attrs != null) {
            processAttributes(context, attrs);
        }

        // Aplicar configuración inicial
        applyInitialConfiguration();
    }

    private void bindViews() {
        left_icon = findViewById(R.id.left_icon);
        team_name_text  = findViewById(R.id.team_name_text);
        optional_text  = findViewById(R.id.optional_text) !=null ? findViewById(R.id.optional_text) : null;
        optional_icon  = findViewById(R.id.optional_icon) !=null ? findViewById(R.id.optional_icon) : null;
        cardContainer = findViewById(R.id.card_container);
    }

    private void processAttributes(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ButtonPageView);

        try {
            // Obtener valores de los atributos existentes
            buttonText = typedArray.getString(R.styleable.ButtonPageView_buttonText);
            if (buttonText == null) buttonText = "...";

            buttonTextOptional = typedArray.getString(R.styleable.ButtonPageView_buttonTextOptional);

            iconButtonSrc = typedArray.getResourceId(R.styleable.ButtonPageView_iconButtonSrc, R.drawable.heartbeat);

            buttonIconOptional = typedArray.getResourceId(R.styleable.ButtonPageView_buttonIconOptional, 0);


        } finally {
            typedArray.recycle();
        }
    }

    private void applyInitialConfiguration() {
        setButtonText(buttonText);
        setButtonIcon(iconButtonSrc);
        setTextOpcional(buttonTextOptional);
        if(iconButtonSrc != 0) setIconOpcional(buttonIconOptional);
    }

    // ========== MÉTODOS PÚBLICOS ==========

    /**
     * Establece el título
     */
    public void setButtonText(String title) {
        this.buttonText = title;
        if (team_name_text != null) {
            team_name_text.setText(title);
        }
    }

    /**
     * Establece el ícono desde resource ID
     */
    public void setButtonIcon(int drawableResId) {
        this.iconButtonSrc = drawableResId;
        if (left_icon != null) {
            left_icon.setImageResource(drawableResId);
        }
    }

    /**
     * Muestra el texto Opcional
     */
    public void setTextOpcional(String title) {
        this.buttonTextOptional = title;
        if (optional_text != null) {
            optional_text.setVisibility(View.VISIBLE);
            optional_text.setText(title);
        }
    }

    /**
     * Muestra el Icono Opcional
     */
    public void setIconOpcional(int drawableResId) {
        this.buttonIconOptional = drawableResId;
        if (optional_icon != null) {
            optional_icon.setVisibility(View.VISIBLE);
            optional_icon.setImageResource(drawableResId);
        }
    }

    /**
     * Configura todos los datos a la vez
     */
    public void setHealthItemData(String title, int iconRes) {
        setButtonText(title);
        setButtonIcon(iconRes);
    }

    // ========== MÉTODOS DE INTERACCIÓN ==========

    /**
     * Establece un listener de clic
     */
    public void setOnButtonClickListener(OnClickListener listener) {
        cardContainer.setOnClickListener(listener);
    }

    /**
     * Establece un listener de clic largo
     */
    public void setOnItemLongClickListener(OnLongClickListener listener) {
        cardContainer.setOnLongClickListener(listener);
    }
}