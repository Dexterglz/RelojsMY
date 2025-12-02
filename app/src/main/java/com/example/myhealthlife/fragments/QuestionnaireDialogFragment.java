package com.example.myhealthlife.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.myhealthlife.R;
import com.example.myhealthlife.ui.MainActivity;

public class QuestionnaireDialogFragment extends DialogFragment {

    private EditText inputHeight, inputWeight, inputAge;
    private RadioGroup radioGroupGender;

    public QuestionnaireDialogFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_questionnaire, container, false);

        inputHeight = view.findViewById(R.id.inputHeight);
        inputWeight = view.findViewById(R.id.inputWeight);
        Button continueButton = view.findViewById(R.id.btnContinue);
        ImageView closeDialog = view.findViewById(R.id.closeDialog);
        closeDialog.setOnClickListener(v-> dismiss());
        continueButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                String heightStr = inputHeight.getText().toString().trim();
                String weightStr = inputWeight.getText().toString().trim();

                // Validar que los campos no estén vacíos
                if (heightStr.isEmpty()) {
                    inputHeight.setError(getString(R.string.quest_por_favor_ingresa_tu_altura));
                    return;
                }

                if (weightStr.isEmpty()) {
                    inputWeight.setError(getString(R.string.quest_por_favor_ingresa_tu_peso));
                    return;
                }

                // Convertir a números y validar rangos razonables
                try {
                    float height = Float.parseFloat(heightStr);
                    float weight = Float.parseFloat(weightStr);

                    // Validar rangos (ajusta según necesites)
                    if (height < 100 || height > 280) { // altura en cm (100-250cm)
                        inputHeight.setError(getString(R.string.quest_por_favor_ingresa_edad));
                        return;
                    }

                    if (weight < 30 || weight > 300) { // peso en kg (30-300kg)
                        inputWeight.setError(getString(R.string.quest_por_favor_ingresa_peso));
                        return;
                    }

                    // Guardar en SharedPreferences
                    SharedPreferences prefs = getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putFloat("user_height", height);
                    editor.putFloat("user_weight", weight);
                    editor.putBoolean("questionnaire_completed", true);
                    editor.apply();

                    // Continuar a la siguiente actividad
                    startActivity(new Intent(getActivity(), MainActivity.class));
                    getActivity().finish();

                } catch (NumberFormatException e) {
                    // Manejar caso donde la conversión falla
                    inputHeight.setError(getString(R.string.quest_por_favor_ingresa_numero));
                    inputWeight.setError(getString(R.string.quest_por_favor_ingresa_numero));
                    return;
                }
            }
            dismiss();
        });;

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog_background);
            dialog.getWindow().setElevation(10f);
        }
    }
}
