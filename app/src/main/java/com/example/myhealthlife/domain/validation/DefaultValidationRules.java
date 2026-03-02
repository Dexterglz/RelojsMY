package com.example.myhealthlife.domain.validation;

import com.example.myhealthlife.domain.RegisterForm;

public class DefaultValidationRules implements CountryValidationRules {

    @Override
    public ValidationResult validate(RegisterForm form) {

        if (form.nombre.isEmpty())
            return ValidationResult.error("Nombre requerido");

        return ValidationResult.ok();
    }
}
