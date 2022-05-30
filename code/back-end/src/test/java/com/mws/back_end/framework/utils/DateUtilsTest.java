package com.mws.back_end.framework.utils;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DateUtilsTest {

    @Test
    void isDateBeforeNow() {
        Date date = new Date();
        date.setSeconds(date.getSeconds() - 1);
        assertTrue(DateUtils.isDateBeforeNow(date));

        date.setDate(date.getDate() - 1);
        assertTrue(DateUtils.isDateBeforeNow(date));
    }

    @Test
    void isDateBeforeNow_tomorrow_false() {
        Date date = new Date();
        date.setDate(date.getDate() + 1);
        assertFalse(DateUtils.isDateBeforeNow(date));
    }

}
