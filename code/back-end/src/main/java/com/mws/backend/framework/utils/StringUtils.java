package com.mws.backend.framework.utils;

public class StringUtils {
    private StringUtils() {}

    public static boolean isEmpty(final String string) {
        return string == null || string.isEmpty();
    }
}

