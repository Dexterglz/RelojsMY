package com.example.myhealthlife.domain.validation;

import com.example.myhealthlife.domain.RegisterForm;

public interface CountryValidationRules {
    ValidationResult validate(RegisterForm form);
}
