package com.example.myhealthlife.model;

import android.text.TextUtils;
import android.util.Patterns;
import java.util.regex.Pattern;

public class ValidatorUtil {

    // Validar email
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    // Validar teléfono (México 10 dígitos)
    public static boolean isValidPhone(String phone) {
        return phone.matches("^[0-9]{10}$");
    }

    // Validar CURP (expresión regular básica)
    public static boolean isValidCurp(String curp) {
        Pattern pattern = Pattern.compile("^[A-Z]{4}[0-9]{6}[A-Z]{6}[0-9A-Z]{2}$");
        return pattern.matcher(curp).matches();
    }

    // Validar contraseña (mínimo 8 caracteres, al menos una mayúscula y un número)
    public static boolean isValidPassword(String password) {
        Pattern pattern = Pattern.compile("^(?=.*[0-9])(?=.*[A-Z]).{8,}$");
        return pattern.matcher(password).matches();
    }
}
