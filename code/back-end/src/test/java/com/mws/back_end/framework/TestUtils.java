package com.mws.back_end.framework;

import java.util.Random;

public class TestUtils {
    protected static final Long TENANT_ID = 1L;

    private static final Random rand = new Random();


    protected static Long getRandomLong() {
        return rand.nextLong();
    }
}
