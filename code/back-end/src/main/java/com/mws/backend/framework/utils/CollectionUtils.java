package com.mws.backend.framework.utils;

import java.util.Collection;

public class CollectionUtils {
    private CollectionUtils() {}

    public static boolean isEmpty(final Collection<Object> collection) {
        return collection == null || collection.isEmpty();
    }
}

