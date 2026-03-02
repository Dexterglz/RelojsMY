package com.example.myhealthlife.domain.common.register;

public interface RegistrationResult {

    final class Success implements RegistrationResult {
    }

    final class Error implements RegistrationResult {
        private final DomainError error;

        public Error(DomainError error) {
            this.error = error;
        }

        public DomainError getError() {
            return error;
        }
    }
}
