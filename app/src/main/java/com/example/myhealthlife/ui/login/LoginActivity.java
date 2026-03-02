package com.example.myhealthlife.ui.login;
import static android.widget.Toast.makeText;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhealthlife.R;
import com.example.myhealthlife.ui.MainActivity;
import com.example.myhealthlife.ui.register.RegisterActivity;
import com.example.myhealthlife.ui.register.RegisterViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvMessage;
    private TextInputEditText etTelefono, etCurp, etFechaNacimiento, etCorreo, etContrasena, etName, etApellidoP,etApellidoM;
    private AutoCompleteTextView etGenero;
    private LinearLayout pLogin, pRegistro1, pRegistro2, cambiarPais;
    private TextView btnRegistro, terms, terms1;
    private CheckBox checkboxTerminos;
    private Button btnRegistro2, btnRegistro3;
    private VideoView videoView;
    private ImageView backRegistro1,backRegistro2;
    private TextInputLayout tilGenero,tilTelefono,tilCurp,tilCorreo,tilContrasena, tilBirthdate, tilNombre, tilApellidoP, tilApellidoM;
    private String curpCampo, correoCampo, contrasenaCampo, nombreCampo,apellidoPCampo, apellidoMCampo, telefonoCampo, fechaCampo,generoCampo;
    private String country;
    private LoginViewModel viewModel;
    private static final String PASSWORD_PATTERN =
            "^(?=.*[0-9])" +                    // Al menos un dígito
                    "(?=.*[a-z])" +            // Al menos una minúscula
                    "(?=.*[A-Z])" +            // Al menos una mayúscula
                    "(?=.*[@#$%^&+=!])" +      // Al menos un carácter especial
                    "(?=\\S+$)" +              // Sin espacios
                    ".{8,}$";                  // Mínimo 8 caracteres

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        configureButtons();
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

    }

    @Override
    public void onBackPressed() {
        LinearLayout pLogin = findViewById(R.id.pLogin);
        LinearLayout pRegistro1 = findViewById(R.id.pRegistro1);
        LinearLayout pRegistro2 = findViewById(R.id.pRegistro2);
        if (pLogin.getVisibility() == View.VISIBLE) {
            super.onBackPressed(); // Si no, cierra la Activity normalmente
        }
        else {
            super.onBackPressed(); // Si no, cierra la Activity normalmente
        }
    }

    // .............. PRINCIPALES ......................
    private void initViews(){

        //Login
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvMessage = findViewById(R.id.tvMessage);
        //Video
        videoView = findViewById(R.id.videoView);
        //Layouts
        pLogin = findViewById(R.id.pLogin);
        pRegistro1 = findViewById(R.id.pRegistro1);
        pRegistro2 = findViewById(R.id.pRegistro2);
        btnRegistro = findViewById(R.id.btnRegistro);
        btnRegistro.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        });
        btnRegistro2 = findViewById(R.id.btnReg2);
        btnRegistro3 = findViewById(R.id.btnReg3);
        backRegistro1 = findViewById(R.id.backRegistro1);
        backRegistro2 = findViewById(R.id.backRegistro2);
        terms = findViewById(R.id.terms);
        terms1 = findViewById(R.id.terms1);
        checkboxTerminos = findViewById(R.id.checkbox_terminos);
        cambiarPais = findViewById(R.id.cambiar_pais);
        //Registro PT1
        etName = findViewById(R.id.nameReg);
        etApellidoP = findViewById(R.id.apellidopReg);
        etApellidoM = findViewById(R.id.apellidomReg);
        etGenero = findViewById(R.id.generoReg);
        etFechaNacimiento = findViewById(R.id.fechaNacimientoReg);
        //Registro PT2
        etTelefono = findViewById(R.id.telefonoReg);
        etCorreo = findViewById(R.id.correoReg);
        etContrasena = findViewById(R.id.contrasenaReg);
        etCurp = findViewById(R.id.curpReg);

        // Hints?
        tilNombre = findViewById(R.id.til_name);
        tilApellidoP = findViewById(R.id.til_apellido_p);
        tilApellidoM = findViewById(R.id.til_apellido_m);
        tilBirthdate = findViewById(R.id.til_birthday);
        tilTelefono = findViewById(R.id.til_telefono);
        tilCorreo = findViewById(R.id.til_correo);
        tilCurp = findViewById(R.id.til_curp);
        tilContrasena = findViewById(R.id.til_contrasena);
        tilGenero = findViewById(R.id.til_genero);
        //Configurar fondo de video
        setVideo();
    }
    private void configureButtons() {
        btnLogin.setOnClickListener(v -> {

            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                makeText(LoginActivity.this, getString(R.string.login_completa_campos), Toast.LENGTH_SHORT).show();                return;
            }

            if (email.isEmpty()) {
                tilCorreo.setError("Correo requerido");
                return;
            }

            if (password.isEmpty()) {
                tilContrasena.setError("Contraseña requerida");
                return;
            }

            viewModel.login(email, password)
                    .observe(this, result -> {

                        switch (result.status) {
                            case LOADING:
                                makeText(this, getString(R.string.loading), Toast.LENGTH_SHORT).show();
                                break;

                            case SUCCESS:
                                goToMenu();
                                break;

                            case ERROR:
                                makeText(this, result.message, Toast.LENGTH_SHORT).show();
                                break;
                        }
                    });
        });

    }
    private void goToMenu() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
    // 🖼️ UI
    private void setVideo() {
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.background);
        videoView.setVideoURI(uri);
        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);

            // Detectar el tamaño del video
            mp.setOnVideoSizeChangedListener((mediaPlayer, width, height) -> {
                // Ajustar el layout del VideoView para ocupar todo el ancho
                int videoWidth = width;
                int videoHeight = height;

                // Obtener ancho real de la pantalla
                int screenWidth = getResources().getDisplayMetrics().widthPixels;

                // Calcular altura proporcional
                float aspectRatio = (float) videoHeight / videoWidth;
                int newHeight = (int) (screenWidth * aspectRatio);

                // Aplicar nuevo tamaño al VideoView
                ViewGroup.LayoutParams layoutParams = videoView.getLayoutParams();
                layoutParams.width = screenWidth;
                layoutParams.height = newHeight;
                videoView.setLayoutParams(layoutParams);
            });

            videoView.start();
        });
    }



}