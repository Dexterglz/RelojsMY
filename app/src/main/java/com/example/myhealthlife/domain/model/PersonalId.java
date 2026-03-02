package com.example.myhealthlife.domain.model;

public final class PersonalId {

    private final String value;

    public PersonalId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("PersonalId cannot be empty");
        }
        this.value = value.trim();
    }

    public String getValue() {
        return value;
    }
}
