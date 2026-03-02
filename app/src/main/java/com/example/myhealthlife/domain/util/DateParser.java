package com.example.myhealthlife.domain.util;

import java.time.LocalDate;
import java.util.Date;

public interface DateParser {
    Date parse(String rawDate);
}