package com.example.myhealthlife.model;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Objects;

public class HealthViewModel extends ViewModel {
    private SharedPreferences sharedPreferences;
    private MutableLiveData<String> healthHeart = new MutableLiveData<>();
    private MutableLiveData<String> healthHRV = new MutableLiveData<>();
    private MutableLiveData<String> healthCVVRR = new MutableLiveData<>();
    private MutableLiveData<String> healthStep = new MutableLiveData<>();
    private MutableLiveData<String> healthDBP = new MutableLiveData<>();
    private MutableLiveData<String> healthSBP = new MutableLiveData<>();
    private MutableLiveData<String> healthBloodPressure = new MutableLiveData<>();
    private MutableLiveData<String> healthBody = new MutableLiveData<>();
    private MutableLiveData<String> healthBloodSugar = new MutableLiveData<>();
    private MutableLiveData<String> healthOxygen = new MutableLiveData<>();
    private MutableLiveData<String> healthTemp = new MutableLiveData<>();
    private MutableLiveData<String> healthStartTime = new MutableLiveData<>();
    private MutableLiveData<String> healthRespRate = new MutableLiveData<>();

    public void sethealthRespRate(String value, Context context) {
        setParam(context,"health_resp",value,healthRespRate);
    }
    public void sethealthHeart(String value, Context context) {
        setParam(context,"health_heart",value,healthHeart);
    }
    public void sethealthHRV(String value, Context context) {
        setParam(context,"health_hrv",value,healthHRV);
    }
    public void sethealthCVVRR(String value, Context context) {
        setParam(context,"health_cvvrr",value,healthCVVRR);
    }
    public void sethealthStep(String value, Context context) {
        setParam(context,"health_step",value,healthStep);
    }
    public void sethealthDBP(String value, Context context) {
        setParam(context,"health_dbp",value,healthDBP);
    }
    public void sethealthSBP(String value, Context context) {
        setParam(context,"health_sbp",value,healthSBP);
    }
    public void setHealthBloodPressure(String value, Context context) {
        setParam(context,"health_blood",value,healthBloodPressure);
    }
    public void sethealthBody(String value, Context context) {
        setParam(context,"health_body",value,healthBody);
    }
    public void sethealthOxygen(String value, Context context) {
        setParam(context,"health_ox",value,healthOxygen);
    }
    public void sethealthTemp(String value, Context context) {
        setParam(context,"health_temp",value,healthTemp);
    }
    public void setHealthStartTime(String value, Context context) {
        setParam(context,"health_start",value,healthStartTime);
    }

    public LiveData<String> getHealthHeart() {return healthHeart;}
    public LiveData<String> getHealthHRV() {return healthHRV;}
    public LiveData<String> getHealthCVVRR() {return healthCVVRR;}
    public LiveData<String> getHealthStep() {return healthStep;}
    public LiveData<String> getHealthDBP() {return healthDBP;}
    public LiveData<String> getHealthSBP() {return healthSBP;}
    public LiveData<String> getHealthBody() {return healthBody;}
    public LiveData<String> getHealthBloodSugar() {return healthBloodSugar;}
    public LiveData<String> getHealthBloodPressure() {return healthBloodPressure;}
    public LiveData<String> getHealthOxygen() {return healthOxygen;}
    public LiveData<String> getHealthTemp() {return healthTemp;}
    public LiveData<String> getHealthStartTime() {return healthStartTime;}
    public LiveData<String> getHealthRespRate() {return healthRespRate;}

    private void setParam(Context context, String paramName, String param,  MutableLiveData paramL){
        SharedPreferences sharedPreferences = context.getSharedPreferences("health_prefs", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(paramName, param).apply();
        paramL.postValue(param);
    }
}
