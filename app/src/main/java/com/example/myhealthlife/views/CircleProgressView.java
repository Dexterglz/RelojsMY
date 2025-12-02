package com.example.myhealthlife.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CircleProgressView extends View {

    private int percentage = 0;

    private Paint circlePaint;
    private Paint arcPaint;
    private Paint textPaint;

    public CircleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        circlePaint = new Paint();
        circlePaint.setColor(0xFFDDDDDD);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(20);
        circlePaint.setAntiAlias(true);

        arcPaint = new Paint();
        arcPaint.setColor(0xFF2196F3);
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeWidth(20);
        arcPaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setColor(0xFF000000);
        textPaint.setTextSize(64);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth() / 2;
        int h = getHeight() / 2;
        int radius = Math.min(w, h) - 20;

        canvas.drawCircle(w, h, radius, circlePaint);
        canvas.drawArc(w - radius, h - radius, w + radius, h + radius,
                -90, (360 * percentage) / 100f, false, arcPaint);

        canvas.drawText(percentage + "%", w, h + 24, textPaint);
    }

    public void setPercentage(int percent) {
        this.percentage = percent;
        invalidate();
    }
}
