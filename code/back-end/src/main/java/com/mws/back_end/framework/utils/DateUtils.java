package com.mws.back_end.framework.utils;

import java.util.Date;

public class DateUtils {

    private DateUtils() {}

    public static boolean isDateBeforeNow(final Date date) {
        final Date currentDate = new Date();
        return date == null || date.before(currentDate);
    }
}

