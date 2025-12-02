package com.example.myhealthlife.model;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import java.util.Random;

public class WaveBackgroundView extends View {
    private Paint wavePaint;
    private Path wavePath;
    private Random random;

    // Configuración de las ondas
    private float amplitude = 40f;
    private float frequency = 0.01f;
    private float phase = 0f;
    private float speed = 0.008f;

    // Colores sutiles para las ondas
    private int[] waveColors = {
            Color.argb(30, 173, 216, 230), // Light blue con baja opacidad
            Color.argb(25, 135, 206, 250), // Light sky blue
            Color.argb(20, 176, 224, 230)  // Powder blue
    };

    private float[] waveOffsets;

    public WaveBackgroundView(Context context) {
        super(context);
        init();
    }

    public WaveBackgroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WaveBackgroundView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        wavePaint = new Paint();
        wavePaint.setStyle(Paint.Style.FILL);
        wavePaint.setAntiAlias(true);

        wavePath = new Path();
        random = new Random();

        // Inicializar offsets aleatorios para cada onda
        waveOffsets = new float[waveColors.length];
        for (int i = 0; i < waveOffsets.length; i++) {
            waveOffsets[i] = random.nextFloat() * 100;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        // Dibujar múltiples ondas con diferentes colores y fases
        for (int i = 0; i < waveColors.length; i++) {
            drawWave(canvas, width, height, i);
        }

        // Actualizar fase para animación
        phase += speed;

        // Invalidar la vista para continuar la animación
        postInvalidateOnAnimation();
    }

    private void drawWave(Canvas canvas, int width, int height, int waveIndex) {
        wavePath.reset();

        float currentAmplitude = amplitude * (0.9f + 0.1f * waveIndex);
        float currentFrequency = frequency * (0.9f + 0.1f * waveIndex);
        float currentPhase = phase + waveOffsets[waveIndex];

        // Empezar el path
        wavePath.moveTo(0, height);

        // Crear puntos para la onda
        for (int x = 0; x <= width; x += 5) {
            float y = (float) (height * 0.91f -
                    currentAmplitude * Math.sin(currentFrequency * x + currentPhase));
            wavePath.lineTo(x, y);
        }

        // Completar el path para formar un área cerrada
        wavePath.lineTo(width, height);
        wavePath.close();

        // Configurar color y dibujar
        wavePaint.setColor(waveColors[waveIndex]);
        canvas.drawPath(wavePath, wavePaint);
    }

    // Métodos para personalizar la animación
    public void setAmplitude(float amplitude) {
        this.amplitude = amplitude;
        invalidate();
    }

    public void setFrequency(float frequency) {
        this.frequency = frequency;
        invalidate();
    }

    public void setSpeed(float speed) {
        this.speed = speed;
        invalidate();
    }

    public void setWaveColors(int[] colors) {
        this.waveColors = colors;
        waveOffsets = new float[colors.length];
        for (int i = 0; i < waveOffsets.length; i++) {
            waveOffsets[i] = random.nextFloat() * 100;
        }
        invalidate();
    }
}