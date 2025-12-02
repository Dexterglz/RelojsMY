package com.example.myhealthlife.model;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SportViewModel extends ViewModel {
    private SharedPreferences sharedPreferences;
    private MutableLiveData<Integer> sportSteps = new MutableLiveData<>();
    private MutableLiveData<Integer> sportGoalSteps = new MutableLiveData<>();
    private MutableLiveData<Integer> sportDistance = new MutableLiveData<>();
    private MutableLiveData<Integer> sportCalories = new MutableLiveData<>();

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
}
