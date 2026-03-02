package com.example.myhealthlife.domain.validation.policy;

import android.util.Log;

import com.example.myhealthlife.domain.common.register.DomainError;
import com.example.myhealthlife.domain.common.register.RegistrationResult;
import com.example.myhealthlife.domain.model.RegisterUserData;
import com.example.myhealthlife.domain.validation.validator.AgeValidator;
import com.example.myhealthlife.domain.validation.validator.CurpValidator;
import com.example.myhealthlife.domain.validation.validator.EmailValidator;
import com.example.myhealthlife.domain.validation.validator.NameValidator;
import com.example.myhealthlife.domain.validation.validator.PasswordValidator;
import com.example.myhealthlife.domain.validation.validator.UsPassportValidator;

public class USARegistrationPolicy implements RegistrationPolicy {
    private final NameValidator nameValidator;
    private final EmailValidator emailValidator;
    private final PasswordValidator passwordValidator;
    private final AgeValidator ageValidator;
    private final UsPassportValidator personalIdValidator;

    public USARegistrationPolicy(
            NameValidator nameValidator,
            EmailValidator emailValidator,
            PasswordValidator passwordValidator,
            AgeValidator ageValidator,
            UsPassportValidator personalIdValidator
    )
    {
        this.nameValidator = nameValidator;
        this.emailValidator = emailValidator;
        this.passwordValidator = passwordValidator;
        this.ageValidator = ageValidator;
        this.personalIdValidator = personalIdValidator;
    }

    @Override
    public RegistrationResult validate(RegisterUserData data) {
        Log.d("REGISTER_UC", "USA");

        if(!nameValidator.requiresLastName(data.getName())){
            Log.d("REGISTER_UC", "apellido paterno inválido");
            return new RegistrationResult.Error(DomainError.INVALID_SECOND_LAST_N);
        }

        if (!emailValidator.isValid(data.getEmail())) {
            Log.d("REGISTER_UC", "email inválido");

            return new RegistrationResult.Error(DomainError.INVALID_EMAIL);
        }

        if (!passwordValidator.isValid(data.getPassword())) {
            Log.d("REGISTER_UC", "password inválido");

            return new RegistrationResult.Error(DomainError.INVALID_PASSWORD);
        }

        if (!ageValidator.isValid(data.getBirthDate())) {
            Log.d("REGISTER_UC", "bd inválido");

            return new RegistrationResult.Error(DomainError.INVALID_AGE);
        }

        if (!personalIdValidator.isValid(data.getPersonalId())) {
            Log.d("REGISTER_UC", "personal id inválido");

            return new RegistrationResult.Error(DomainError.INVALID_PERSONAL_ID);
        }

        return new RegistrationResult.Success();
    }
}
