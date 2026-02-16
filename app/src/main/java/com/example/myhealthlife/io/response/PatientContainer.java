package com.example.myhealthlife.io.response;

import com.google.gson.annotations.SerializedName;

// Clase contenedora para mapear el JSON completo
public class PatientContainer {

    @SerializedName("user")
    private UserApiModel user;

    // Este es el método que necesitas
    public UserApiModel getUser() {
        return user;
    }

    public void setUser(UserApiModel user) {
        this.user = user;
    }
}


