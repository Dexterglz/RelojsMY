package com.example.myhealthlife.domain.model;

import java.time.LocalDate;
import java.util.Date;

public final class BirthDate {

    private final Date value;

    public BirthDate(Date value) {
        this.value = value;
    }

    public Date getValue() {
        return value;
    }
}