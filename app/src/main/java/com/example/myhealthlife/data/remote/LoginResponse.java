package com.example.myhealthlife.data.remote;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("access")  // ← Este nombre debe coincidir con el JSON
    private String token;

    @SerializedName("success")
    private boolean success;

    public String getToken() {
        return token;
    }
}