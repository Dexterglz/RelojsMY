package com.example.myhealthlife.fragments;

import static androidx.core.app.ActivityCompat.recreate;
import static com.yucheng.ycbtsdk.YCBTClient.settingBloodOxygenModeMonitor;
import static com.yucheng.ycbtsdk.YCBTClient.settingHeartMonitor;
import static com.yucheng.ycbtsdk.YCBTClient.settingTemperatureMonitor;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.myhealthlife.R;
import com.example.myhealthlife.model.LocaleHelper;
import com.example.myhealthlife.ui.MainActivity;
import com.yucheng.ycbtsdk.response.BleDataResponse;

import java.util.HashMap;
import java.util.Locale;

public class LanguageDialogFragment extends DialogFragment {

    public LanguageDialogFragment() {}
    Button btnChangeMonitoring;
    SharedPreferences prefs;
    Spinner spinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_language_dialog, container, false);
        prefs = requireActivity().getSharedPreferences("app", Context.MODE_PRIVATE);
        btnChangeMonitoring = view.findViewById(R.id.btnChangeLanguage);

        int savedInterval = prefs.getInt("language", 0);
        // 2. Buscar la posición del valor en el array del Spinner
        String[] intervals = getResources().getStringArray(R.array.spinner_optionsL); // Tu array de opciones
        int position = 0; // Posición por defecto (ej: 10)
        for (int i = 0; i < intervals.length; i++) {
            if (i == savedInterval) {
                position = i;
                break;
            }
        }

        spinner = view.findViewById(R.id.mySpinnerL);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.spinner_optionsL, // Define este array en res/values/strings.xml
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(position);

        btnChangeMonitoring.setOnClickListener(v ->
        {
            saveOption();
            dismiss();
        });

        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog_background);
        }
    }
    public void saveOption() {
        int[] values = {0, 1};
        String[] languages = {"en", "es"};
        int selectedValue = values[spinner.getSelectedItemPosition()];
        String languageSelected = languages[spinner.getSelectedItemPosition()];

        SharedPreferences prefs = requireActivity().getSharedPreferences("app", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("language", selectedValue);
        editor.apply();

        ((MainActivity) requireActivity()).refreshTabs();

        // Reemplaza recreate() por esto
        Intent intent = requireActivity().getIntent();
        requireActivity().finish();
        startActivity(intent);
    }



}