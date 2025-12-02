package com.example.myhealthlife.io.response;

import com.google.gson.annotations.SerializedName;

// Clase contenedora para mapear el JSON completo
public class PatientContainer {

    @SerializedName("user")
    private PatientResponse user;

    // Este es el método que necesitas
    public PatientResponse getUser() {
        return user;
    }

    public void setUser(PatientResponse user) {
        this.user = user;
    }
}


