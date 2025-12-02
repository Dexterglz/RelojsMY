package com.example.myhealthlife.io.response;

public class HistorySendData {
    private String usuarioId;
    private int heartValue;
    private int oxygenValue;
    private int diastolicValue;
    private int systolicValue;
    private int respRateValue;
    private int bloodSugarValue;
    private int tempIntValue;
    private int tempFloatValue;
    private long timestampValue;

    // Constructor
    public HistorySendData(String usuarioId, int heartValue, int oxygenValue, int diastolicValue,int systolicValue,int respRateValue, int bloodSugarValue,int tempIntValue, int tempFloatValue, long timestampValue ) {
        this.usuarioId = usuarioId;
        this.heartValue = heartValue;
        this.oxygenValue = oxygenValue;
        this.diastolicValue = diastolicValue;
        this.systolicValue = systolicValue;
        this.respRateValue = respRateValue;
        this.bloodSugarValue = bloodSugarValue;
        this.tempIntValue = tempIntValue;
        this.tempFloatValue = tempFloatValue;
        this.timestampValue = timestampValue;
    }
}
