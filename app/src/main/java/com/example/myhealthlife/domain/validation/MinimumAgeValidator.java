package com.example.myhealthlife.domain.validation;

import com.example.myhealthlife.domain.model.BirthDate;
import com.example.myhealthlife.domain.validation.validator.AgeValidator;

import java.util.Calendar;
import java.util.Date;

public class MinimumAgeValidator implements AgeValidator {

    private final int minimumAge;

    public MinimumAgeValidator(int minimumAge) {
        this.minimumAge = minimumAge;
    }

    @Override
    public boolean isValid(BirthDate birthDate) {
        Date birth = birthDate.getValue();

        Calendar today = Calendar.getInstance();
        Calendar birthCal = Calendar.getInstance();
        birthCal.setTime(birth);

        int age = today.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < birthCal.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        return age >= minimumAge;
    }
}
