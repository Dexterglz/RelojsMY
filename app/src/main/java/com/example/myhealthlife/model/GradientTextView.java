package com.example.myhealthlife.model;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.text.Layout;
import android.util.AttributeSet;

import androidx.core.content.ContextCompat;

import com.example.myhealthlife.R;

public class GradientTextView extends androidx.appcompat.widget.AppCompatTextView {

    int primary = ContextCompat.getColor(getContext(), R.color.primary);
    int accent = ContextCompat.getColor(getContext(), R.color.accent);
    int secondary = ContextCompat.getColor(getContext(), R.color.secondary);

    private final int[] colors = new int[]{
            primary, // primary
            accent, // accent
            secondary  // secondary
    };


    public GradientTextView(Context context) {
        super(context);
    }

    public GradientTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GradientTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        CharSequence text = getText();
        Paint paint = getPaint();
        Layout layout = getLayout();

        if (layout == null || text == null) return;

        // 1️⃣ Medimos el texto COMO SI FUERA UNA SOLA LÍNEA
        String fullText = text.toString().replace("\n", "");
        float totalWidth = paint.measureText(fullText);

        // 2️⃣ Creamos UN SOLO degradado global
        LinearGradient gradient = new LinearGradient(
                0f, 0f, totalWidth, 0f,
                colors,
                null,
                Shader.TileMode.CLAMP
        );

        paint.setShader(gradient);

        float xOffset = 0f;

        // 3️⃣ Dibujamos cada línea manualmente
        for (int i = 0; i < layout.getLineCount(); i++) {

            int start = layout.getLineStart(i);
            int end = layout.getLineEnd(i);

            String lineText = text.subSequence(start, end).toString();

            float y = layout.getLineBaseline(i);

            // 🔥 Desplazamos el shader para que continúe
            Matrix matrix = new Matrix();
            matrix.setTranslate(-xOffset, 0);
            gradient.setLocalMatrix(matrix);

            canvas.drawText(lineText, 0, y, paint);

            // Acumulamos el ancho ya dibujado
            xOffset += paint.measureText(lineText);
        }
    }
}

