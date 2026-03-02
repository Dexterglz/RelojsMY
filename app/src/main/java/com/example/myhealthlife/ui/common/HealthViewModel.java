package com.example.myhealthlife.ui.common;

import static com.example.myhealthlife.data.local.ble.BleCallback.getAllHealthData;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.myhealthlife.data.local.repository.HealthRepository;
import com.example.myhealthlife.ui.HomeNav;
import com.example.myhealthlife.R;
import com.example.myhealthlife.data.local.entity.HistoryBloodEntity;
import com.example.myhealthlife.data.local.entity.HistoryCompEntity;
import com.example.myhealthlife.data.local.entity.HistoryHealthEntity;
import com.example.myhealthlife.data.local.entity.HistorySleepEntity;
import com.example.myhealthlife.data.local.entity.HistorySportEntity;
import com.example.myhealthlife.domain.util.TimeUtils;
import com.example.myhealthlife.data.local.repository.HistoryDataRepository;
import com.example.myhealthlife.ui.home.HomeCardState;

import java.util.concurrent.TimeUnit;

public class HealthViewModel extends AndroidViewModel {
    private HistoryDataRepository repository;
    private LiveData<HistoryHealthEntity> lastHealth;
    private LiveData<HistorySleepEntity> lastSleep;
    private LiveData<HistorySportEntity> lastSport;
    private LiveData<HistoryBloodEntity> lastBlood;
    private LiveData<HistoryCompEntity> lastComp;
    private final HealthRepository retroRepository;

    public HealthViewModel(@NonNull Application app) {
        super(app);
        repository = new HistoryDataRepository(app);
        lastHealth = repository.getLastHealth();
        lastSleep = repository.getLastSleep();
        lastSport = repository.getLastSport();
        lastBlood = repository.getLastBlood();
        lastComp = repository.getLastComp();
        retroRepository = new HealthRepository();
    }

    private final MutableLiveData<Event<HomeNav>> navEvent =
            new MutableLiveData<>();
    public LiveData<Event<HomeNav>> navigation() {
        return navEvent;
    }
    //Definir Estructura Inicial
    public HomeCardState ecgCardState() {
        return new HomeCardState(
                R.drawable.heartbeat,
                R.string.ecg,
                "",
                "none"
        );
    }
    public HomeCardState sleepCardState() {
        return new HomeCardState(
                R.drawable.heartbeat,
                R.string.sleep,
                "-H -m",
                ""
        );
    }
    public HomeCardState heartCardState() {
        return new HomeCardState(
                R.drawable.heartbeat,
                R.string.heart,
                "--",
                "bpm"
        );
    }
    public HomeCardState sugarCardState() {
        return new HomeCardState(
                R.drawable.heartbeat,
                R.string.blood_sugar,
                "--",
                "mmol/L"
        );
    }
    public HomeCardState bloodCardState() {
        return new HomeCardState(
                R.drawable.heartbeat,
                R.string.blood_pressure,
                "--/--",
                "sb/db"
        );
    }
    public HomeCardState tempCardState() {
        return new HomeCardState(
                R.drawable.heartbeat,
                R.string.temperatura_corporal,
                "--",
                "°C"
        );
    }
    public HomeCardState acidCardState() {
        return new HomeCardState(
                R.drawable.heartbeat,
                R.string.uric_acid,
                "--",
                "µmol/L"
        );
    }
    public HomeCardState trigCardState() {
        return new HomeCardState(
                R.drawable.heartbeat,
                R.string.grasa,
                "--",
                "mmol/L"
        );
    }
    //Obtener Datos Concretos
    public LiveData<String> sleepValue() {
        return Transformations.map(lastSleep, i -> {
            if (i == null) return "--";

            if (!TimeUtils.isToday(i.timestamp)) {
                return "--";
            }

            long durationMillis = i.endTime - i.timestamp;
            long totalMinutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis);
            long hours = totalMinutes / 60;
            long minutes = totalMinutes % 60;
            return hours + "hr " + minutes + "m";
        });
    }
    public LiveData<String> heartValue() {
        return Transformations.map(lastHealth, i -> {
            if (i == null) return "--";
            if (i.heartValue > 0) return i.heartValue+"";
            return "--";
        });
    }

    public LiveData<String> sugarValue() {
        return Transformations.map(lastHealth, i -> {
            if (i == null) return "--";
            if (i.bloodSugarValue > 0) return i.bloodSugarValue+"";
            return "--";
        });
    }
    public LiveData<String> tempValue() {
        return Transformations.map(lastHealth, i -> {
            if (i == null) return "--";
            if (i.tempIntValue > 0) {
                float temperature =
                        Float.parseFloat(
                                i.tempIntValue
                                        + "."
                                        + i.tempFloatValue
                        );
                return temperature+"";
            }
            return "--";
        });
    }
    public LiveData<String> bloodValue() {
        return Transformations.map(lastBlood, i -> {
            if (i == null) return "--";
            int dbp = i.dbpValue;
            int sbp = i.sbpValue;
            if (dbp > 0 && sbp > 0) return sbp + "/" + dbp;
            return "--";
        });
    }
    public LiveData<String> acidValue() {
        return Transformations.map(lastComp, i -> {
            if (i == null) return "--";
            if (i.uricAcid > 0) return i.uricAcid+"";
            return "--";
        });
    }
    public LiveData<String> trigValue() {
        return Transformations.map(lastComp, i -> {
            if (i == null) return "--";
            float trig =
                    Float.parseFloat(
                            i.triCholInt
                                    + "."
                                    + i.triCholFloat
                    );
            if (trig > 0) return String.format("%.2f", trig);
            return "--";
        });
    }
    public LiveData<String> stepsValue() {
        return Transformations.map(lastSport, i -> {
            if (i == null) return "--";
            if (i.sportStep > 0 ) return i.sportStep+"";
            return "0";
        });
    }
    public LiveData<Float> stepsPct() {
        return Transformations.map(lastSport, i -> {
            if (i == null) return 0f;
            if (i.sportStep > 0)  return (i.sportStep * 100f / 1000);
            return 0f;
        });
    }
    public LiveData<String> caloriesValue() {
        return Transformations.map(lastSport, i -> {
            if (i == null) return "--";
            if (i.sportCalorie > 0) return i.sportCalorie+"";
            return "0";
        });
    }
    public LiveData<String> distanceValue() {
        return Transformations.map(lastSport, i -> {
            if (i == null) return "--";
            if (i.sportCalorie > 0) return (float) i.sportDistance/1000 + "";
            return "0";
        });
    }
    public LiveData<Long> timeValue() {
        return Transformations.map(lastHealth, i -> {
            if (i == null) return 0L;
            return i.timestamp;
        });
    }
    public LiveData<String> lastUpdate() {
        return Transformations.map(lastHealth, i -> {
            if (i == null) return "";
            return TimeUtils.timeToString(i.timestamp, "dd/MM/yyyy hh:mm a");
        });
    }
    /*public String lastUpdate() {
        *//*SimpleDateFormat sdf = new SimpleDateFormat(
                "dd/MM/yyyy hh:mm a",
                Locale.getDefault()
        );
        return sdf.format(new Date());*//*

        return Transformations.map(lastHealth, i -> {
            if (i == null) return "";
            SimpleDateFormat sdf = new SimpleDateFormat(
                    "dd/MM/yyyy hh:mm a",
                    Locale.getDefault()
            );
            return sdf;
        });
    }*/
    public void syncAllFromBle() {
        getAllHealthData(getApplication());
    }
    //Asignar navegacion
    public void onSleepClicked() {
        navEvent.setValue(new Event<>(HomeNav.SLEEP));
    }
    public void onHeartClicked() {
        navEvent.setValue(new Event<>(HomeNav.HEART));
    }
    public void onBloodClicked() {
        navEvent.setValue(new Event<>(HomeNav.BLOOD));
    }
    public void onSugarlicked() {
        navEvent.setValue(new Event<>(HomeNav.SUGAR));
    }
    public void onTempClicked() {
        navEvent.setValue(new Event<>(HomeNav.TEMP));
    }
    public void onFatClicked() {
        navEvent.setValue(new Event<>(HomeNav.FAT));
    }
    public void onAcidClicked() {
        navEvent.setValue(new Event<>(HomeNav.URIC));
    }
    //Obtener Ultimos Datos
    public LiveData<HistoryHealthEntity> getLastHealth() {
        return lastHealth;
    }
    public LiveData<HistorySportEntity> getLastSport() {
        return lastSport;
    }
    public LiveData<HistoryBloodEntity> getLastBlood() {
        return lastBlood;
    }
    public LiveData<HistoryCompEntity> getLastComp() {
        return lastComp;
    }
    //Valores por Intervalo


}

