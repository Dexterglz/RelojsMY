package com.example.myhealthlife.domain.model;

public class Name {

    private final String firstName;
    private final String lastName;
    private final String secondLastName;

    public Name(String firstName, String lastName, String secondLastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.secondLastName = secondLastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getSecondLastName() {
        return secondLastName;
    }
}
