package com.example.myhealthlife.io.response;

import com.google.gson.annotations.SerializedName;

public class UserApiModel {

    @SerializedName("ID_USUARIO")
    private String idUsuario;

    @SerializedName("FL_NOMBRE")
    private String nombre;

    @SerializedName("FL_PRIMERAPELLIDO")
    private String primerApellido;

    @SerializedName("FL_SEGUNDOAPELLIDO")
    private String segundoApellido;

    @SerializedName("FL_FECHANACIMIENTO")
    private String fechaNacimiento;

    @SerializedName("FL_GENERO")
    private boolean genero;

    @SerializedName("FL_CORREO")
    private String correo;

    @SerializedName("FL_URL")
    private String fotoUrl;

    public String getNombre() {
        return nombre;
    }

    public String getPrimerApellido() {
        return primerApellido;
    }

    public String getSegundoApellido() {
        return segundoApellido;
    }

    public String getCorreo() {
        return correo;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public boolean isGenero() {
        return genero;
    }
}

