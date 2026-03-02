package com.example.myhealthlife.ui.register;

import com.example.myhealthlife.domain.RegisterUiState;
import com.example.myhealthlife.domain.common.register.DomainError;

public final class RegisterUiStateMapper {

    private RegisterUiStateMapper() {
    }

    public static RegisterUiState fromDomain(DomainError error) {

        switch (error) {

            case INVALID_EMAIL:
                return new RegisterUiState.Error(
                        DomainError.INVALID_EMAIL
                );

            case INVALID_PASSWORD:
                return new RegisterUiState.Error(
                        DomainError.INVALID_PASSWORD
                );

            case INVALID_AGE:
                return new RegisterUiState.Error(
                        DomainError.INVALID_AGE
                );

            case INVALID_PERSONAL_ID:
                return new RegisterUiState.Error(
                        DomainError.INVALID_PERSONAL_ID
                );


            default:
                return new RegisterUiState.Error(
                        DomainError.INVALID_DATA
                );
        }
    }
}
