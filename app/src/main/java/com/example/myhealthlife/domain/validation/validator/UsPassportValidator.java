package com.example.myhealthlife.domain.validation.validator;

import com.example.myhealthlife.domain.model.PersonalId;

import java.util.regex.Pattern;

public class UsPassportValidator implements PersonalIdValidator {

    private static final Pattern PASSPORT_PATTERN =
            Pattern.compile("^[A-Z0-9]{9}$");

    @Override
    public boolean isValid(PersonalId personalId) {
        if (personalId == null || personalId.getValue() == null) {
            return false;
        }

        return PASSPORT_PATTERN
                .matcher(personalId.getValue().toUpperCase())
                .matches();
    }

}
