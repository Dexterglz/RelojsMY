package com.example.myhealthlife.domain.validation.validator;

import com.example.myhealthlife.domain.model.PersonalId;

import java.util.regex.Pattern;

public class CurpValidator implements PersonalIdValidator {

    private static final Pattern CURP_PATTERN =
            Pattern.compile("^[A-Z]{4}\\d{6}[HM][A-Z]{5}[A-Z0-9]\\d$");

    @Override
    public boolean isValid(PersonalId personalId) {
        return CURP_PATTERN.matcher(personalId.getValue()).matches();
    }
}
