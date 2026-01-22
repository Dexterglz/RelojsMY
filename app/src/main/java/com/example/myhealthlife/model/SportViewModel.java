package com.example.myhealthlife.model;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SportViewModel extends ViewModel {
    private SharedPreferences sharedPreferences;
    private static MutableLiveData<Integer> sportSteps = new MutableLiveData<>();
    private static MutableLiveData<Integer> sportGoalSteps = new MutableLiveData<>();
    private static MutableLiveData<Integer> sportDistance = new MutableLiveData<>();
    private static MutableLiveData<Integer> sportCalories = new MutableLiveData<>();

    public void setSportStep(int steps, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("sport_prefs", Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt("sport_steps", steps).apply();
        sportSteps.postValue(steps);
    }

    public void setSportGoalStep(int goalSteps, Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("sport_prefs", Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt("sport_goal_steps", goalSteps).apply();
        sportGoalSteps.postValue(goalSteps);
    }

    public void setSportCalories(int calories, Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("sport_prefs", Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt("sport_calories", calories).apply();
        sportCalories.postValue(calories);
    }
    public void setSportDistance(int distance, Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("sport_prefs", Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt("sport_distance", distance).apply();
        sportDistance.postValue(distance);
    }

    public LiveData<Integer> getSportGoalSteps() {return sportGoalSteps;}
    public LiveData<Integer> getSportSteps() { return sportSteps; }
    public LiveData<Integer> getSportDistance() { return sportDistance; }
    public LiveData<Integer> getSportCalories() { return sportCalories; }

    public static void setSportInitialParams(Context context){
        SharedPreferences prefs = context.getSharedPreferences("sport_prefs", Context.MODE_PRIVATE);
        int lastgoalSteps = prefs.getInt("sport_goal_steps", 0);
        int lastSteps = prefs.getInt("sport_steps", 0);
        int lastCalories = prefs.getInt("sport_calories", 0);
        int lastDistance = prefs.getInt("sport_distance", 0);

        //Configurar valores al cargar por primera vez la vista
        sportGoalSteps.postValue(lastgoalSteps);
        sportSteps.postValue(lastSteps);
        sportCalories.postValue(lastCalories);
        sportDistance.postValue(lastDistance);
    }
}
