package com.example.myhealthlife.ui;

import static android.widget.Toast.makeText;
import static androidx.core.content.ContentProviderCompat.requireContext;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myhealthlife.R;
import com.example.myhealthlife.fragments.HomeFragment;
import com.example.myhealthlife.io.response.ApiClient;
import com.example.myhealthlife.io.response.ApiService;
import com.example.myhealthlife.io.response.PatientContainer;
import com.example.myhealthlife.io.response.PatientResponse;
import com.example.myhealthlife.model.LocaleHelper;
import com.example.myhealthlife.model.LoginRequest;
import com.example.myhealthlife.model.LoginResponse;
import com.example.myhealthlife.model.RegisterRequest;
import com.example.myhealthlife.model.RegisterResponse;
import com.example.myhealthlife.model.Usuario;
import com.example.myhealthlife.model.ValidatorUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.example.myhealthlife.fragments.QuestionnaireDialogFragment;
import com.google.gson.Gson;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;





import java.util.Calendar;
import java.util.TimeZone;
import java.util.function.Supplier;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

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

        initViews();                                            //Inicializar vistas
        mostrarDialogoTerminos();                               //Solicitar aceptar términos si no se han aceptado
        configureButtons();                                     //Configurar botones
        contenidoPag1();                                        //Registro PT1
        contenidoPag2();                                        //Registro PT22

    }

    @Override
    public void onBackPressed() {
        LinearLayout pLogin = findViewById(R.id.pLogin);
        LinearLayout pRegistro1 = findViewById(R.id.pRegistro1);
        LinearLayout pRegistro2 = findViewById(R.id.pRegistro2);
        if (pLogin.getVisibility() == View.VISIBLE) {
            super.onBackPressed(); // Si no, cierra la Activity normalmente
        }
        // Si pRegistro1 está visible, vuelve a pLogin en lugar de cerrar la Activity
        if (pRegistro1.getVisibility() == View.VISIBLE) {
            cambiarPantalla(pRegistro1, pLogin); // Usa tu función reutilizable
        }
        if (pRegistro2.getVisibility() == View.VISIBLE) {
            cambiarPantalla(pRegistro2, pRegistro1); // Usa tu función reutilizable
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
        // INICIO DE SESION
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            if (email.isEmpty() || password.isEmpty()) {
                makeText(LoginActivity.this, getString(R.string.login_completa_campos), Toast.LENGTH_SHORT).show();                return;
            }
            loginUser(email, password);
        });
            terms.setOnClickListener(v ->{
            showScrollablePopup();
        });
            terms1.setOnClickListener(v ->{
            showScrollablePopup();
        });
            cambiarPais.setOnClickListener(v -> {
            mostrarDialogoPaises();
            });

        //REGISTRO PT1
        btnRegistro.setOnClickListener(v -> {
            cambiarPantalla(pLogin, pRegistro1);
        });
        backRegistro1.setOnClickListener(v -> {
            cambiarPantalla(pRegistro1, pLogin);
        });
        //Genero
        String[] generos = new String[]{"Masculino", "Femenino"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdown_item, generos);
        etGenero.setAdapter(adapter);
        etGenero.setOnItemClickListener((parent, view, position, id) -> {
            showDatePickerDialog();
        });
        etFechaNacimiento.setOnClickListener(v -> showDatePickerDialog());
        //Siguiente
        btnRegistro2.setOnClickListener(v -> {
            if (validateAllFields1()) {
                cambiarPantalla(pRegistro1, pRegistro2); // Oculta pRegistro1 y muestra pRegistro2
            }
        });
    }
    // .............. 🌐 APIS  ........................
    private void loginUser(String email, String password) {
        ApiService apiService = ApiClient.newClient().create(ApiService.class);
        Call<LoginResponse> call = apiService.loginUser(new LoginRequest(email, password));
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken();

                    SharedPreferences prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();

                    // Guardar el token
                    editor.putString("user_token", token);

                    // También puedes guardar otros datos relacionados
                    editor.putBoolean("is_logged_in", true);

                    // Aplicar los cambios (commit() es sincrónico, apply() asincrónico)
                    editor.apply(); // o editor.commit() si necesitas confirmación inmediata

                    Log.d("Token", "Token guardado: " + token);

                    getPatientData(token);

                    // Aquí puedes guardar el token en SharedPreferences para futuras peticiones
                    goToMenu();
                    makeText(LoginActivity.this, getString(R.string.login_login_exitoso), Toast.LENGTH_SHORT).show();

                } else {
                    tvMessage.setText("Error: " + response.message());
                    makeText(LoginActivity.this, getString(R.string.login_credenciales_inc), Toast.LENGTH_SHORT).show();
                    //goToMenu();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                tvMessage.setText("Error: " + t.getMessage());
                makeText(LoginActivity.this, getString(R.string.login_error_conexion), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getPatientData(String token) {
        ApiService apiService = ApiClient.newClient().create(ApiService.class);
        Call<PatientContainer> call = apiService.getPatientData("Bearer "+token);
        Log.d("PACIENTE", "Bearer "+token);

        call.enqueue(new Callback<PatientContainer>() {
            @Override
            public void onResponse(Call<PatientContainer> call, Response<PatientContainer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PatientResponse patient = response.body().getUser();

                    String idUsuario = patient.getId_usuario();
                    String fechaN = patient.getFecha_nacimiento();

                    // Aquí guardas en SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("user_id", idUsuario);
                    editor.putString("user_date", fechaN);
                    editor.apply();

                    Log.d("PACIENTE", "ID: " + idUsuario);
                    Log.d("PACIENTE", "Fecha Nacimiento: " + fechaN);

                } else {
                    Log.e("API_ERROR", "Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<PatientContainer> call, Throwable t) {
                Log.e("API_ERROR", "Fallo en la conexión: " + t.getMessage());
            }
        });


    }
    private void registerUser(String email, String password, String nombre, String primerApellido,
                              String segundoApellido, String telefono, String curp,
                              String fechaNacimiento, String genero) {

        // Crear el objeto de solicitud con los datos del usuario
        Usuario usuario = new Usuario(
                3, // Paciente Rol
                password,
                email,
                fechaNacimiento,
                genero,
                nombre,
                primerApellido,
                segundoApellido,
                telefono,
                curp
        );

        ApiService apiService = ApiClient.newClient().create(ApiService.class);

        Call<RegisterResponse> call = apiService.registerUser(new RegisterRequest(usuario));

        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Registro exitoso
                    tvMessage.setText("Registro exitoso");
                    makeText(LoginActivity.this, getString(R.string.login_usuario_registrado), Toast.LENGTH_SHORT).show();
                    // Puedes redirigir al login o hacer otras acciones
                } else {
                    tvMessage.setText("Error: " + response.message());
                    makeText(LoginActivity.this, getString(R.string.login_error_registro)+" " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                tvMessage.setText("Error: " + t.getMessage());
                makeText(LoginActivity.this, getString(R.string.login_error_registro), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // .............. ⚠️ VALIDACIONES ...............
    private boolean termsAccepted() {
        SharedPreferences prefs = setPrefs("MyApp");
        return prefs.getBoolean("termsAccepted", false);
    }
    private boolean isMexico() {
        country = setPrefs("app").getString("country", "");
        boolean isMx = country.equals("México");
        Log.d("pais",country);
        Log.d("pais", String.valueOf(isMx));
        return isMx;
    }
    private boolean validateName() {
        nombreCampo = ((EditText)tilNombre.getEditText()).getText().toString().trim();

        if (nombreCampo.isEmpty()) {
            tilNombre.setError(getString(R.string.el_nombre_es_obligatorio));
            return false;
        } else if (!nombreCampo.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+")) {
            tilNombre.setError(getString(R.string.solo_se_permiten_letras));
            return false;
        } else {
            tilNombre.setError(null);
            return true;
        }
    }
    private boolean validateApellidoP() {
        apellidoPCampo = ((EditText)tilApellidoP.getEditText()).getText().toString().trim();

        if (apellidoPCampo.isEmpty()) {
            tilApellidoP.setError(getString(R.string.el_apellido_p_es_obligatorio));
            return false;
        } else if (!apellidoPCampo.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+")) {
            tilApellidoP.setError(getString(R.string.solo_se_permiten_letras));
            return false;
        } else {
            tilApellidoP.setError(null);
            return true;
        }
    }
    private boolean validateApellidoM() {
        apellidoMCampo = ((EditText)tilApellidoM.getEditText()).getText().toString().trim();

        if (apellidoMCampo.isEmpty()) {
            tilApellidoM.setError(getString(R.string.el_apellido_m_es_obligatorio));
            return false;
            //AÑADIR VALIDACION DE NUMEROS
        } else if (!apellidoMCampo.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+")) {
            tilApellidoM.setError(getString(R.string.solo_se_permiten_letras));
            return false;
        } else if(!isMexico()) {
            tilApellidoM.setError(null);
            return true;
        }
        else {
            return true;
        }
    }
    private boolean validateGenero() {
        AutoCompleteTextView actvGenero = (AutoCompleteTextView) tilGenero.getEditText();
        String genero = actvGenero.getText().toString().trim();
        Log.d("REGISTRO","genero : "+genero);
        if(genero.equals("Masculino"))
        {
            generoCampo = "1";
        }else
        {
            generoCampo = "2";
        }

        if (genero.isEmpty()) {
            tilGenero.setError(getString(R.string.selecciona_un_genero));
            return false;
        } else {
            tilGenero.setError(null);
            return true;
        }
    }
    private boolean validateBd() {
        EditText etBirthdate = tilBirthdate.getEditText();
        String birthdateStr = etBirthdate.getText().toString().trim();

        if (birthdateStr.isEmpty()) {
            tilBirthdate.setError(getString(R.string.la_fecha_es_obligatoria));
            return false;
        }

        try {
            // Parsear la fecha (asumiendo formato dd/MM/yyyy)
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            sdf.setLenient(false); // Validación estricta de fecha
            Date birthdate = sdf.parse(birthdateStr);
            fechaCampo = String.valueOf(birthdate); // "1969-12-11T00:00:00.000Z"

            // Calcular edad
            Calendar dob = Calendar.getInstance();
            dob.setTime(birthdate);
            Calendar today = Calendar.getInstance();

            int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

            // Ajustar si aún no ha pasado el cumpleaños este año
            if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }

            if (age < 18) {
                tilBirthdate.setError(getString(R.string.debe_tener_al_menos));
                return false;
            }

            // Validar que la fecha no sea futura
            if (birthdate.after(new Date())) {
                tilBirthdate.setError(getString(R.string.la_fecha_no_puede));
                return false;
            }

            tilBirthdate.setError(null);
            return true;

        } catch (ParseException e) {
            tilBirthdate.setError(getString(R.string.formato_invalido_fecha));
            return false;
        }
    }
    private boolean validateTelefono() {

        telefonoCampo = etTelefono.getText().toString().trim();

        if (telefonoCampo.isEmpty()) {
            tilTelefono.setError(getString(R.string.el_telefono_obligatorio));
            return false;
        } else if (telefonoCampo.length() != 10) {
            tilTelefono.setError(getString(R.string.el_telefono_debe_tener));
            return false;
        } else if (!telefonoCampo.matches("\\d{10}")) {
            tilTelefono.setError(getString(R.string.solo_se_permiten_numeros));
            return false;
        } else {
            tilTelefono.setError(null);
            return true;
        }
    }
    private boolean validateCorreo() {
        correoCampo = etCorreo.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (correoCampo.isEmpty()) {
            tilCorreo.setError(getString(R.string.el_correo_es_obligatorio));
            return false;
        } else if (!correoCampo.matches(emailPattern)) {
            tilCorreo.setError(getString(R.string.ingresa_un_correo_valido));
            return false;
        } else {
            tilCorreo.setError(null);
            return true;
        }
    }
    private boolean validateCurp() {
        curpCampo = etCurp.getText().toString().trim().toUpperCase();

        // Obtener datos personales
        String nombre = etName.getText().toString().trim().toUpperCase();
        String apellidoP = etApellidoP.getText().toString().trim().toUpperCase();
        String apellidoM = etApellidoM.getText().toString().trim().toUpperCase();

        // Validar fecha de nacimiento
        String fechaTexto = etFechaNacimiento.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date fechaN = null;

        try {
            fechaN = sdf.parse(fechaTexto);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Validaciones básicas de la CURP
        if (curpCampo.isEmpty()) {
            tilCurp.setError(getString(R.string.la_curp_esobligatoria));
            return false;
        } else if (curpCampo.length() != 18) {
            tilCurp.setError(getString(R.string.la_curp_debe_tener));
            return false;
        } else if (!curpCampo.matches("^[A-Z]{4}[0-9]{6}[HM][A-Z]{5}[A-Z0-9]{2}$")) {
            tilCurp.setError(getString(R.string.el_formato_curp_invalido));
            return false;
        }

        // Validar que coincida con nombres y apellidos
        if (!validarNombresEnCurp(curpCampo, nombre, apellidoP, apellidoM)) {
            tilCurp.setError(getString(R.string.el_curp_no_coincide_nombres));
            return false;
        }

        // Validar que coincida con la fecha de nacimiento
        if (fechaN != null && !validarFechaEnCurp(curpCampo, fechaN)) {
            tilCurp.setError(getString(R.string.el_curp_no_coincide_fecha));
            return false;
        }

        tilCurp.setError(null);
        return true;
    }
    private boolean validarNombresEnCurp(String curp, String nombre, String apellidoP, String apellidoM) {
        try {
            // Primer letra del apellido paterno (posición 0 de la CURP)
            char primerLetraApellidoP = apellidoP.charAt(0);

            // Primera vocal interna del apellido paterno (posición 1 de la CURP)
            char primeraVocalApellidoP = obtenerPrimeraVocalInterna(apellidoP);

            // Primer letra del apellido materno (posición 2 de la CURP)
            char primerLetraApellidoM = apellidoM.isEmpty() ? 'X' : apellidoM.charAt(0);

            // Primer letra del nombre (posición 3 de la CURP)
            char primerLetraNombre = nombre.isEmpty() ? 'X' : nombre.charAt(0);

            // Verificar que coincidan con la CURP
            return curp.charAt(0) == primerLetraApellidoP &&
                    curp.charAt(1) == primeraVocalApellidoP &&
                    curp.charAt(2) == primerLetraApellidoM &&
                    curp.charAt(3) == primerLetraNombre;
        } catch (Exception e) {
            return false;
        }
    }
    private char obtenerPrimeraVocalInterna(String texto) {
        // Eliminar la primera letra y buscar la primera vocal
        if (texto.length() > 1) {
            String resto = texto.substring(1).toUpperCase();
            for (char c : resto.toCharArray()) {
                if (c == 'A' || c == 'E' || c == 'I' || c == 'O' || c == 'U') {
                    return c;
                }
            }
        }
        return 'X'; // Valor por defecto si no encuentra vocal
    }
    private boolean validateContrasena() {
        contrasenaCampo = etContrasena.getText().toString().trim();

        if (contrasenaCampo.isEmpty()) {
            tilContrasena.setError(getString(R.string.la_contrasena_obligatoria));
            return false;
        }

        if (contrasenaCampo.length() < 8) {
            tilContrasena.setError(getString(R.string.contrasena_al_menos));
            return false;
        }

        if (!contrasenaCampo.matches(PASSWORD_PATTERN)) {
            tilContrasena.setError(
                    "Debe incluir:\n" +
                            "- Al menos una mayúscula\n" +
                            "- Al menos una minúscula\n" +
                            "- Al menos un número\n" +
                            "- Al menos un carácter especial (@#$%^&+=!)\n" +
                            "- Sin espacios"
            );
            return false;
        }

        tilContrasena.setError(null);
        return true;
    }
    private boolean validarFechaEnCurp(String curp, Date fechaNacimiento) {
        try {
            // Extraer fecha de la CURP (posiciones 4-9: AAMMDD)
            String fechaCurp = curp.substring(4, 10);
            int añoCurp = Integer.parseInt(fechaCurp.substring(0, 2));
            int mesCurp = Integer.parseInt(fechaCurp.substring(2, 4));
            int diaCurp = Integer.parseInt(fechaCurp.substring(4, 6));

            // Obtener fecha de nacimiento
            Calendar cal = Calendar.getInstance();
            cal.setTime(fechaNacimiento);
            int añoNac = cal.get(Calendar.YEAR) % 100; // Solo últimos 2 dígitos
            int mesNac = cal.get(Calendar.MONTH) + 1; // Meses en Calendar son 0-based
            int diaNac = cal.get(Calendar.DAY_OF_MONTH);

            // Comparar
            return añoCurp == añoNac && mesCurp == mesNac && diaCurp == diaNac;
        } catch (Exception e) {
            return false;
        }
    }
    private boolean validatePasaporte() {
        String pasaporte = etCurp.getText().toString().trim().toUpperCase();

        // Validaciones básicas del pasaporte
        if (pasaporte.isEmpty()) {
            tilCurp.setError(getString(R.string.el_pasaporte_es_obligatorio));
            return false;
        } else if (pasaporte.length() < 6 || pasaporte.length() > 12) {
            tilCurp.setError(getString(R.string.el_pasaporte_debe_tener_entre_6_y_12_caracteres));
            return false;
        } else if (!pasaporte.matches("^[A-Z0-9]{6,12}$")) {
            tilCurp.setError(getString(R.string.el_formato_pasaporte_invalido));
            return false;
        }

        // Validar que coincida con datos personales (opcional, según tu necesidad)
        if (!validarDatosEnPasaporte(pasaporte)) {
            tilCurp.setError(getString(R.string.el_pasaporte_no_coincide_datos));
            return false;
        }

        tilCurp.setError(null);
        return true;
    }

    private boolean validateDmvId() {
        String dmvId = etCurp.getText().toString().trim().toUpperCase();

        // Obtener datos personales para validación
        String nombre = etName.getText().toString().trim().toUpperCase();
        String apellidoP = etApellidoP.getText().toString().trim().toUpperCase();
        String fechaTexto = etFechaNacimiento.getText().toString();

        // Validaciones básicas del DMV ID
        if (dmvId.isEmpty()) {
            tilCurp.setError(getString(R.string.el_dmv_id_es_obligatorio));
            return false;
        } else if (dmvId.length() < 6 || dmvId.length() > 15) {
            tilCurp.setError(getString(R.string.el_dmv_id_debe_tener_entre_6_y_15_caracteres));
            return false;
        } else if (!dmvId.matches("^[A-Z0-9]{6,15}$")) {
            tilCurp.setError(getString(R.string.el_formato_dmv_id_invalido));
            return false;
        }

        // Validar que coincida con nombres
        if (!validarNombresEnDmvId(dmvId, nombre, apellidoP)) {
            tilCurp.setError(getString(R.string.el_dmv_id_no_coincide_nombres));
            return false;
        }

        // Validar que coincida con la fecha de nacimiento
        if (!validarFechaEnDmvId(dmvId, fechaTexto)) {
            tilCurp.setError(getString(R.string.el_dmv_id_no_coincide_fecha));
            return false;
        }

        tilCurp.setError(null);
        return true;
    }
    private boolean validarNombresEnDmvId(String dmvId, String nombre, String apellidoP) {
        // Validar que el DMV ID contenga referencias a los nombres
        // Esto puede variar según el formato específico de tu región

        // Ejemplo: verificar que contenga las primeras letras del nombre y apellido
        if (nombre.length() > 0 && apellidoP.length() > 0) {
            String primeraLetraNombre = nombre.substring(0, 1);
            String primeraLetraApellido = apellidoP.substring(0, 1);

            if (!dmvId.contains(primeraLetraNombre) && !dmvId.contains(primeraLetraApellido)) {
                return false;
            }
        }

        return true;
    }

    private boolean validarFechaEnDmvId(String dmvId, String fechaTexto) {
        // Validar que el DMV ID contenga referencias a la fecha
        // Esto puede variar según el formato específico

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date fechaNacimiento = sdf.parse(fechaTexto);
            Calendar cal = Calendar.getInstance();
            cal.setTime(fechaNacimiento);

            int año = cal.get(Calendar.YEAR) % 100; // Últimos 2 dígitos
            int mes = cal.get(Calendar.MONTH) + 1;
            int dia = cal.get(Calendar.DAY_OF_MONTH);

            // Buscar patrones de fecha en el DMV ID
            String patronDia = String.format("%02d", dia);
            String patronMes = String.format("%02d", mes);
            String patronAño = String.format("%02d", año);

            if (dmvId.contains(patronAño) || dmvId.contains(patronMes) || dmvId.contains(patronDia)) {
                return true;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean validarDatosEnPasaporte(String pasaporte) {
        // Aquí puedes implementar validaciones específicas del pasaporte
        // Por ejemplo, verificar formato de país, dígitos de control, etc.

        // Ejemplo básico: verificar que empiece con letra (para pasaportes internacionales)
        if (!Character.isLetter(pasaporte.charAt(0))) {
            return false;
        }

        return true;
    }

    public boolean validateUsaIds() {
        boolean pasaporteValido = validatePasaporte();
        boolean dmvIdValido = validateDmvId();

        return pasaporteValido && dmvIdValido;
    }

    private boolean validateAllFields1() {
        boolean nameValid = validateName();
        boolean apellidoPValid = validateApellidoP();
        boolean apellidoMValid = true;
        if(isMexico())
            apellidoMValid = validateApellidoM();
        boolean generoValid = validateGenero();
        boolean bdValid = validateBd();

        return nameValid && apellidoPValid && apellidoMValid && generoValid && bdValid;
    }
    private boolean validateAllFields2() {
        boolean telefonoValid = validateTelefono();
        boolean validateCurp = isMexico()? validateCurp() : validateUsaIds();
        boolean validateCorreo= validateCorreo();
        boolean validateContrasena = validateContrasena();

        return telefonoValid && validateCurp && validateCorreo && validateContrasena;
    }
    private void contenidoPag1(){
        //Validaciones
        setUpFieldValidation(etName, this::validateName);
        setUpFieldValidation(etApellidoP, this::validateApellidoP);
        setUpFieldValidation(etApellidoM, this::validateApellidoM);
        setUpFieldValidation(etFechaNacimiento, this::validateBd);
        setUpFieldValidation(etGenero, this::validateGenero);
    }
    private void contenidoPag2(){
        //REGÍSTRATE PT2

        QuestionnaireDialogFragment dialog = new QuestionnaireDialogFragment();
        backRegistro2.setOnClickListener(v -> {
            cambiarPantalla(pRegistro2, pRegistro1); // Usa tu función reutilizable
            tilTelefono.setError(null);
            tilCurp.setError(null);
            tilCorreo.setError(null);
            tilContrasena.setError(null);
        });
        btnRegistro3.setOnClickListener(v -> {
            if (validateAllFields2() && checkboxTerminos.isChecked()) {
                // Imprimir variables en consola
                Log.d("REGISTRO", "Datos del usuario:");
                Log.d("REGISTRO", "Email: " + correoCampo);
                //Log.d("REGISTRO", "Contraseña: " + contrasenaCampo); // Cuidado: No deberías loggear contraseñas en producción
                Log.d("REGISTRO", "Nombre: " + nombreCampo);
                Log.d("REGISTRO", "Primer apellido: " + apellidoPCampo);
                Log.d("REGISTRO", "Segundo apellido: " + apellidoMCampo);
                Log.d("REGISTRO", "Teléfono: " + telefonoCampo);
                Log.d("REGISTRO", "CURP: " + curpCampo);
                Log.d("REGISTRO", "Fecha nacimiento: " + fechaCampo);
                Log.d("REGISTRO", "Género: " + generoCampo);

                registerUser(
                        correoCampo,                        // email
                        contrasenaCampo,                    // password
                        nombreCampo,                        // nombre
                        apellidoPCampo,                     // primer apellido
                        apellidoMCampo,                     // segundo apellido
                        "+52"+telefonoCampo,                // teléfono
                        curpCampo,                          // CURP
                        fechaCampo,                         //"1969-12-11T00:00:00.000Z",// fecha nacimiento
                        generoCampo                         // género
                );
                dialog.show(getSupportFragmentManager(), "QuestionnaireDialog");
            }
        });
        setUpFieldValidation(etTelefono, this::validateTelefono);
        if(isMexico()){
            setUpFieldValidation(etCurp, this::validateCurp);
        }
        else{
            setUpFieldValidation(etCurp, this::validateUsaIds);
        }
        setUpFieldValidation(etCorreo, this::validateCorreo);
        setUpFieldValidation(etContrasena, this::validateContrasena);
    }
    private boolean validarFormatoCURP(String curp) {
        String regex = "^[A-Z]{4}[0-9]{6}[HM][A-Z]{5}[A-Z0-9]{2}$";
        return curp.matches(regex);
    }
    private void updateLanguage(){
        SharedPreferences prefs = this.getSharedPreferences("app", Context.MODE_PRIVATE);
        Integer language = prefs.getInt("language", 0); // "es" como idioma por defecto
        String[] languages = {"en", "es"}; // Array de valores numéricos
        String selectedValue = languages[language];
        LocaleHelper.setLocale(this, selectedValue);
        this.recreate();
    }

    // .............. AUXILIARES ...................

    // 🤖 ATAJOS
    private SharedPreferences setPrefs(String sharedNames){
        return getSharedPreferences(sharedNames, MODE_PRIVATE);
    }
    private void guardarPaisSeleccionado(String pais, Integer position) {
        SharedPreferences prefs = getSharedPreferences("app", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("country", pais);
        editor.apply();

        //Cambiar Idioma
        String[] languages = {"en", "es"};
        String languageSelected = languages[position];

        editor.putInt("language", position);
        editor.apply();

        // Cambiar idioma en toda la app
        LocaleHelper.setLocale(this, languageSelected);

        /*FragmentManager fragmentManager = this.getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        // Reinicia la Activity principal para que tome el idioma
        Intent intent = this.getIntent();
        this.finish();
        this.startActivity(intent);*/
    }
    private void guardarAceptacionTerminos(boolean accepted) {
        SharedPreferences prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("termsAccepted", accepted);
        editor.apply();
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
    private void cambiarPantalla(LinearLayout pantallaOculta, LinearLayout pantallaVisible) {
        pantallaOculta.setVisibility(View.GONE);
        pantallaVisible.setVisibility(View.VISIBLE);
        // Animación de desvanecimiento
        pantallaVisible.setAlpha(0f);
        pantallaVisible.animate().alpha(1f).setDuration(300).start();
    }
    private void setUpFieldValidation(View inputView, Supplier<Boolean> validator) {
        // Limpiar error al obtener el foco
        inputView.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                clearError(inputView);
            } else {
                validator.get();
            }
        });

        // Limpiar error durante la edición (para EditText y AutoCompleteTextView)
        if (inputView instanceof TextView) {
            ((TextView) inputView).addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    clearError(inputView);
                }
                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }
    private void clearError(View inputView) {
        if (inputView instanceof TextInputEditText) {
            TextInputLayout parent = (TextInputLayout) inputView.getParent().getParent();
            parent.setError(null);
            parent.setErrorEnabled(false);
        }
        else if (inputView instanceof AutoCompleteTextView) {
            ((AutoCompleteTextView) inputView).setError(null);
        }
    }
    // DIALOGOS
    private void mostrarDialogoTerminos() {
        if (!termsAccepted())
        {
            SharedPreferences prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
            String paisActual = prefs.getString("country", "");

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.terminos_condiciones))
                    .setMessage(getString(R.string.por_favor_acepta_terminos))
                    .setCancelable(false) // No permitir cerrar tocando fuera
                    .setPositiveButton(getString(R.string.aceptar), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            guardarAceptacionTerminos(true);
                            if(paisActual.equals("")){
                                mostrarDialogoPaises();
                            }
                        }
                    })
                    .setNegativeButton(getString(R.string.rechazar), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            guardarAceptacionTerminos(false);
                            // Si rechaza, cerrar la aplicación
                            finish();
                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();

            // Opcional: Personalizar el color del botón negativo
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    AlertDialog dialog = (AlertDialog) dialogInterface;
                    Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                    negativeButton.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                }
            });
        }
    }
    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR) - 18; // Por defecto 18 años atrás
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Formatear fecha seleccionada
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(selectedYear, selectedMonth, selectedDay);

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    String formattedDate = sdf.format(selectedDate.getTime());

                    // Establecer texto y validar
                    TextInputEditText etFecha = findViewById(R.id.fechaNacimientoReg);
                    etFecha.setText(formattedDate);
                    validateBd(); // Validar inmediatamente
                },
                year,
                month,
                day
        );

        // Establecer fecha máxima (hoy)
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }
    private void mostrarDialogoPaises() {
        final String[] paises = getResources().getStringArray(R.array.spinner_optionsCountries);

        SharedPreferences prefs = getSharedPreferences("app", MODE_PRIVATE);
        String paisActual = prefs.getString("country", "");
        int checkedItem = -1;

        // Debug: ver qué valores tenemos
        Log.d("DialogoPaises", "País actual en prefs: '" + paisActual + "'");
        Log.d("DialogoPaises", "Opciones disponibles: " + Arrays.toString(paises));

        // Búsqueda más robusta (case insensitive y trim)
        for (int i = 0; i < paises.length; i++) {
            if (paises[i].equalsIgnoreCase(paisActual.trim())) {
                checkedItem = i;
                break;
            }
        }

        Log.d("DialogoPaises", "Índice seleccionado: " + checkedItem);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona tu país")
                .setCancelable(false)
                .setSingleChoiceItems(paises, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String paisSeleccionado = paises[which];
                        Integer p = 0;

                        // MEJORA: Usar equals() en lugar de ==
                        if("USA".equals(paisSeleccionado)) {
                            p = 0; // en
                        } else if("México".equals(paisSeleccionado)) {
                            p = 1; // es
                        }
                        guardarPaisSeleccionado(paisSeleccionado, p);
                        actualizarIdioma(paisSeleccionado);
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
private void actualizarIdioma(String pais) {
    Locale locale;
    if ("USA".equals(pais)) {
        locale = new Locale("en");
    } else if ("México".equals(pais)) {
        locale = new Locale("es");
    } else {
        locale = Locale.getDefault();
    }

    Locale.setDefault(locale);
    Configuration config = new Configuration();
    config.setLocale(locale);
    getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    recreate();
}


    private void showScrollablePopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflar un layout con ScrollView
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_scrollable, null);

        builder.setView(dialogView)
                .setNeutralButton("Cerrar", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}