package com.example.myhealthlife.data.remote.mapper;

import com.example.myhealthlife.data.remote.RegisterRequest;
import com.example.myhealthlife.domain.model.BirthDate;
import com.example.myhealthlife.domain.model.Gender;
import com.example.myhealthlife.domain.model.RegisterUserData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class RegisterRequestMapper {

    public RegisterRequest toRequest(RegisterUserData data) {

        RegisterRequest.Usuario usuario =
                new RegisterRequest.Usuario(
                        3, // FK_ROL → constante o config
                        data.getPassword().getValue(),
                        data.getEmail().getValue(),
                        formatDate(data.getBirthDate()),
                        mapGender(data.getGender()),
                        data.getName().getFirstName(),
                        data.getName().getLastName(),
                        data.getName().getSecondLastName(),
                        data.getPhoneNumber().getValue(),
                        data.getPersonalId().getValue()
                );

        return new RegisterRequest(usuario);
    }

    private String formatDate(BirthDate birthDate) {

        SimpleDateFormat iso =
                new SimpleDateFormat(
                        "yyyy-MM-dd'T'00:00:00.000'Z'",
                        Locale.US
                );

        iso.setTimeZone(TimeZone.getTimeZone("UTC"));

        return iso.format(birthDate.getValue());
    }

    private String mapGender(Gender gender) {
        return gender == Gender.M ? "1" : "0";
    }
}
