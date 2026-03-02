package com.example.myhealthlife.ui.register;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myhealthlife.domain.RegisterForm;
import com.example.myhealthlife.domain.RegisterFormMapper;
import com.example.myhealthlife.domain.RegisterUiState;
import com.example.myhealthlife.domain.common.register.DomainError;
import com.example.myhealthlife.domain.common.register.RegistrationResult;
import com.example.myhealthlife.domain.model.RegisterUserData;
import com.example.myhealthlife.domain.usecase.RegisterUserUseCase;
import com.example.myhealthlife.domain.validation.CountryValidationRules;
import com.example.myhealthlife.domain.validation.ValidationFactory;
import com.example.myhealthlife.domain.validation.ValidationResult;

public class RegisterViewModel extends ViewModel {

    private final RegisterUserUseCase registerUserUseCase;
    private final RegisterFormMapper formMapper;

    private final MutableLiveData<RegisterUiState> uiState =
            new MutableLiveData<>(new RegisterUiState.Idle());

    public LiveData<RegisterUiState> getUiState() {
        return uiState;
    }

    public RegisterViewModel(
            RegisterUserUseCase registerUserUseCase,
            RegisterFormMapper formMapper
    ) {
        this.registerUserUseCase = registerUserUseCase;
        this.formMapper = formMapper;
    }

    // 🔑 ESTO ES LO QUE FALTABA
    public void register(RegisterForm form) {
        Log.d("REGISTER_VM", "register() llamado");

        uiState.setValue(new RegisterUiState.Loading());

        RegisterUserData domainData;

        try {
            domainData = formMapper.toDomain(form);
            Log.d("REGISTER_VM", "Form mapeado correctamente");
        }
        catch (IllegalArgumentException e) {
            Log.e("REGISTER_VM", "Error en mapper", e);
            uiState.setValue(
                    new RegisterUiState.Error(
                            DomainError.INVALID_DATA
                    )
            );
            return;
        }

        registerUserUseCase.execute(domainData, result -> {

            if (result == null) {
                Log.e("REGISTER_VM", "RegistrationResult = null");
                uiState.postValue(
                        new RegisterUiState.Error(DomainError.UNKNOWN_ERROR)
                );
                return;
            }

            if (result instanceof RegistrationResult.Success) {
                Log.d("REGISTER_VM", "SUCCESS");
                uiState.postValue(new RegisterUiState.Success());
            }
            else if (result instanceof RegistrationResult.Error) {

                DomainError error =
                        ((RegistrationResult.Error) result).getError();

                Log.e("REGISTER_VM", "ERROR: " + error);

                uiState.postValue(
                        RegisterUiStateMapper.fromDomain(error)
                );
            }
        });

    }
}