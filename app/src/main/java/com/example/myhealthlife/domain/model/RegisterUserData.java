package com.example.myhealthlife.domain.model;

public class RegisterUserData {

    private final Email email;
    private final Password password;
    private final Name name;
    private final PersonalId personalId;
    private final BirthDate birthDate;
    private final Gender gender;
    private final PhoneNumber phoneNumber;
    private final String countryCode;

    public RegisterUserData(
            Email email,
            Password password,
            Name name,
            PersonalId personalId,
            BirthDate birthDate,
            Gender gender,
            PhoneNumber phoneNumber,
            String countryCode
    ) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.personalId = personalId;
        this.birthDate = birthDate;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.countryCode = countryCode;
    }

    public BirthDate getBirthDate() {
        return birthDate;
    }

    public Email getEmail() {
        return email;
    }

    public Gender getGender() {
        return gender;
    }

    public Password getPassword() {
        return password;
    }

    public PersonalId getPersonalId() {
        return personalId;
    }

    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    public Name getName() {
        return name;
    }

    public String getCountryCode() {
        return countryCode;
    }
}

