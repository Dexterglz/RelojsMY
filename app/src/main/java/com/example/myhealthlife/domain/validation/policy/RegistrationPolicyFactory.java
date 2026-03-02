package com.example.myhealthlife.domain.validation.policy;

import com.example.myhealthlife.domain.validation.validator.AgeValidator;
import com.example.myhealthlife.domain.validation.validator.CurpValidator;
import com.example.myhealthlife.domain.validation.validator.EmailValidator;
import com.example.myhealthlife.domain.validation.validator.NameValidator;
import com.example.myhealthlife.domain.validation.validator.PasswordValidator;
import com.example.myhealthlife.domain.validation.validator.PersonalIdValidator;
import com.example.myhealthlife.domain.validation.validator.UsPassportValidator;

public class RegistrationPolicyFactory {

    private final NameValidator nameValidator;
    private final EmailValidator emailValidator;
    private final PasswordValidator passwordValidator;
    private final AgeValidator ageValidator;
    private final CurpValidator curpValidator;
    private final UsPassportValidator passportValidator;
    private final PersonalIdValidator genericIdValidator;

    public RegistrationPolicyFactory(
            NameValidator nameValidator,
            EmailValidator emailValidator,
            PasswordValidator passwordValidator,
            AgeValidator ageValidator,
            CurpValidator curpValidator,
            UsPassportValidator passportValidator,
            PersonalIdValidator genericIdValidator
    ) {
        this.nameValidator = nameValidator;
        this.emailValidator = emailValidator;
        this.passwordValidator = passwordValidator;
        this.ageValidator = ageValidator;
        this.curpValidator = curpValidator;
        this.passportValidator = passportValidator;
        this.genericIdValidator = genericIdValidator;
    }

    public RegistrationPolicy forCountry(String countryCode) {
        switch (countryCode) {
            case "MX":
                return new MexicoRegistrationPolicy(
                        nameValidator,
                        emailValidator,
                        passwordValidator,
                        ageValidator,
                        curpValidator
                );
            case "US":
                return new USARegistrationPolicy(
                        nameValidator,
                        emailValidator,
                        passwordValidator,
                        ageValidator,
                        passportValidator
                );
            default:
                return new GenericRegistrationPolicy(
                        /*emailValidator,
                        passwordValidator*/
                );
        }
    }
}
