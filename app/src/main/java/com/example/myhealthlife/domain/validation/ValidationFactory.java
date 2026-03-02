package com.example.myhealthlife.domain.validation;

import android.widget.EditText;

import com.example.myhealthlife.R;
import com.example.myhealthlife.domain.RegisterForm;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ValidationFactory {

    public static CountryValidationRules getRules(String countryCode) {

        switch (countryCode) {
            case "MX":
                return new MexicoValidationRules();
            case "US":
                return new UsaValidationRules();
            default:
                throw new IllegalArgumentException("Country not supported");
        }
    }
}
