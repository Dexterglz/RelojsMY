package com.example.myhealthlife.domain;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import androidx.core.graphics.drawable.DrawableKt;

public class MetallicTint {

    public static void applyMetallicGradient(ImageView icon, int baseColor) {
        icon.post(() -> {
            Drawable drawable = icon.getDrawable();
            if (drawable == null) return;

            int width = icon.getWidth();
            int height = icon.getHeight();

            // Generar colores metálicos basados en el color base
            int[] colors = generateMetallicColors(baseColor);

            // Crear un bitmap base del ícono
            Bitmap baseBitmap = DrawableKt.toBitmap(drawable, width, height, null);
            Bitmap resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            // Crear el degradado metálico
            LinearGradient metallicGradient = new LinearGradient(
                    0, 0, width, height,
                    colors, null, Shader.TileMode.MIRROR
            );

            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setShader(metallicGradient);

            // 💫 Aplica el degradado solo dentro del ícono (como tinte)
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));

            // Dibujar el ícono y aplicar la máscara
            Canvas canvas = new Canvas(resultBitmap);
            canvas.drawBitmap(baseBitmap, 0, 0, null);
            canvas.drawRect(0, 0, width, height, paint);

            // Mostrar el resultado
            icon.setImageBitmap(resultBitmap);
        });
    }

    /**
     * Genera una paleta de colores metálicos basada en un color base
     * @param baseColor Color base en formato 0xAARRGGBB
     * @return Array de 4 colores metálicos en armonía con el color base
     */
    private static int[] generateMetallicColors(int baseColor) {
        // Extraer componentes RGB del color base
        int red = (baseColor >> 16) & 0xFF;
        int green = (baseColor >> 8) & 0xFF;
        int blue = baseColor & 0xFF;
        int alpha = (baseColor >> 24) & 0xFF;

        // Ajustar la opacidad si es completamente opaco
        if (alpha == 0xFF) {
            alpha = 0xFF; // Mantener opaco
        }

        // Crear variaciones metálicas del color base
        return new int[]{
                // Versión más oscura y saturada (shadow)
                adjustColorBrightness(baseColor, 0.45f, 0.1f),
                // Color base ligeramente aclarado
                adjustColorBrightness(baseColor, 0.9f, 1f),
                // Versión más clara y brillante (highlight)
                adjustColorBrightness(baseColor, 1.5f, 3f)
        };
    }

    /**
     * Ajusta el brillo y saturación de un color
     * @param color Color original
     * @param brightnessFactor Factor de brillo (1.0 = sin cambio)
     * @param saturationFactor Factor de saturación (1.0 = sin cambio)
     * @return Color ajustado
     */
    private static int adjustColorBrightness(int color, float brightnessFactor, float saturationFactor) {
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;
        int alpha = (color >> 24) & 0xFF;

        // Convertir a HSL para ajustar saturación
        float[] hsv = new float[3];
        android.graphics.Color.RGBToHSV(red, green, blue, hsv);

        // Ajustar brillo y saturación
        hsv[1] = Math.min(1.0f, hsv[1] * saturationFactor); // Saturación
        hsv[2] = Math.min(1.0f, hsv[2] * brightnessFactor); // Brillo

        // Convertir de vuelta a RGB
        int rgb = android.graphics.Color.HSVToColor(hsv);

        // Reconstruir el color con el alpha original
        return (alpha << 24) | (rgb & 0x00FFFFFF);
    }

    // Método sobrecargado para usar colores por defecto
    public static void applyMetallicGradient(ImageView icon) {
        // Usar un azul metálico por defecto
        applyMetallicGradient(icon, 0xFF4FC3F7);
    }
}