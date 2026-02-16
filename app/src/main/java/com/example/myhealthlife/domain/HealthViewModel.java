package com.example.myhealthlife.domain;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HealthViewModel extends ViewModel {
    private SharedPreferences sharedPreferences;
    private static MutableLiveData<String> healthHeart = new MutableLiveData<>();
    private static MutableLiveData<String> healthHRV = new MutableLiveData<>();
    private MutableLiveData<String> healthHRVCVRR = new MutableLiveData<>();
    private static MutableLiveData<String> healthCVVRR = new MutableLiveData<>();
    private MutableLiveData<String> healthStep = new MutableLiveData<>();
    private static MutableLiveData<String> healthDBP = new MutableLiveData<>();
    private static MutableLiveData<String> healthSBP = new MutableLiveData<>();
    private static MutableLiveData<String> healthBloodPressure = new MutableLiveData<>();
    private MutableLiveData<String> healthBody = new MutableLiveData<>();
    private static MutableLiveData<String> healthBloodSugar = new MutableLiveData<>();
    private static MutableLiveData<String> healthOxygen = new MutableLiveData<>();
    private static MutableLiveData<String> healthTemp = new MutableLiveData<>();
    private static MutableLiveData<String> healthStartTime = new MutableLiveData<>();
    private static MutableLiveData<String> healthRespRate = new MutableLiveData<>();
    // Variables para almacenar los datos
    private MutableLiveData<String> sleepStartTime = new MutableLiveData<>() ;
    private MutableLiveData<String> sleepEndTime = new MutableLiveData<>() ;
    private static MutableLiveData<String> sleepDuration = new MutableLiveData<>() ;
    private MutableLiveData<String> deepSleepTotal = new MutableLiveData<>() ;
    private MutableLiveData<String> lightSleepTotal = new MutableLiveData<>() ;
    private MutableLiveData<String> remSleepTotal = new MutableLiveData<>() ;
    private MutableLiveData<String> wakeDuration = new MutableLiveData<>() ;
    private MutableLiveData<String> sleepSegments = new MutableLiveData<>() ;
    private MutableLiveData<String> sleepSummary = new MutableLiveData<>() ;

    //Health_HistoryComprehensiveMeasureData
    private static MutableLiveData<String> comprehesive_uricAcid = new MutableLiveData<>() ;
    private static MutableLiveData<String> comprehesive_triglycerideCholesterol = new MutableLiveData<>() ;
    private static MutableLiveData<String> comprehesive_cholesterol = new MutableLiveData<>() ;
    private static MutableLiveData<String> comprehesive_highLipoproteinCholesterol = new MutableLiveData<>() ;
    private static MutableLiveData<String> comprehesive_lowLipoproteinCholesterol = new MutableLiveData<>() ;


    public void setHealthBloodSugar(String value, Context context) {
        setParam(context,"health_sugar",value,healthBloodSugar);
    }
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
    public void setHealthHRVCVVRR(String value, Context context) {
        setParam(context,"health_hrv_cvvrr",value,healthHRVCVRR);
    }

    // Métodos para guardar los datos de sueño

    public void setSleepStartTime(String value, Context context) {
        setParam(context, "sleep_start_time", value, sleepStartTime);
    }

    public void setSleepEndTime(String value, Context context) {
        setParam(context, "sleep_end_time", value, sleepEndTime);
    }

    public void setSleepDuration(String value, Context context) {
        setParam(context, "sleep_duration", value, sleepDuration);
    }

    public void setDeepSleepTotal(String value, Context context) {
        setParam(context, "deep_sleep_total", value, deepSleepTotal);
    }

    public void setLightSleepTotal(String value, Context context) {
        setParam(context, "light_sleep_total", value, lightSleepTotal);
    }

    public void setRemSleepTotal(String value, Context context) {
        setParam(context, "rem_sleep_total", value, remSleepTotal);
    }

    public void setWakeDuration(String value, Context context) {
        setParam(context, "wake_duration", value, wakeDuration);
    }

    public void setSleepSegments(String value, Context context) {
        setParam(context, "sleep_segments", value, sleepSegments);
    }

    public void setSleepSummary(String value, Context context) {
        setParam(context, "sleep_summary", value, sleepSummary);
    }
    public void setComprehesive_triglycerideCholesterol(String value, Context context) {
        setParam(context, "comprehesive_trigly", value, comprehesive_triglycerideCholesterol);
    }
    public void setComprehesive_cholesterol(String value, Context context) {
        setParam(context, "comprehesive_cholesterol", value, comprehesive_cholesterol);
    }
    public void setComprehesive_highLipoproteinCholesterol(String value, Context context) {
        setParam(context, "comprehesive_hdl", value, comprehesive_highLipoproteinCholesterol);
    }
    public void setComprehesive_lowLipoproteinCholesterol(String value, Context context) {
        setParam(context, "comprehesive_ldl", value, comprehesive_lowLipoproteinCholesterol);
    }
    public void setComprehesive_uricAcid(String value, Context context) {
        setParam(context, "comprehesive_acid_uric", value, comprehesive_uricAcid);
    }





    public LiveData<String> getHealthHeart() {return healthHeart;}
    public LiveData<String> getHealthHRVCVRR() {return healthHRVCVRR;}
    public LiveData<String> getHealthHRV() {return healthHRV;}
    public LiveData<String> getHealthCVVRR() {return healthCVVRR;}
    public LiveData<String> getHealthStep() {return healthStep;}
    public LiveData<String> getHealthDBP() {return healthDBP;}
    public LiveData<String> getHealthSBP() {return healthSBP;}
    public LiveData<String> getHealthBody() {return healthBody;}
    public LiveData<String> getHealthBloodSugar() {return healthBloodSugar;}
    public LiveData<String> getHealthBloodPressure() {return healthBloodPressure;}
    public LiveData<String> getHealthOxygen() {return healthOxygen;}
    public LiveData<String> getSleepDuration() {return sleepDuration;}
    public LiveData<String> getHealthTemp() {return healthTemp;}
    public LiveData<String> getHealthStartTime() {return healthStartTime;}
    public LiveData<String> getHealthRespRate() {return healthRespRate;}
    //Comprehesive
    public LiveData<String> getCompTrigliceryd() {return comprehesive_triglycerideCholesterol;}
    public LiveData<String> geyCompUricAcid() {return comprehesive_uricAcid;}
    public LiveData<String> getCompCholesterol() {return comprehesive_cholesterol;}
    public LiveData<String> getCompHdl() {return comprehesive_highLipoproteinCholesterol;}
    public LiveData<String> getCompLdl() {return comprehesive_lowLipoproteinCholesterol;}

    private void setParam(Context context, String paramName, String param,  MutableLiveData paramL){
        SharedPreferences sharedPreferences = context.getSharedPreferences("health_prefs", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(paramName, param).apply();
        paramL.postValue(param);
    }

    public static void setHealthInitialParams(Context context){
        SharedPreferences prefs = context.getSharedPreferences("health_prefs", Context.MODE_PRIVATE);
        
        String heart = prefs.getString("health_heart",null);
        String hrv = prefs.getString("health_hrv",null);
        String cvrr = prefs.getString("health_cvvrr",null);
        String resp = prefs.getString("health_resp",null);
        String temp = prefs.getString("health_temp",null);
        String oxy = prefs.getString("health_ox",null);
        String sbp = prefs.getString("health_sbp",null);
        String dbp = prefs.getString("health_dbp",null);
        String start = prefs.getString("health_start",null);
        String sugar = prefs.getString("health_sugar",null);
        String sleep = prefs.getString("sleep_duration",null);

        String triyg = prefs.getString("comprehesive_trigly",null);
        String chol = prefs.getString("comprehesive_cholesterol",null);
        String hdl = prefs.getString("comprehesive_hdl",null);
        String ldl = prefs.getString("comprehesive_ldl",null);
        String acid = prefs.getString("comprehesive_acid_uric",null);

        //Presion Arterial-
        healthBloodPressure.postValue(dbp+"/"+sbp);
        //Corazon-
        healthHeart.postValue(heart);
        //HRV-
        healthHRV.postValue(hrv);
        //CVRR-
        healthCVVRR.postValue(cvrr);
        //Temperatura-
        healthTemp.postValue(temp);
        //Oxigeno-
        healthOxygen.postValue(oxy);
        //Presion SBP-
        healthSBP.postValue(sbp);
        //Presion DBP-
        healthDBP.postValue(dbp);
        //FR
        healthRespRate.postValue(resp);
        //StartTime
        healthStartTime.postValue(start);
        //Sleep Duration
        sleepDuration.postValue(sleep);
        //Blood Sugar
        healthBloodSugar.postValue(sugar);

        comprehesive_triglycerideCholesterol.postValue(triyg);
        comprehesive_cholesterol.postValue(chol);
        comprehesive_highLipoproteinCholesterol.postValue(hdl);
        comprehesive_lowLipoproteinCholesterol.postValue(ldl);
        comprehesive_uricAcid.postValue(acid);


    }
}
