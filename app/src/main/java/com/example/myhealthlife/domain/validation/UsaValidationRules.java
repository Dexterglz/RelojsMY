package com.example.myhealthlife.domain.validation;

import com.example.myhealthlife.domain.RegisterForm;

public class UsaValidationRules implements CountryValidationRules {

    @Override
    public ValidationResult validate(RegisterForm form) {

        if (form.nombre.isEmpty())
            return ValidationResult.error("First name required");

        // En USA no hay apellido materno obligatorio
        if (form.apellidoPaterno.isEmpty())
            return ValidationResult.error("Last name required");

        if (form.personalID == null || form.personalID.length() < 6)
            return ValidationResult.error("Invalid passport");

        return ValidationResult.ok();
    }
}
