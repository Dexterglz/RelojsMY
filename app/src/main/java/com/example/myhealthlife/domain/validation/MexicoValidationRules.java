package com.example.myhealthlife.domain.validation;

import com.example.myhealthlife.domain.RegisterForm;

public class MexicoValidationRules implements CountryValidationRules {

    @Override
    public ValidationResult validate(RegisterForm form) {

        if (form.nombre.isEmpty())
            return ValidationResult.error("Nombre requerido");

        if (form.apellidoPaterno.isEmpty())
            return ValidationResult.error("Apellido paterno requerido");

        if (form.personalID == null || form.personalID.length() != 18)
            return ValidationResult.error("CURP inválida");

        return ValidationResult.ok();
    }
}
