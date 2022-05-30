package com.mws.back_end.framework.utils;

import com.mws.back_end.framework.exception.MWSRException;

public class ExceptionUtils {

    private ExceptionUtils() {
    }

    public static void require(final boolean condition, final String exceptionMessage) {
        if (!condition) {
            throw new MWSRException(exceptionMessage);
        }
    }

    public static void requireNotNull(final Object object, final String exceptionMessage) {
        require(object != null, exceptionMessage);
    }
}

