package com.example.myhealthlife.model;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.myhealthlife.R;

public class AnimatedCircularProgress extends View {

    private Paint progressPaint;
    private Paint backgroundPaint, firstBackgroundPaint, fillPaint;
    private RectF rectF;
    private float progress = 0f;
    private float maxProgress = 100f;
    private float progressText = 0;

    private int[] gradientColors = {Color.parseColor("#4CB56F"), Color.parseColor("#4CB5AB")};
    private LinearGradient gradient;

    public AnimatedCircularProgress(Context context) {
        super(context);
        init();
    }

    public AnimatedCircularProgress(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setProgressText(float progress) {
        this.progressText = progress;
        invalidate(); // Esto fuerza a redibujar la vista
    }

    private void init() {
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(Color.parseColor("#48605E"));
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(55f);

        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(55f);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);

        rectF = new RectF();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        rectF.set(50, 50, w - 50, h - 50);
        gradient = new LinearGradient(0, 0, w, h, gradientColors, null, Shader.TileMode.MIRROR);
        progressPaint.setShader(gradient);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Fondo circular
        canvas.drawArc(rectF, 0, 360, false, backgroundPaint);

        // Progreso animado
        canvas.drawArc(rectF, -90, (progress / maxProgress) * 360, false, progressPaint);

        // Texto centrado
        Paint titlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        titlePaint.setColor(Color.WHITE);
        titlePaint.setTextSize(40f);
        titlePaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Pasos", getWidth() / 2f, getHeight() / 2f -10, titlePaint);

        String title = "Pasos";
        float textX = getWidth() / 2f;
        float textY = getHeight() / 2f - 30;

        /*Drawable iconDrawable = ContextCompat.getDrawable(getContext(), R.drawable.sneakers);
        int iconSize = 55;
        int left = (int) (textX - (titlePaint.measureText(title) / 2) - iconSize + 110);
        int top = (int) (textY - iconSize / 2) - 130;
        iconDrawable.setBounds(left, top, left + iconSize, top + iconSize);
        iconDrawable.draw(canvas);*/

        // Texto centrado
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(55f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText((int) progressText + "", getWidth() / 2f, getHeight() / 2f + 55, textPaint);
    }

    // Animar el progreso
    public void setProgressWithAnimation(float newProgress) {
        ValueAnimator animator = ValueAnimator.ofFloat(progress, newProgress);
        animator.setDuration(1500);
        animator.addUpdateListener(animation -> {
            progress = (float) animation.getAnimatedValue();
            invalidate();
        });
        animator.start();
    }
}

