package com.tracebill.util;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

@Component
public class FinancialYearUtil {

    public String resolve(LocalDate date) {
        int year = date.getYear();
        if (date.getMonthValue() < 4) {
            return (year - 1) + "-" + String.valueOf(year).substring(2);
        }
        return year + "-" + String.valueOf(year + 1).substring(2);
    }
}
