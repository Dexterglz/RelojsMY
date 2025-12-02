package com.example.myhealthlife.model;


import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    @SerializedName("usuario")
    private Usuario usuario;

    public RegisterRequest(Usuario usuario) {
        this.usuario = usuario;
    }

    // Getters y setters
}
