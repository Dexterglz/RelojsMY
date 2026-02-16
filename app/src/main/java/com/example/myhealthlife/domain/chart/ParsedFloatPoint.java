package com.example.myhealthlife.domain.chart;

public class ParsedFloatPoint implements ChartPoint {

    public long x;
    public float y;

    // Caso 1: ya viene como float
    public ParsedFloatPoint(long x, float y) {
        this.x = x;
        this.y = y;
    }

    // Caso 2: viene separado en parte entera y decimal
    // ej: entero=12, decimal=34  →  12.34
    public ParsedFloatPoint(long x, int integerPart, int decimalPart) {
        this.x = x;
        this.y = parse(integerPart, decimalPart);
    }

    // Caso 3: viene como long ("12", "34" o "12.34")
    public ParsedFloatPoint(long x, String value) {
        this.x = x;
        this.y = Float.parseFloat(normalize(value));
    }

    private float parse(int integerPart, int decimalPart) {
        float divisor = (float) Math.pow(10, digits(decimalPart));
        return integerPart + (decimalPart / divisor);
    }

    private int digits(int n) {
        if (n == 0) return 1;
        return (int) Math.log10(n) + 1;
    }

    private String normalize(String v) {
        return v.contains(".") ? v : v + ".0";
    }

    @Override
    public long getX() {
        return x;
    }
}

