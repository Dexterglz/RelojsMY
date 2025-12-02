package com.example.myhealthlife.model;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences prefs = newBase.getSharedPreferences("app", Context.MODE_PRIVATE);
        int languageIndex = prefs.getInt("language", 1); // 0=EN, 1=ES
        String[] languages = {"en", "es"};
        if (languageIndex < 0 || languageIndex >= languages.length) languageIndex = 1;
        String selectedLang = languages[languageIndex];

        Context context = LocaleHelper.setLocale(newBase, selectedLang);
        super.attachBaseContext(context);
    }
}

