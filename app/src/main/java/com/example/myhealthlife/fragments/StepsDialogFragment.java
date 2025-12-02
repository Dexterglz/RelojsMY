package com.example.myhealthlife.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhealthlife.R;
import com.example.myhealthlife.model.SportViewModel;
import com.example.myhealthlife.ui.MainActivity;

public class StepsDialogFragment extends DialogFragment {

    private EditText inputGoalSteps;
    private RadioGroup radioGroupGender;
    private SportViewModel viewModel;

    public StepsDialogFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_steps, container, false);

        inputGoalSteps = view.findViewById(R.id.inputSteps);
        Button continueButton = view.findViewById(R.id.btnContinue);
        viewModel = new ViewModelProvider(requireActivity()).get(SportViewModel.class);

        SharedPreferences prefs = requireActivity().getSharedPreferences("sport_prefs", MODE_PRIVATE);
        int goalSteps = prefs.getInt("sport_goal_steps", 0);
        inputGoalSteps.setText(""+goalSteps);


        continueButton.setOnClickListener(v -> {

            if (getActivity() != null) {
                String stepsStr = inputGoalSteps.getText().toString().trim();

                // Validar que los campos no estén vacíos
                if (stepsStr.isEmpty()) {
                    inputGoalSteps.setError(getString(R.string.steps_por_favor_ingresa_un_numero));
                    return;
                }

                // Convertir a números y validar rangos razonables
                try {
                    Integer steps = Integer.parseInt(stepsStr);

                    //Guarda en shared y manda al modelo
                    viewModel.setSportGoalStep(steps,getContext());

                } catch (NumberFormatException e) {
                    // Manejar caso donde la conversión falla
                    inputGoalSteps.setError(getString(R.string.quest_por_favor_ingresa_numero));
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
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog_background);
        }
    }

}
