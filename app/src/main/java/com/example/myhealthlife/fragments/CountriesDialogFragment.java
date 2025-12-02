package com.example.myhealthlife.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.fragment.app.DialogFragment;

import com.example.myhealthlife.R;
import com.example.myhealthlife.model.LocaleHelper;

public class CountriesDialogFragment extends DialogFragment {

    public CountriesDialogFragment() {}
    Button btnChangeCountry;
    SharedPreferences prefs;
    Spinner spinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_countries_dialog, container, false);
        prefs = requireActivity().getSharedPreferences("app", Context.MODE_PRIVATE);
        btnChangeCountry = view.findViewById(R.id.btnChangeCountry);

        int savedInterval = prefs.getInt("country", 0);
        // 2. Buscar la posición del valor en el array del Spinner
        String[] intervals = getResources().getStringArray(R.array.spinner_optionsCountries); // Tu array de opciones
        int position = 0; // Posición por defecto (ej: 10)
        for (int i = 0; i < intervals.length; i++) {
            if (i == savedInterval) {
                position = i;
                break;
            }
        }

        spinner = view.findViewById(R.id.mySpinnerCountry);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.spinner_optionsCountries, // Define este array en res/values/strings.xml
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(position);

        btnChangeCountry.setOnClickListener(v ->
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

        // Cambiar idioma en toda la app
        LocaleHelper.setLocale(requireActivity(), languageSelected);

        String[] countries = {"MEXICO", "USA"};
        int selectedCountryValue = values[spinner.getSelectedItemPosition()];
        String countrySelected = countries[spinner.getSelectedItemPosition()];

        editor.putInt("country", selectedCountryValue);
        editor.apply();

        // Reinicia la Activity principal para que tome el idioma
        Intent intent = requireActivity().getIntent();
        requireActivity().finish();
        requireActivity().startActivity(intent);
    }



}