package com.example.myhealthlife.ui.register;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhealthlife.data.error.RemoteErrorMapper;
import com.example.myhealthlife.data.remote.mapper.RegisterRequestMapper;
import com.example.myhealthlife.domain.DefaultDateParser;
import com.example.myhealthlife.domain.RegisterFormMapper;
import com.example.myhealthlife.domain.RegisterRepositoryImpl;
import com.example.myhealthlife.domain.model.PersonalId;
import com.example.myhealthlife.domain.repository.register.RegisterRepository;
import com.example.myhealthlife.domain.usecase.RegisterUserUseCase;
import com.example.myhealthlife.domain.validation.MinimumAgeValidator;
import com.example.myhealthlife.domain.validation.policy.GenericRegistrationPolicy;
import com.example.myhealthlife.domain.validation.policy.RegistrationPolicyFactory;
import com.example.myhealthlife.domain.validation.validator.AgeValidator;
import com.example.myhealthlife.domain.validation.validator.CurpValidator;
import com.example.myhealthlife.domain.validation.validator.EmailValidator;
import com.example.myhealthlife.domain.validation.validator.NameValidator;
import com.example.myhealthlife.domain.validation.validator.PasswordValidator;
import com.example.myhealthlife.domain.validation.validator.PersonalIdValidator;
import com.example.myhealthlife.domain.validation.validator.UsPassportValidator;
import com.example.myhealthlife.io.response.ApiClient;
import com.example.myhealthlife.io.response.ApiService;

import org.apache.commons.lang3.time.DateParser;
import org.jspecify.annotations.NonNull;

public class RegisterViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        PersonalIdValidator personalIdValidator =
                new CurpValidator();

        RegisterRepository repository =
                new RegisterRepositoryImpl(
                        ApiClient.newClient().create(ApiService.class),
                        new RemoteErrorMapper(),
                        new RegisterRequestMapper()
                );

        RegistrationPolicyFactory policyFactory =
                new RegistrationPolicyFactory(
                        new NameValidator(),
                        new EmailValidator(),
                        new PasswordValidator(),
                        new MinimumAgeValidator(18),
                        new CurpValidator(),
                        new UsPassportValidator(),
                        personalIdValidator
                );

        RegisterUserUseCase useCase =
                new RegisterUserUseCase(
                        repository,
                        policyFactory
                );

        RegisterFormMapper mapper =
                new RegisterFormMapper(new DefaultDateParser());

        return (T) new RegisterViewModel(useCase, mapper);
    }
}
