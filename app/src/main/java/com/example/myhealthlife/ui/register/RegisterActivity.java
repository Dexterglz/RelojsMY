package com.example.myhealthlife.ui.register;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhealthlife.R;
import com.example.myhealthlife.domain.LocaleHelper;
import com.example.myhealthlife.domain.RegisterForm;
import com.example.myhealthlife.domain.RegisterUiState;
import com.example.myhealthlife.domain.common.register.DomainError;
import com.example.myhealthlife.ui.MainActivity;
import com.example.myhealthlife.ui.login.LoginActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private TextInputEditText etNombre;
    private TextInputEditText etApellidoPaterno;
    private TextInputEditText etApellidoMaterno;
    private TextInputEditText etTelefono;
    private TextInputEditText etCurp;
    private TextInputEditText etFechaNacimiento;
    private RadioGroup rgGenero;

    private Button btnRegister;
    private ProgressBar progressBar;

    private RegisterViewModel viewModel;
    private boolean isUserInteraction = false;

    private Spinner spCountry;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String lang = getSharedPreferences("settings", MODE_PRIVATE)
                .getString("languages", "es");

        setContentView(R.layout.activity_register);

        initViews();
        initViewModel();
        observeState();
        restoreCountrySelection();

    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etNombre = findViewById(R.id.etNombre);
        etApellidoPaterno = findViewById(R.id.etApellidoPaterno);
        etApellidoMaterno = findViewById(R.id.etApellidoMaterno);
        etTelefono = findViewById(R.id.etTelefono);
        etCurp = findViewById(R.id.etCurp);
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento);
        etFechaNacimiento.setOnClickListener(v -> showDatePicker());
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);
        rgGenero = findViewById(R.id.rgGenero);

        spCountry = findViewById(R.id.spCountry);

        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(
                        this,
                        R.array.countries,
                        android.R.layout.simple_spinner_item
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spCountry.setAdapter(adapter);

        spCountry.setOnTouchListener((v, event) -> {
            isUserInteraction = true;
            return false;
        });

        spCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            boolean firstTime = true;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (firstTime) {
                    firstTime = false;
                    return;
                }

                String newLang = position == 0 ? "es" : "en";

                getSharedPreferences("settings", MODE_PRIVATE)
                        .edit()
                        .putString("languages", newLang)
                        .apply();

                finish();
                startActivity(getIntent()); // 🔥 reinicio limpio
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnRegister.setOnClickListener(v -> submit());
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(
                this,
                new RegisterViewModelFactory()
        ).get(RegisterViewModel.class);
    }

    private void observeState() {
        viewModel.getUiState().observe(this, state -> {

            if (state instanceof RegisterUiState.Loading) {
                progressBar.setVisibility(View.VISIBLE);
                btnRegister.setEnabled(false);
            }

            else if (state instanceof RegisterUiState.Success) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(
                        this,
                        "Registro exitoso",
                        Toast.LENGTH_LONG
                ).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }

            else if (state instanceof RegisterUiState.Error) {
                progressBar.setVisibility(View.GONE);
                btnRegister.setEnabled(true);

                DomainError error =
                        ((RegisterUiState.Error) state).getError();

                showError(error);
            }
        });
    }

    private void submit() {

        RegisterForm form = new RegisterForm();
        form.email = safeText(etEmail);
        form.password = safeText(etPassword);
        form.nombre = safeText(etNombre);
        form.apellidoPaterno = safeText(etApellidoPaterno);
        form.apellidoMaterno = safeText(etApellidoMaterno);
        form.telefono = safeText(etTelefono);
        form.personalID = safeText(etCurp);
        form.fecha = safeText(etFechaNacimiento);

        int position = spCountry.getSelectedItemPosition();

        String[] countryCodes = getResources()
                .getStringArray(R.array.country_codes);

        form.countryCode = countryCodes[position];

        if (safeText(etEmail).isEmpty()) {
            Toast.makeText(this,
                    getString(R.string.ingresa_un_correo),
                    Toast.LENGTH_LONG).show();
            return;
        }
        if (safeText(etPassword).isEmpty()) {
            Toast.makeText(this,
                    getString(R.string.introduce_una_contrasena),
                    Toast.LENGTH_LONG).show();
            return;
        }
        if (safeText(etNombre).isEmpty()) {
            Toast.makeText(this,
                    getString(R.string.introduce_tu_nombre),
                    Toast.LENGTH_LONG).show();
            return;
        }
        if (safeText(etApellidoPaterno).isEmpty()) {
            Toast.makeText(this,
                    getString(R.string.introduce_tu_apellido_paterno),
                    Toast.LENGTH_LONG).show();
            return;
        }
        if (safeText(etApellidoMaterno).isEmpty() && position == 0) {
            Toast.makeText(this,
                    getString(R.string.introduce_tu_apellido_materno),
                    Toast.LENGTH_LONG).show();
            return;
        }
        int selectedId = rgGenero.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this,
                    getString(R.string.selecciona_un_genero),
                    Toast.LENGTH_LONG).show();
            return;
        }
        if (safeText(etTelefono).isEmpty()) {
            Toast.makeText(this,
                    getString(R.string.introduce_tu_telefono),
                    Toast.LENGTH_LONG).show();
            return;
        }
        if (safeText(etCurp).isEmpty()) {
            Toast.makeText(this,
                    getString(R.string.introduce_tu_curp),
                    Toast.LENGTH_LONG).show();
            return;
        }
        if (safeText(etFechaNacimiento).isEmpty()) {
            Toast.makeText(this,
                    getString(R.string.ingresa_tu_fecha_de_nacimiento),
                    Toast.LENGTH_LONG).show();
            return;
        }

        boolean genero;
        if (selectedId == R.id.rbFemenino) {
            genero = true;
        } else {
            genero = false;
        }

        form.genero = genero;

        viewModel.register(form);
    }

    private String safeText(TextInputEditText editText) {
        return editText.getText() != null
                ? editText.getText().toString().trim()
                : "";
    }

    private void showError(DomainError error) {
        String message;

        switch (error) {
            case INVALID_EMAIL:
                message = getString(R.string.ingresa_un_correo_valido);
                break;
            case INVALID_PASSWORD:
                message = getString(R.string.contrasena_al_menos);
                break;
            case INVALID_AGE:
                message = getString(R.string.edad_no_permitida);
                break;
            case INVALID_PERSONAL_ID:
                message = getString(R.string.el_formato_curp_invalido);
                break;
            case NETWORK_ERROR:
                message = getString(R.string.login_error_conexion);
                break;
            case USER_ALREADY_EXISTS:
                message = getString(R.string.usuario_ya_registrado);
                break;
            default:
                message = getString(R.string.login_error_registro);
        }

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {

                    String date = String.format(
                            Locale.getDefault(),
                            "%02d/%02d/%04d",
                            dayOfMonth,
                            month + 1,
                            year
                    );

                    etFechaNacimiento.setText(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        dialog.show();
    }

    private void saveLanguage(String languageCode) {
        getSharedPreferences("settings", MODE_PRIVATE)
                .edit()
                .putString("languages", languageCode)
                .apply();
    }

    @Override
    public boolean onSupportNavigateUp() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
        return true;
    }
    private void restoreCountrySelection() {
        String country =
                getSharedPreferences("settings", MODE_PRIVATE)
                        .getString("country", "MX");

        int position = country.equals("MX") ? 0 : 1;

        spCountry.setSelection(position, false); // 🔥 clave
    }
}