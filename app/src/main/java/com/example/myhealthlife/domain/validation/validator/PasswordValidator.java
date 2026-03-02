package com.example.myhealthlife.domain.validation.validator;

import com.example.myhealthlife.domain.model.Password;

public class PasswordValidator {
    private static final String PASSWORD_PATTERN =
            "^(?=.*[0-9])" +                    // Al menos un dígito
                    "(?=.*[a-z])" +            // Al menos una minúscula
                    "(?=.*[A-Z])" +            // Al menos una mayúscula
                    "(?=.*[@#$%^&+=!])" +      // Al menos un carácter especial
                    "(?=\\S+$)" +              // Sin espacios
                    ".{8,}$";                  // Mínimo 8 caracteres


    public boolean isValid(Password password) {
        if (password == null || password.getValue() == null) return false;

        return password.getValue()
                .matches(PASSWORD_PATTERN);
    }
}
