package com.example.myhealthlife.domain.validation.validator;

import com.example.myhealthlife.domain.model.BirthDate;

public interface AgeValidator {
    boolean isValid(BirthDate birthDate);
}