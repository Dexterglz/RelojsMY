package com.example.myhealthlife.io.response;
public class HistorySendData {

    private String usuarioId;
    private Integer heartValue;
    private Integer oxygenValue;
    private Integer diastolicValue;
    private Integer systolicValue;
    private Integer respRateValue;
    private Integer bloodSugarValue;
    private Integer tempIntValue;
    private Integer tempFloatValue;
    private Long timestampValue;
    private Float grasa;
    private Integer sueno;
    private Float acido_urico;
    private Integer pasos;
    private Integer calorias;
    private Float cvrr;
    private Float hrv;

    public HistorySendData(
            String usuarioId,
            Integer heartValue,
            Integer oxygenValue,
            Integer diastolicValue,
            Integer systolicValue,
            Integer respRateValue,
            Integer bloodSugarValue,
            Integer tempIntValue,
            Integer tempFloatValue,
            Long timestampValue,
            Float  grasa,
            Integer sueno,
            Float acido_urico,
            Integer pasos,
            Integer calorias,
            Float cvrr,
            Float hrv
    ) {
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
        this.grasa = grasa;
        this.sueno = sueno;
        this.acido_urico = acido_urico;
        this.pasos = pasos;
        this.calorias = calorias;
        this.cvrr = cvrr;
        this.hrv = hrv;
    }
}