package com.example.myhealthlife.domain.chart;

public class DoubleIntPoint implements ChartPoint {
    public long x;
    public int y1;
    public int y2;

    public DoubleIntPoint(long x, int y1, int y2) {
        this.x = x;
        this.y1 = y1;
        this.y2 = y2;
    }

    @Override
    public long getX() {
        return x;
    }
}
