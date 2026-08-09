package com.community.util;

public final class DataMaskingUtils {
    private DataMaskingUtils() { }

    public static String phone(String value) {
        return value == null || value.length() < 7 ? value : value.substring(0, 3) + "****" + value.substring(value.length() - 4);
    }

    public static String idCard(String value) {
        return value == null || value.length() < 10 ? value : value.substring(0, 6) + "********" + value.substring(value.length() - 4);
    }
}
