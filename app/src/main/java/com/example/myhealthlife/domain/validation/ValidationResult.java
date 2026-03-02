package com.example.myhealthlife.domain.validation;

public class ValidationResult {

    public boolean isValid;
    public String errorMessage;

    private ValidationResult(boolean isValid, String errorMessage) {
        this.isValid = isValid;
        this.errorMessage = errorMessage;
    }

    public static ValidationResult ok() {
        return new ValidationResult(true, null);
    }

    public static ValidationResult error(String msg) {
        return new ValidationResult(false, msg);
    }
}
