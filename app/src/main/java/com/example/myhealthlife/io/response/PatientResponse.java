package com.example.myhealthlife.io.response;

import com.google.gson.annotations.SerializedName;

public class PatientResponse {
    @SerializedName("ID_USUARIO")
    private String id_usuario;

    @SerializedName("FL_FECHANACIMIENTO")
    private String fecha_nacimiento;

    public String getId_usuario() {
        return id_usuario;
    }

    public String getFecha_nacimiento() {
        return fecha_nacimiento;
    }

}
