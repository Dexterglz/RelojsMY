package com.example.myhealthlife.model;

import static android.provider.Settings.System.getString;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.example.myhealthlife.R;

import java.text.DecimalFormat;
import java.util.Objects;

public class CustomMarkerView extends MarkerView {

    private final TextView tvContent;
    private final LinearLayout marker;
    private final String[] xLabels;
    private final String interval, parametro;

    public CustomMarkerView(Context context, int layoutResource, String[] xLabels, String interv, String param) {
        super(context, layoutResource);
        this.xLabels = xLabels;
        tvContent = findViewById(R.id.tvContent);
        marker = findViewById(R.id.custom_marker);
        interval = interv;
        parametro = param;
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        int xIndex = Math.round(e.getX());
        String hora = (xIndex >= 0 && xIndex < xLabels.length) ? xLabels[xIndex] : "";
        String value = String.valueOf(e.getY());

        String valueText = value;
        if(!Objects.equals(parametro, "temp")){
            valueText = String.valueOf((int) e.getY());
        }

        String icono = "🕑";
        if(!Objects.equals(interval, "hoy")){
            icono = "🗓️";
        }

        String iconoValue = "📋";
        String unit = "";
        if(parametro == "heartValue"){
            iconoValue = "❤️";
            unit = getContext().getString(R.string.bpm);
        } else if(parametro == "hrvValue"){
            iconoValue = "📊";
            unit = getContext().getString(R.string.ms);
        } else if(parametro == "cvrrValue"){
            iconoValue = "🔄";
            unit = getContext().getString(R.string.percent);
        } else if(parametro == "oxygenValue"){
            iconoValue = "💨";
            unit = getContext().getString(R.string.percent);
        }
        else if(parametro == "diastolicValue"){
            iconoValue = "🩺";
            unit = getContext().getString(R.string.mmhg);
        } else if(parametro == "systolicValue"){
            iconoValue = "💓";
            unit = getContext().getString(R.string.mmhg);
        } else if(parametro == "respRateValue"){
            iconoValue = "🌬️";
            unit = getContext().getString(R.string.rpm);
        } else if(parametro == "bodyFatFracValue"){
            iconoValue = "📈";
            unit = getContext().getString(R.string.percent);
        } else if(parametro == "bloodSugarValue"){
            iconoValue = "🩸";
            unit = getContext().getString(R.string.mgdl);
        } else if(parametro == "temp"){
            iconoValue = "🌡️";
            unit = getContext().getString(R.string.celsius);
        }

        if(!value.equals("0.0")){
            marker.setVisibility(VISIBLE);
            tvContent.setText(icono+" "+hora + "\n"+iconoValue+" " + valueText+" "+unit);
        }else{
            marker.setVisibility(GONE);
        }
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2f), -getHeight() - 10);
    }
}

