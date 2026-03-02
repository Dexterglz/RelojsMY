package com.example.myhealthlife.domain.usecase;
import com.example.myhealthlife.domain.common.register.RegistrationResult;
import com.example.myhealthlife.domain.model.RegisterUserData;

import com.example.myhealthlife.domain.repository.register.RegisterRepository;
import com.example.myhealthlife.domain.validation.policy.RegistrationPolicy;
import com.example.myhealthlife.domain.validation.policy.RegistrationPolicyFactory;

public class RegisterUserUseCase {

    private final RegisterRepository repository;
    private final RegistrationPolicyFactory policyFactory;

    public RegisterUserUseCase(
            RegisterRepository repository,
            RegistrationPolicyFactory policyFactory
    ) {
        this.repository = repository;
        this.policyFactory = policyFactory;
    }

    public void execute(
            RegisterUserData data,
            RegisterRepository.ResultCallback callback
    ) {

        RegistrationPolicy policy =
                policyFactory.forCountry(data.getCountryCode());

        RegistrationResult validationResult = policy.validate(data);

        if (validationResult instanceof RegistrationResult.Error) {
            callback.onResult(validationResult);
            return;
        }

        repository.register(data, callback);
    }
}