package com.example.myhealthlife.domain.chart;

public class IntPoint implements ChartPoint {
    public long x;
    public int y;

    public IntPoint(long x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public long getX() {
        return x;
    }
}
