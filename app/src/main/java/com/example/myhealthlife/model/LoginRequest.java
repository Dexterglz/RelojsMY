package com.example.myhealthlife.model;

public class LoginRequest {
    private String email;    // Deben coincidir con los nombres esperados por el API
    private String password;

    // Constructor, getters y setters (importantes para Gson)
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getters y Setters (opcional, pero recomendado)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}