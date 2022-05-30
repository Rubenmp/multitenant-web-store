package com.mws.back_end.framework;

import java.util.Random;

public class TestUtils {
    private static final Random rand = new Random();

    protected static Long getRandomLong() {
        return rand.nextLong();
    }
}
