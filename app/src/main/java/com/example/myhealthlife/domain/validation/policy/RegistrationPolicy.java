package com.example.myhealthlife.domain.validation.policy;

import com.example.myhealthlife.domain.common.register.RegistrationResult;
import com.example.myhealthlife.domain.model.RegisterUserData;

public interface RegistrationPolicy {
    RegistrationResult validate(RegisterUserData userData);
}