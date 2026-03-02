package com.example.myhealthlife.domain.validation.validator;

import com.example.myhealthlife.domain.model.Email;

public class EmailValidator {

    public boolean isValid(Email email) {
        if (email == null || email.getValue() == null) return false;

        return email.getValue()
                .matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }
}
