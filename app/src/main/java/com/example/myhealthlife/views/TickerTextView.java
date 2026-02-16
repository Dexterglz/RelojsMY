package com.example.myhealthlife.views;

import android.content.Context;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;
import android.os.Handler;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

public class TickerTextView extends AppCompatTextView {

    private Scroller scroller;
    private int speedPxPerSecond = 80; // VELOCIDAD (px/seg)
    private int pauseMillis = 1000;     // PAUSA ENTRE VUELTAS
    private final Handler handler = new Handler();

    public TickerTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setSingleLine(true);
        setHorizontallyScrolling(true);

        scroller = new Scroller(getContext(), new LinearInterpolator());
        setScroller(scroller);
    }

    public void startScroll() {
        handler.postDelayed(this::startInternalScroll, 300);
    }

    private void startInternalScroll() {
        int textWidth = (int) getPaint().measureText(getText().toString());
        int viewWidth = getWidth();

        int distance = textWidth + viewWidth;
        int duration = (distance * 1000) / speedPxPerSecond;

        scroller.startScroll(
                -viewWidth,
                0,
                distance,
                0,
                duration
        );

        invalidate();

        handler.postDelayed(this::startInternalScroll, duration + pauseMillis);
    }

    public void stopScroll() {
        handler.removeCallbacksAndMessages(null);
        scroller.forceFinished(true);
    }

    public void setSpeed(int pxPerSecond) {
        this.speedPxPerSecond = pxPerSecond;
    }

    public void setPauseMillis(int millis) {
        this.pauseMillis = millis;
    }
}


