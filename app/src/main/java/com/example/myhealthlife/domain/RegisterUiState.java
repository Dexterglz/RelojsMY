package com.example.myhealthlife.domain;

import com.example.myhealthlife.domain.common.register.DomainError;

public interface RegisterUiState {

    final class Idle implements RegisterUiState {}

    final class Loading implements RegisterUiState {}

    final class Success implements RegisterUiState {}

    final class Error implements RegisterUiState {
        private final DomainError error;

        public Error(DomainError error) {
            this.error = error;
        }

        public DomainError getError() {
            return error;
        }
    }
}