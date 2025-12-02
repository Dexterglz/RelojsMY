package com.example.myhealthlife.model;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.myhealthlife.R;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

public class HealthItemView extends LinearLayout {

    // Views
    private TextView txtTime, txtTitle;
    private ImageView iconImage;
    private CardView cardContainer;
    private View timeContainer;

    // Atributos
    private String timeText = "00h 00 min";
    private String titleText = "Título";
    private int iconResource = R.drawable.heartbeat;
    private int backgroundColor = Color.WHITE;
    private int metallicColor = 0xFFB2EBF2;
    private float titleTextSize = 15f;
    private float timeTextSize = 13f;
    private boolean showTimeContainer = true;

    // Nuevos atributos para CardView
    private float cardElevation = 4f;
    private float cardCornerRadius = 8f;
    private int cardBackgroundColor = Color.WHITE;

    public HealthItemView(Context context) {
        super(context);
        init(context, null);
    }

    public HealthItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public HealthItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        // Inflar el layout
        LayoutInflater.from(context).inflate(R.layout.layout_health_item, this, true);

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
        txtTime = findViewById(R.id.txt_time);
        txtTitle = findViewById(R.id.txt_title);
        iconImage = findViewById(R.id.icon_image);
        cardContainer = findViewById(R.id.card_container);
        timeContainer = findViewById(R.id.time_container);
    }

    private void processAttributes(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HealthItemView);

        try {
            // Obtener valores de los atributos existentes
            timeText = typedArray.getString(R.styleable.HealthItemView_timeText);
            if (timeText == null || timeText.equals("0")) timeText = "--";

            titleText = typedArray.getString(R.styleable.HealthItemView_titleText);
            if (titleText == null) titleText = "Título";

            titleTextSize = typedArray.getDimension(R.styleable.HealthItemView_titleTextSize, titleTextSize);
            timeTextSize = typedArray.getDimension(R.styleable.HealthItemView_timeTextSize, timeTextSize);
            iconResource = typedArray.getResourceId(R.styleable.HealthItemView_iconSrc, R.drawable.heartbeat);
            backgroundColor = typedArray.getColor(R.styleable.HealthItemView_backgroundColor, Color.WHITE);
            metallicColor = typedArray.getColor(R.styleable.HealthItemView_metallicColor, 0xFFB2EBF2);
            showTimeContainer = typedArray.getBoolean(R.styleable.HealthItemView_showTimeContainer, true);

        } finally {
            typedArray.recycle();
        }
    }

    private void applyInitialConfiguration() {
        setTime(timeText);
        setTitle(titleText);
        setIcon(iconResource);
        setBackgroundColor(backgroundColor);
        setTimeContainerVisible(showTimeContainer);
        applyMetallicGradient();
        setTitleTextSize(titleTextSize);
        setTimeTextSize(timeTextSize);
    }

    // ========== MÉTODOS PÚBLICOS ==========

    /**
     * Establece el texto del tiempo
     */
    public void setTime(String time) {
        this.timeText = time;
        if (txtTime != null) {
            txtTime.setText(time);
        }
    }

    /**
     * Establece el título
     */
    public void setTitle(String title) {
        this.titleText = title;
        if (txtTitle != null) {
            txtTitle.setText(title);
        }
    }

    /**
     * Establece el tamaño del texto del título en sp
     */
    public void setTitleTextSize(float sizeInSp) {
        this.titleTextSize = sizeInSp;
        if (txtTitle != null) {
            txtTitle.setTextSize(sizeInSp);
        }
    }

    public void setTimeTextSize(float sizeInSp) {
        this.timeTextSize = sizeInSp;
        if (txtTime != null) {
            txtTime.setTextSize(sizeInSp);
        }
    }

    /**
     * Establece el ícono desde resource ID
     */
    public void setIcon(int drawableResId) {
        this.iconResource = drawableResId;
        if (iconImage != null) {
            iconImage.setImageResource(drawableResId);
        }
    }

    /**
     * Establece el color de fondo del contenido interno
     */
    public void setBackgroundColor(int color) {
        this.backgroundColor = color;
        if (cardContainer != null && cardContainer.getChildCount() > 0) {
            View content = cardContainer.getChildAt(0);
            if (content instanceof LinearLayout) {
                content.setBackgroundTintList(android.content.res.ColorStateList.valueOf(color));
            }
        }
    }

    /**
     * Muestra u oculta el contenedor del tiempo
     */
    public void setTimeContainerVisible(boolean visible) {
        this.showTimeContainer = visible;
        if (timeContainer != null) {
            timeContainer.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * Establece el color metálico y aplica el gradiente
     */
    public void setMetallicColor(int color) {
        this.metallicColor = color;
        applyMetallicGradient();
    }

    /**
     * Aplica el efecto de gradiente metálico al ícono
     */
    public void applyMetallicGradient() {
        if (iconImage != null) {
            MetallicTint.applyMetallicGradient(iconImage, metallicColor);
        }
    }

    // ========== NUEVOS MÉTODOS PARA CARDVIEW ==========

    /**
     * Establece la elevación del CardView
     */
    public void setCardElevation(float elevation) {
        this.cardElevation = elevation;
        if (cardContainer != null) {
            cardContainer.setCardElevation(elevation);
        }
    }

    /**
     * Establece el radio de las esquinas del CardView
     */
    public void setCardCornerRadius(float radius) {
        this.cardCornerRadius = radius;
        if (cardContainer != null) {
            cardContainer.setRadius(radius);
        }
    }

    /**
     * Establece el color de fondo del CardView
     */
    public void setCardBackgroundColor(int color) {
        this.cardBackgroundColor = color;
        if (cardContainer != null) {
            cardContainer.setCardBackgroundColor(color);
        }
    }

    /**
     * Configura todos los datos a la vez
     */
    public void setHealthItemData(String time, String title, int iconRes, int metallicColor) {
        setTime(time);
        setTitle(title);
        setIcon(iconRes);
        setMetallicColor(metallicColor);
    }

    // ========== MÉTODOS DE OBTENCIÓN ==========

    public String getTime() {
        return timeText;
    }

    public String getTitle() {
        return titleText;
    }

    public int getIconResource() {
        return iconResource;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public int getMetallicColor() {
        return metallicColor;
    }

    public boolean isTimeContainerVisible() {
        return showTimeContainer;
    }

    // Nuevos getters para propiedades del CardView
    public float getCardElevation() {
        return cardElevation;
    }

    public float getCardCornerRadius() {
        return cardCornerRadius;
    }

    public int getCardBackgroundColor() {
        return cardBackgroundColor;
    }

    // ========== MÉTODOS DE INTERACCIÓN ==========

    /**
     * Establece un listener de clic
     */
    public void setOnItemClickListener(OnClickListener listener) {
        cardContainer.setOnClickListener(listener);
    }

    /**
     * Establece un listener de clic largo
     */
    public void setOnItemLongClickListener(OnLongClickListener listener) {
        cardContainer.setOnLongClickListener(listener);
    }
}