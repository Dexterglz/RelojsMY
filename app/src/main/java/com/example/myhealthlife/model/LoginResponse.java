package com.example.myhealthlife.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("access")  // ← Este nombre debe coincidir con el JSON
    private String token;

    @SerializedName("success")
    private boolean success;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}