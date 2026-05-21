package com.community.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    private DateUtils() {
    }

    public static final String PATTERN_DATETIME = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_DATE = "yyyy-MM-dd";
    public static final String PATTERN_TIME = "HH:mm:ss";

    public static String format(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String formatNow(String pattern) {
        return format(LocalDateTime.now(), pattern);
    }

    public static LocalDateTime parse(String str, String pattern) {
        if (StrUtils.isBlank(str)) {
            return null;
        }
        return LocalDateTime.parse(str, DateTimeFormatter.ofPattern(pattern));
    }

    public static LocalDateTime getStartOfDay(LocalDate date) {
        return date.atStartOfDay();
    }

    public static LocalDateTime getEndOfDay(LocalDate date) {
        return date.atTime(23, 59, 59);
    }
}
