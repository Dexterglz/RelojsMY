package com.example.myhealthlife.domain;

import android.provider.ContactsContract;

import com.example.myhealthlife.domain.model.BirthDate;
import com.example.myhealthlife.domain.model.Email;
import com.example.myhealthlife.domain.model.Gender;
import com.example.myhealthlife.domain.model.Name;
import com.example.myhealthlife.domain.model.Password;
import com.example.myhealthlife.domain.model.PersonalId;
import com.example.myhealthlife.domain.model.PhoneNumber;
import com.example.myhealthlife.domain.model.RegisterUserData;
import com.example.myhealthlife.domain.util.DateParser;


public class RegisterFormMapper {

    private final DateParser dateParser;

    public RegisterFormMapper(DateParser dateParser) {
        this.dateParser = dateParser;
    }

    public RegisterUserData toDomain(RegisterForm form) {

        return new RegisterUserData(
                new Email(form.email),
                new Password(form.password),
                new Name(
                        form.nombre,
                        form.apellidoPaterno,
                        form.apellidoMaterno
                ),
                new PersonalId(form.personalID),
                new BirthDate(
                        dateParser.parse(form.fecha)
                ),
                form.genero ? Gender.M : Gender.F,
                new PhoneNumber(form.telefono),
                form.countryCode
        );
    }
}
