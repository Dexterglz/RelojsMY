package com.example.myhealthlife.data.remote;


import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class RegisterRequest {

    @SerializedName("usuario")
    private Usuario usuario;

    public RegisterRequest(Usuario usuario) {
        this.usuario = usuario;
    }

    public static class Usuario {

        @SerializedName("FK_ROL")
        private int rol;

        @SerializedName("FL_CONTRASENA")
        private String password;

        @SerializedName("FL_CORREO")
        private String email;

        @SerializedName("FL_FECHANACIMIENTO")
        private String birthDate;

        @SerializedName("FL_GENERO")
        private String gender;

        @SerializedName("FL_NOMBRE")
        private String firstName;

        @SerializedName("FL_PRIMERAPELLIDO")
        private String lastName;

        @SerializedName("FL_SEGUNDOAPELLIDO")
        private String secondLastName;

        @SerializedName("FL_TELEFONO")
        private String phone;

        @SerializedName("FL_CURP")
        private String personalId;

        public Usuario(
                int rol,
                String password,
                String email,
                String birthDate,
                String gender,
                String firstName,
                String lastName,
                String secondLastName,
                String phone,
                String personalId
        ) {
            this.rol = rol;
            this.password = password;
            this.email = email;
            this.birthDate = birthDate;
            this.gender = gender;
            this.firstName = firstName;
            this.lastName = lastName;
            this.secondLastName = secondLastName;
            this.phone = phone;
            this.personalId = personalId;
        }
    }
}