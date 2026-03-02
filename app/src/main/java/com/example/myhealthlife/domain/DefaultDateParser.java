package com.example.myhealthlife.domain;


import com.example.myhealthlife.domain.util.DateParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import com.example.myhealthlife.domain.util.DateParser;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DefaultDateParser implements DateParser {

    @Override
    public Date parse(String rawDate) {
        try {
            SimpleDateFormat sdf =
                    new SimpleDateFormat("dd/MM/yyyy", Locale.US);
            return sdf.parse(rawDate);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date", e);
        }
    }
}