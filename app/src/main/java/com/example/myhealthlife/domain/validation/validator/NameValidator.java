package com.example.myhealthlife.domain.validation.validator;


import com.example.myhealthlife.domain.model.Name;

public class NameValidator {

    public boolean requiresLastName(Name name) {
        if (    name == null                ||
                name.getLastName() == null ||
                name.getLastName() == ""

        ) return false;

        return true;
    }
    public boolean requiresSecondLastName(Name name) {
        if (    name == null                ||
                name.getSecondLastName() == null ||
                name.getSecondLastName() == ""

        ) return false;

        return true;
    }
}
