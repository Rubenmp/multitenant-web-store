package com.mws.back_end.framework.utils;

import com.mws.back_end.framework.exception.MWSRException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class ExceptionUtilsTest {

    @Test
    void require() {
        try {
            ExceptionUtils.require(true, "msg");
        } catch (MWSRException e) {
            fail("It should not throw exception");
        }
    }

    @Test
    void require_throwException() {
        boolean exceptionThrown = false;
        try {
            ExceptionUtils.require(false, "msg");
        } catch (MWSRException e) {
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown, "Exception must be thrown");
    }

    @Test
    void requireNotNull() {
        try {
            ExceptionUtils.requireNotNull(new Object(), "msg");
        } catch (MWSRException e) {
            fail("It should not throw exception");
        }
    }

    @Test
    void requireNotNull_throwException() {
        boolean exceptionThrown = false;
        try {
            ExceptionUtils.requireNotNull(null, "msg");
        } catch (MWSRException e) {
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown, "Exception must be thrown");
    }

}
