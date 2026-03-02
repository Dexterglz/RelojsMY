package com.example.myhealthlife.domain.validation.policy;

import com.example.myhealthlife.domain.common.register.DomainError;
import com.example.myhealthlife.domain.common.register.RegistrationResult;
import com.example.myhealthlife.domain.model.RegisterUserData;

public class GenericRegistrationPolicy implements RegistrationPolicy {

    @Override
    public RegistrationResult validate(RegisterUserData userData) {

        if (userData.getName().getLastName() == null) {
            return new RegistrationResult.Error(DomainError.INVALID_NAME);
        }

        return new RegistrationResult.Success();
    }
}
